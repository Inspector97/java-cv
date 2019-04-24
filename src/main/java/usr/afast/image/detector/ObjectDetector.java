package usr.afast.image.detector;

import javafx.util.Pair;
import org.jetbrains.annotations.Contract;
import usr.afast.image.descriptor.Matching;
import usr.afast.image.descriptor.PanoramaMaker;
import usr.afast.image.descriptor.PanoramaMaker.Point;
import usr.afast.image.descriptor.PointsPair;
import usr.afast.image.points.InterestingPoint;
import usr.afast.image.wrapped.Matrix;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static usr.afast.image.descriptor.BlobFinder.matchBlobs;
import static usr.afast.image.descriptor.PanoramaMaker.*;
import static usr.afast.image.points.PointMarker.markMatching;
import static usr.afast.image.util.ImageIO.*;

public class ObjectDetector {
    public static void detect(String savePath, Matrix[] images, Matrix[] objects, String[] imageNames,
                              String[] objectNames) {
        if (!savePath.endsWith("/") && !savePath.endsWith("\\"))
            savePath = savePath + "\\";
        Path withPath = Paths.get(savePath, "with");
        try {
            File file = new File(String.valueOf(withPath));
            if (file.exists()) deleteFolder(file);
            Files.createDirectory(withPath);
        } catch (IOException e) {
            //
        }
        Path withoutPath = Paths.get(savePath, "without");
        try {
            File file = new File(String.valueOf(withoutPath));
            if (file.exists()) deleteFolder(file);
            Files.createDirectory(withoutPath);
        } catch (IOException e) {
            //
        }
        Path matchPath = Paths.get(savePath, "matchings");
        try {
            File file = new File(String.valueOf(matchPath));
            if (file.exists()) deleteFolder(file);
            Files.createDirectory(matchPath);
        } catch (IOException e) {
            //
        }
        String withDir = withPath.toString() + "\\";
        String withoutDir = withoutPath.toString() + "\\";
        String matchDir = matchPath.toString() + "\\";
        for (int i = 0; i < images.length; i++) {
            Matrix image = images[i];
            for (int j = 0; j < objects.length; j++) {
                Matrix object = objects[j];
                Matching matching = matchBlobs(image, object, savePath);

                BufferedImage withBlobs = markMatching(image, object, matching);
                write(matchDir + imageNames[i] + " - " + objectNames[j] + " MATCHING.png", withBlobs);
                System.out.println("Matching found");

                double objectCenterX = object.getWidth() / 2D;
                double objectCenterY = object.getHeight() / 2D;

                Voting voting = new Voting(image.getWidth(), 50,
                                           image.getHeight(), 50,
                                           image.getWidth(), 30,
                                           Math.PI / 6);

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

                    voting.vote(resultX, resultY, votingScale, votingAngle, pair);
                }

                List<List<PointsPair>> candidates = voting.maximums(image, savePath, object.getWidth(), object.getHeight());
                int w1 = image.getWidth(), h1 = image.getHeight();
                int w2 = object.getWidth(), h2 = object.getHeight();
                BufferedImage bufferedImage = Matrix.save(image);
                Graphics2D graphics = bufferedImage.createGraphics();
                graphics.setColor(Color.ORANGE);
                graphics.setStroke(new BasicStroke(2));
                int rects = 0;
                for (List<PointsPair> match : candidates) {
                    int before = match.size();
                    List<Pair<Point, Point>> inliners = PanoramaMaker.getInliners(image, object, match);
                    if (inliners == null) continue;
                    if (inliners.size() < 5) continue;
                    System.out.println(before + " " + inliners.size());
                    PanoramaMaker.Perspective reversePerspective = getReversePerspective(inliners);
                    Point leftTop = Point.at(-1, -1);
                    leftTop = reversePerspective.apply(leftTop);
                    Point convertedLeftTop = Point.at(convertFrom(leftTop.getX(), w1),
                                                      convertFrom(leftTop.getY(), h1));
                    Point rightTop = Point.at(1, -1);
                    rightTop = reversePerspective.apply(rightTop);
                    Point convertedRightTop = Point.at(convertFrom(rightTop.getX(), w1),
                                                       convertFrom(rightTop.getY(), h1));
                    Point leftBottom = Point.at(-1, 1);
                    leftBottom = reversePerspective.apply(leftBottom);
                    Point convertedLeftBottom = Point.at(convertFrom(leftBottom.getX(), w1),
                                                         convertFrom(leftBottom.getY(), h1));
                    Point rightBottom = Point.at(1, 1);
                    rightBottom = reversePerspective.apply(rightBottom);
                    Point convertedRightBottom = Point.at(convertFrom(rightBottom.getX(), w1),
                                                          convertFrom(rightBottom.getY(), h1));
                    rects++;
                    drawRect(graphics, convertedLeftBottom, convertedLeftTop, convertedRightTop, convertedRightBottom);

                }
                System.out.println("Drawed " + rects + " rects");
                graphics.dispose();
                if (rects == 0) {
                    write(withoutDir + imageNames[i] + " - " + objectNames[j] + " MARKED.png", bufferedImage);
                } else {
                    write(withDir + imageNames[i] + " - " + objectNames[j] + " MARKED.png", bufferedImage);
                }
            }
        }
    }

    private static void drawRect(Graphics2D graphics, Point... points) {
        for (int i = 0; i < points.length; i++) {
            int j = (i + 1) % points.length;
            graphics.drawLine((int) points[i].getX(),
                              (int) points[i].getY(),
                              (int) points[j].getX(),
                              (int) points[j].getY());
        }
    }

    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    static class Voting {
        private double[][][][] votes;
        private List<PointsPair>[][][][] votedPairs;
        private int widthBin, heightBin;
        private double angleBin, scaleBin;
        private int n, m, k, l;

        public Voting(int width, int widthBin, int height, int heightBin, double maxScale, double scaleBin, double angleBin) {
            n = width / widthBin + 1;
            m = height / heightBin + 1;
            k = (int) (maxScale / scaleBin + 1);
            l = (int) Math.round(2 * Math.PI / angleBin);
            System.out.println(n + " " + m + " " + k + " " + l);
            votes = new double[n][m][k][l];
            votedPairs = new List[n][m][k][l];
            this.angleBin = angleBin;
            this.heightBin = heightBin;
            this.widthBin = widthBin;
            this.scaleBin = scaleBin;
        }

        public void vote(double x, double y, double scale, double angle, PointsPair pointsPair) {
            angle = normalize(angle);
            double _x = x * 1D / widthBin;
            double _y = y * 1D / heightBin;
            double _scale = scale / scaleBin;
            double _angle = angle / angleBin;
//            System.out.println(x + " " + y + " " + scale + " " + angle);
            if (_x < 0 || _y < 0 || _scale < 0 || _angle < 0) return;
            if (_x >= n || _y >= m || _scale >= k || _angle >= l) return;
            for (int i = 0; i <= 1; i++) {
                int curX = ((int) _x + i) % n;
                for (int j = 0; j <= 1; j++) {
                    int curY = ((int) _y + j) % m;
                    for (int k = 0; k <= 1; k++) {
                        int curScale = ((int) _scale + k) % this.k;
                        for (int l = 0; l <= 1; l++) {
                            int curAngle = ((int) _angle + l) % this.l;
                            votes[curX][curY][curScale][curAngle] +=
                                    Math.abs(_x - curX) *
                                    Math.abs(_y - curY) *
                                    Math.abs(_scale - curScale) *
                                    Math.abs(_angle - curAngle);
                            List<PointsPair> list = votedPairs[curX][curY][curScale][curAngle];
                            if (list == null) {
                                votedPairs[curX][curY][curScale][curAngle] = list = new LinkedList<>();
                            }
                            list.add(pointsPair);
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


        public List<List<PointsPair>> maximums(Matrix matrix, String path, int objWidth, int objHeight) {
            List<List<PointsPair>> lists = new LinkedList<>();
            BufferedImage bufferedImage = Matrix.save(matrix);
//            Graphics2D graphics = bufferedImage.createGraphics();
//            graphics.setStroke(new BasicStroke(2));
//            graphics.setColor(Color.ORANGE);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    for (int k = 0; k < this.k; k++) {
                        for (int l = 0; l < this.l; l++) {
                            double val = votes[i][j][k][l];
                            if (val < 2) continue;
                            if (votedPairs[i][j][k][l] == null || votedPairs[i][j][k][l].size() < 4)
                                continue;
                            boolean ok = true;
                            for (int di = -1; di <= 1 && ok; di++) {
                                for (int dj = -1; dj <= 1 && ok; dj++) {
                                    for (int dk = -1; dk <= 1 && ok; dk++) {
                                        for (int dl = -1; dl <= 1 && ok; dl++) {
                                            if (di == 0 && dj == 0 && dk == 0 && dl == 0) continue;
                                            int ni = (i + di + this.n) % this.n;
                                            int nj = (j + dj + this.m) % this.m;
                                            int nk = (k + dk + this.k) % this.k;
                                            int nl = (l + dl + this.l) % this.l;
                                            ok = val > votes[ni][nj][nk][nl];
                                        }
                                    }
                                }
                            }
                            if (ok) {
                                System.out.println(i + " " + j + " " + k + " " + l + " " + votedPairs[i][j][k][l].size() + " " + val);
                                double x = (i + 0.5) * widthBin, y = (j + 0.5) * heightBin;
                                double scale = (k + 0.5) * scaleBin;
                                double coef = scale / objWidth;
                                double w = objWidth * coef;
                                double h = objHeight * coef;
                                double angle = (l) * angleBin;
//                                graphics.drawRect((int) (x - w / 2), (int) (y - h / 2), (int) w, (int) h);
//                                Pair<Double, Double>[] points = new Pair[]{
//                                        new Pair(x - w / 2, y - h / 2),
//                                        new Pair(x - w / 2, y + h / 2),
//                                        new Pair(x + w / 2, y + h / 2),
//                                        new Pair(x + w / 2, y - h / 2)
//                                };
                                lists.add(votedPairs[i][j][k][l]);
//                                drawRectRotated(graphics, points, new Pair<>(x, y), angle);
                            }
                        }
                    }
                }
            }
//            graphics.dispose();
//            write(getSaveFilePath(path, "MAXS"), bufferedImage);
            return lists;
        }


        private Pair<Double, Double> rotate(Pair<Double, Double> point, Pair<Double, Double> center, double angle) {
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double x = point.getKey() - center.getKey();
            double y = point.getValue() - center.getValue();
            double rx = x * cos - y * sin, ry = x * sin + y * cos;
            return new Pair<>(rx + center.getKey(), ry + center.getValue());
        }
    }
}
