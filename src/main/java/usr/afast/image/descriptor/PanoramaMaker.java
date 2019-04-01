package usr.afast.image.descriptor;

import javafx.util.Pair;
import lombok.AllArgsConstructor;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import usr.afast.image.wrapped.Matrix;

import java.awt.image.BufferedImage;
import java.util.*;

import static usr.afast.image.util.ImageIO.getSaveFilePath;
import static usr.afast.image.util.ImageIO.write;

public class PanoramaMaker {
    private static final Random RANDOM = new Random("Captain Marsik".hashCode());
    private static List<Integer> indices;

    private static final double EPS = 10;

    public static BufferedImage makePanorama(BufferedImage imageA, BufferedImage imageB, Matching matching) {
        Matrix matrixA = Matrix.of(imageA);
        Matrix matrixB = Matrix.of(imageB);
        List<PointsPair> matches = matching.getPointsPairs();
        final int n = matches.size();
        indices = new ArrayList<>(n);
        for (int i = 0; i < n; i++) indices.add(i);
        int cnt = 1000;

        List<Pair<double[], double[]>> inliners = new LinkedList<>();
        Perspective foundPerspective = null;
        Perspective foundReversePerspective = null;

        List<Pair<double[], double[]>> pairs = new ArrayList<>(n);
        int w1 = matrixA.getWidth(), h1 = matrixA.getHeight();
        int w2 = matrixB.getWidth(), h2 = matrixB.getHeight();
        for (int i = 0; i < n; i++) {
            PointsPair cur = matches.get(i);
            pairs.add(new Pair<>(
                    new double[]{convertCoordinate(cur.getPointA().getX(), w1),
                            convertCoordinate(cur.getPointA().getY(), h1)},
                    new double[]{convertCoordinate(cur.getPointB().getX(), w2),
                            convertCoordinate(cur.getPointB().getY(), h2)}));
        }

        for (int voting = 0; voting < cnt; voting++) {
            Collections.shuffle(indices, RANDOM);
            List<Pair<double[], double[]>> currentMatch = new ArrayList<>(4);
            for (int i = 0; i < 4; i++) currentMatch.add(pairs.get(indices.get(i)));

            Perspective perspective = getPerspective(currentMatch);
            Perspective reversed = getReversePerspective(currentMatch);
            List<Pair<double[], double[]>> curOk = new LinkedList<>();
            int inline = 0;
            int inline2 = 0;
            for (int i = 0; i < n; i++) {
                Pair<double[], double[]> pair = pairs.get(i);
                double[] conv = perspective.apply(pair.getKey());
                double x0 = pair.getValue()[0], y0 = pair.getValue()[1];
                double x1 = conv[0], y1 = conv[1];

                double eps = Math.max(convertCoordinate(x0, w2) - convertCoordinate(x1, w2),
                        convertCoordinate(y0, h2) - convertCoordinate(y1, h2));
//                System.out.println(eps);
                if (eps < EPS) {
                    curOk.add(new Pair<>(pair.getKey(), pair.getValue()));
                    inline++;
                }
            }
            if (inliners.size() < curOk.size()) {
                inliners = curOk;
                foundPerspective = perspective;
                foundReversePerspective = reversed;
            }
            System.out.println("INLINE = " + inliners.size());
        }

        double minX = -1, maxX = 1;
        double minY = -1, maxY = 1;
        int[][] angles = new int[][]{
                {0, 0},
                {0, imageB.getHeight()},
                {imageB.getWidth(), 0},
                {imageB.getWidth(), imageB.getHeight()}
        };

        for (int[] angle : angles) {
            double[] point = foundReversePerspective.apply(new double[]{convertCoordinate(angle[0], imageB.getWidth()),
                    convertCoordinate(angle[1], imageB.getHeight())});
            minX = Math.min(minX, point[0]);
            minY = Math.min(minY, point[1]);
            maxX = Math.max(maxX, point[0]);
            maxY = Math.max(maxY, point[1]);
        }
        int minI = convertCoordinate(minX, w1);
        int maxI = convertCoordinate(maxX, w1);
        int minJ = convertCoordinate(minY, h1);
        int maxJ = convertCoordinate(maxY, h1);

        int nw = maxI - minI + 1, nh = maxJ - minJ + 1;

        BufferedImage result = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_RGB);

//        for (int i = 0; i < imageA.getWidth(); i++) {
//            for (int j = 0; j < imageA.getHeight(); j++) {
//                int x = i - minX, y = j - minY;
//                result.setRGB(x, y, imageA.getRGB(i, j));
//            }
//        }


        System.out.println(minX + " " + minY);

