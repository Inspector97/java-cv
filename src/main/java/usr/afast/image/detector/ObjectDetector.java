package usr.afast.image.detector;

import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.Contract;
import usr.afast.image.descriptor.Matching;
import usr.afast.image.descriptor.PointsPair;
import usr.afast.image.points.InterestingPoint;
import usr.afast.image.wrapped.Matrix;

import java.awt.*;
import java.awt.image.BufferedImage;

import static usr.afast.image.descriptor.BlobFinder.matchBlobs;
import static usr.afast.image.points.PointMarker.markMatching;
import static usr.afast.image.util.ImageIO.*;

public class ObjectDetector {
    public static void detect(String savePath, Matrix image, Matrix... objects) {
        Matrix object = objects[0];
        Matching matching = matchBlobs(image, object, savePath);

        BufferedImage withBlobs = markMatching(image, object, matching);
        write(getSaveFilePath(savePath, "MATCHING"), withBlobs);
        System.out.println("Matching found");

        double objectCenterX = object.getWidth() / 2D;
        double objectCenterY = object.getHeight() / 2D;

        Voting voting = new Voting(image.getWidth(), 30, image.getHeight(), 30, image.getWidth(), 30, Math.PI / 30);

        for (PointsPair pair : matching.getPointsPairs()) {
            InterestingPoint atImage = pair.getPointA();
            InterestingPoint atObject = pair.getPointB();

            double x = objectCenterX - atObject.getX();
            double y = objectCenterY - atObject.getY();
            double angle = atObject.getAngle(), cos = Math.cos(-angle), sin = Math.sin(-angle);

            double rotatedX = x * cos - y * sin, rotatedY = x * sin + y * cos;
            double scaledX = rotatedX / atObject.getScale(), scaledY = rotatedY / atObject.getScale();

            double objectScale = object.getWidth() / atObject.getScale();

            double resultUnscaledX = scaledX * atImage.getScale();
            double resultUnscaledY = scaledY * atImage.getScale();
            cos = Math.cos(atImage.getAngle());
            sin = Math.sin(atImage.getAngle());
            double resultX = atImage.getX() + resultUnscaledX * cos - resultUnscaledY * sin;
            double resultY = atImage.getY() + resultUnscaledX * sin + resultUnscaledY * cos;

            double votingAngle = atImage.getAngle() - atObject.getAngle();
            double votingScale = objectScale * atImage.getScale();

            voting.vote(resultX, resultY, votingScale, votingAngle);
        }

        voting.maximums(image, savePath, object.getWidth(), object.getHeight());
    }

    static class Voting {
        private double[][][][] votes;
        private int[][][][] votedPairs;
        private int widthBin, heightBin;
        private double angleBin, scaleBin;
        private int n, m, k, l;

        public Voting(int width, int widthBin, int height, int heightBin, double maxScale, double scaleBin, double angleBin) {
            n = width / widthBin + 1;
            m = height / heightBin + 1;
            k = (int) (maxScale / scaleBin + 1);
            l = (int) (2 * Math.PI / angleBin + 1);
            System.out.println(n + " " + m + " " + k + " " + l);
            votes = new double[n][m][k][l];
            votedPairs = new int[n][m][k][l];
            this.angleBin = angleBin;
            this.heightBin = heightBin;
            this.widthBin = widthBin;
            this.scaleBin = scaleBin;
        }

        public void vote(double x, double y, double scale, double angle) {
            angle = normalize(angle);
            double _x = x * 1D / widthBin;
            double _y = y * 1D / heightBin;
            double _scale = scale / scaleBin;
            double _angle = angle / angleBin;
//            System.out.println(x + " " + y + " " + scale + " " + angle);
            if (_x < 0 || _y < 0 || _scale < 0 || _angle < 0) return;
            if (_x >= n || _y >= m || _scale >= k || _angle >= l) return;
            for (int i = 0; i <= 1; i++) {
                int curX = i == 0 ? (int) _x : (int) _x + 1;
                if (curX >= this.n) continue;
                for (int j = 0; j <= 1; j++) {
                    int curY = j == 0 ? (int) _y : (int) _y + 1;
                    if (curY >= this.m) continue;
                    for (int k = 0; k <= 1; k++) {
                        int curScale = k == 0 ? (int) _scale : (int) _scale + 1;
                        if (curScale >= this.k) continue;
                        for (int l = 0; l <= 1; l++) {
                            int curAngle = l == 0 ? (int) _angle : (int) _angle + 1;
                            if (curAngle >= this.l) continue;
                            votes[curX][curY][curScale][curAngle] +=
                                    Math.abs(_x - curX) *
                                    Math.abs(_y - curY) *
                                    Math.abs(_scale - curScale) *
                                    Math.abs(_angle - curAngle);
//                            votedPairs[(int) _x][(int) _y][(int) _scale][(int) _angle]++;
                            votedPairs[curX][curY][curScale][curAngle]++;
                        }
                    }
                }
            }
        }