        for (int i = minI; i <= maxI; i++) {
            for (int j = minJ; j <= maxJ; j++) {
                double x = (1D * i - minI) / (maxI - minI) * (maxX - minX) + minX;
                double y = (1D * j - minJ) / (maxJ - minJ) * (maxY - minY) + minY;
                double[] xy = new double[]{x, y};

                int aX = convertCoordinate(xy[0], w1);
                int aY = convertCoordinate(xy[1], h1);
                if (aX >= 0 && aX < w1 && aY >= 0 && aY < h1) {
                    result.setRGB(i - minI, j - minJ, imageA.getRGB(aX, aY));
                }

                double[] nxt = foundPerspective.apply(xy);
                int nx = convertCoordinate(nxt[0], imageB.getWidth());
                int ny = convertCoordinate(nxt[1], imageB.getHeight());
                if (nx >= 0 && nx < imageB.getWidth() && ny >= 0 && ny < imageB.getHeight()) {
                    result.setRGB(i - minI, j - minJ, imageB.getRGB(nx, ny));
                }
            }
        }

//        write(getSaveFilePath("E:\\GitHub\\java-cv\\images\\cats\\tempp\\azaza.png", "VOTING"), result);

        return result;
    }

    private static double convertCoordinate(int coord, int size) {
        return (2D * coord - size) / size;
    }

    private static int convertCoordinate(double coord, int size) {
        return (int) ((coord * size + size) / 2);
    }

    private static Perspective getPerspective(List<Pair<double[], double[]>> currentMatch) {
        double[][] matrix = new double[8][9];
        for (int i = 0; i < 4; i++) {
            double[] xy1 = currentMatch.get(i).getKey();
            double[] xy2 = currentMatch.get(i).getValue();
            matrix[i * 2][0] = xy1[0];
            matrix[i * 2][1] = xy1[1];
            matrix[i * 2][2] = 1;
            matrix[i * 2 + 1][3] = xy1[0];
            matrix[i * 2 + 1][4] = xy1[1];
            matrix[i * 2 + 1][5] = 1;
            matrix[i * 2][6] = -xy1[0] * xy2[0];
            matrix[i * 2][7] = -xy1[1] * xy2[0];
            matrix[i * 2][8] = -xy2[0];
            matrix[i * 2 + 1][6] = -xy1[0] * xy2[1];
            matrix[i * 2 + 1][7] = -xy1[1] * xy2[1];
            matrix[i * 2 + 1][8] = -xy2[1];
        }
        RealMatrix apacheMatrix = MatrixUtils.createRealMatrix(matrix);
        RealMatrix transposed = apacheMatrix.transpose();
        RealMatrix M = transposed.multiply(apacheMatrix);

        SingularValueDecomposition svd = new SingularValueDecomposition(M);

        double[] singularValues = svd.getSingularValues();
        int minIndex = getMinIndex(singularValues);

        RealMatrix U = svd.getU();
        double[] h = U.getColumn(minIndex);

        double h22 = h[8];
        for (int i = 0; i < 9; i++) h[i] /= h22;
//        System.out.println(Arrays.toString(h));

        return new Perspective(h);
    }

    private static Perspective getReversePerspective(List<Pair<double[], double[]>> currentMatch) {
        List<Pair<double[], double[]>> reversed = new ArrayList<>(currentMatch.size());
        for (Pair<double[], double[]> cur : currentMatch) {
            reversed.add(new Pair<>(cur.getValue(), cur.getKey()));
        }
        return getPerspective(reversed);
    }

    @AllArgsConstructor
    static class Perspective {
        private RealMatrix matrix;

        Perspective(double[] buffer) {
            double[][] h = new double[3][3];
            int ptr = 0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    h[i][j] = buffer[ptr++];
                }
            }
//            matrix = new LUDecomposition(MatrixUtils.createRealMatrix(h)).getSolver().getInverse();
            matrix = MatrixUtils.createRealMatrix(h);
        }

        double[] apply(double[] xy) {
            double[][] coords = new double[3][1];
            coords[0][0] = xy[0];
            coords[1][0] = xy[1];
            coords[2][0] = 1;
            RealMatrix realMatrix = MatrixUtils.createRealMatrix(coords);
            realMatrix = matrix.multiply(realMatrix);
            double[][] buf = realMatrix.getData();
            return new double[]{buf[0][0], buf[1][0]};
        }
    }

    private static int getMinIndex(double[] doubles) {
        double min = Double.MAX_VALUE;
        int idx = -1;
        for (int i = 0; i < doubles.length; i++) {
            if (min > doubles[i]) {
                min = doubles[i];
                idx = i;
            }
        }
        return idx;
    }
}