        @Contract(pure = true)
        private double normalize(double angle) {
            while (angle < 0)
                angle += Math.PI * 2;
            while (angle >= Math.PI * 2)
                angle -= Math.PI * 2;
            return angle;
        }


        public void maximums(Matrix matrix, String path, int objWidth, int objHeight) {
            BufferedImage bufferedImage = Matrix.save(matrix);
            Graphics2D graphics = bufferedImage.createGraphics();
            graphics.setStroke(new BasicStroke(2));
            graphics.setColor(Color.ORANGE);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    for (int k = 0; k < this.k; k++) {
                        for (int l = 0; l < this.l; l++) {
                            double val = votes[i][j][k][l];
                            if (votedPairs[i][j][k][l] < 4)
                                continue;
                            boolean ok = true;
                            for (int di = -1; di <= 1 && ok; di++) {
                                for (int dj = -1; dj <= 1 && ok; dj++) {
                                    for (int dk = -1; dk <= 1 && ok; dk++) {
                                        for (int dl = -1; dl <= 1 && ok; dl++) {
                                            if (di == 0 && dj == 0 && dk == 0 && dl == 0) continue;
                                            int ni = i + di, nj = j + dj, nk = k + dk, nl = l + dl;
                                            if (ni < 0 || nj < 0 || nk < 0 || nl < 0) continue;
                                            if (ni >= this.n || nj >= this.m || nk >= this.k || nl >= this.l)
                                                continue;
                                            ok = val > votes[ni][nj][nk][nl];
                                        }
                                    }
                                }
                            }
                            if (ok) {
                                System.out.println(i + " " + j + " " + k + " " + l);
                                double x = (i + 0.5) * widthBin, y = (j + 0.5) * heightBin;
                                double scale = (k + 0.5) * scaleBin;
                                double coef = scale / objWidth;
                                double w = objWidth * coef;
                                double h = objHeight * coef;
                                double angle = (l) * angleBin;
//                                graphics.drawRect((int) (x - w / 2), (int) (y - h / 2), (int) w, (int) h);
                                Pair<Double, Double>[] points = new Pair[]{
                                        new Pair(x - w / 2, y - h / 2),
                                        new Pair(x - w / 2, y + h / 2),
                                        new Pair(x + w / 2, y + h / 2),
                                        new Pair(x + w / 2, y - h / 2)
                                };
                                drawRectRotated(graphics, points, new Pair<>(x, y), angle);
                            }
                        }
                    }
                }
            }
            graphics.dispose();
            write(getSaveFilePath(path, "MAXS"), bufferedImage);
        }

        private void drawRectRotated(Graphics2D graphics, Pair<Double, Double>[] points,
                                     Pair<Double, Double> center, double angle) {
            Pair<Double, Double>[] rotated = new Pair[points.length];
            for (int i = 0; i < points.length; i++) {
                rotated[i] = rotate(points[i], center, angle);
            }
            for (int i = 0; i < rotated.length; i++) {
                int j = (i + 1) % rotated.length;
                graphics.drawLine(rotated[i].getFirst().intValue(),
                                  rotated[i].getSecond().intValue(),
                                  rotated[j].getFirst().intValue(),
                                  rotated[j].getSecond().intValue());
            }
        }

        private Pair<Double, Double> rotate(Pair<Double, Double> point, Pair<Double, Double> center, double angle) {
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double x = point.getFirst() - center.getFirst();
            double y = point.getSecond() - center.getSecond();
            double rx = x * cos - y * sin, ry = x * sin + y * cos;
            return new Pair<>(rx + center.getFirst(), ry + center.getSecond());
        }
    }
}
