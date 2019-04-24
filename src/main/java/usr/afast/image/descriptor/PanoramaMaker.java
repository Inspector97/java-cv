package usr.afast.image.descriptor;

import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import usr.afast.image.wrapped.Matrix;

import java.awt.image.BufferedImage;
import java.util.*;

public class PanoramaMaker {
    private static final Random RANDOM = new Random("Captain Marsik".hashCode());
    private static List<Integer> indices;

    private static final double EPS = 2;

    public static BufferedImage makePanorama(BufferedImage imageA, BufferedImage imageB, Matching matching) {
        Matrix matrixA = Matrix.of(imageA);
        Matrix matrixB = Matrix.of(imageB);
        List<PointsPair> matches = matching.getPointsPairs();
        final int n = matches.size();
        Perspective foundPerspective = null;
        Perspective foundReversePerspective = null;
        int w1 = matrixA.getWidth(), h1 = matrixA.getHeight();
        int w2 = matrixB.getWidth(), h2 = matrixB.getHeight();

        List<Pair<Point, Point>> inliners = getInliners(matrixA, matrixB, matches);

        foundPerspective = getPerspective(inliners);
        foundReversePerspective = getReversePerspective(inliners);

        for (Pair<Point, Point> pair : inliners) {
            Point calculatedSecond = foundPerspective.apply(pair.getKey());
            Point calculatedFirst = foundReversePerspective.apply(pair.getValue());

            double eps1 = Math.max(Math.abs(convertFrom(calculatedSecond.getX(), w2) - convertFrom(pair.getValue().getX(), w2)),
                    Math.abs(convertFrom(calculatedSecond.getY(), h2) - convertFrom(pair.getValue().getY(), h2)));

            double eps2 = Math.max(Math.abs(convertFrom(calculatedFirst.getX(), w1) - convertFrom(pair.getKey().getX(), w1)),
                    Math.abs(convertFrom(calculatedFirst.getY(), h1) - convertFrom(pair.getKey().getY(), h1)));

            System.out.println(eps1 + " " + eps2);
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
            Point point = foundReversePerspective.apply(Point.at(convertTo(angle[0], imageB.getWidth()),
                    convertTo(angle[1], imageB.getHeight())));
            minX = Math.min(minX, point.getX());
            minY = Math.min(minY, point.getY());
            maxX = Math.max(maxX, point.getX());
            maxY = Math.max(maxY, point.getY());
        }
        int minI = convertFrom(minX, w1);
        int maxI = convertFrom(maxX, w1);
        int minJ = convertFrom(minY, h1);
        int maxJ = convertFrom(maxY, h1);

        int nw = maxI - minI + 1, nh = maxJ - minJ + 1;

        BufferedImage result = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_RGB);

        System.out.println(minX + " " + minY);

        for (int i = minI; i <= maxI; i++) {
            for (int j = minJ; j <= maxJ; j++) {
                double x = (1D * i - minI) * (maxX - minX) / (maxI - minI) + minX;
                double y = (1D * j - minJ) * (maxY - minY) / (maxJ - minJ) + minY;
                Point xy = Point.at(x, y);

                int aX = convertFrom(xy.getX(), w1);
                int aY = convertFrom(xy.getY(), h1);
                if (aX >= 0 && aX < w1 && aY >= 0 && aY < h1) {
                    result.setRGB(i - minI, j - minJ, imageA.getRGB(aX, aY));
                }

                Point nxt = foundPerspective.apply(xy);
                int nx = convertFrom(nxt.getX(), imageB.getWidth());
                int ny = convertFrom(nxt.getY(), imageB.getHeight());
                if (nx >= 0 && nx < imageB.getWidth() && ny >= 0 && ny < imageB.getHeight()) {
                    result.setRGB(i - minI, j - minJ, imageB.getRGB(nx, ny));
                }
            }
        }

        return result;
    }

    public static List<Pair<Point, Point>> getInliners(Matrix matrixA, Matrix matrixB, List<PointsPair> matches) {
        int cnt = 1000;
        final int n = matches.size();
        indices = new ArrayList<>(n);
        for (int i = 0; i < n; i++) indices.add(i);

        List<Pair<Point, Point>> inliners = new LinkedList<>();

        List<Pair<Point, Point>> pairs = new ArrayList<>(n);
        int w1 = matrixA.getWidth(), h1 = matrixA.getHeight();
        int w2 = matrixB.getWidth(), h2 = matrixB.getHeight();
        for (int i = 0; i < n; i++) {
            PointsPair cur = matches.get(i);
            pairs.add(new Pair<>(Point.at(convertTo(cur.getPointA().getX(), w1), convertTo(cur.getPointA().getY(), h1)),
                    Point.at(convertTo(cur.getPointB().getX(), w2), convertTo(cur.getPointB().getY(), h2))));
        }

        for (int voting = 0; voting < cnt; voting++) {
            Collections.shuffle(indices, RANDOM);
            List<Pair<Point, Point>> currentMatch = new ArrayList<>(4);
            for (int i = 0; i < 4; i++) currentMatch.add(pairs.get(indices.get(i)));

            Perspective perspective = getPerspective(currentMatch);
            List<Pair<Point, Point>> curOk = new LinkedList<>();
            for (int i = 0; i < n; i++) {
                Pair<Point, Point> pair = pairs.get(i);
                Point a = pair.getKey(), b = pair.getValue();
                Point conv = perspective.apply(pair.getKey());
                double x0 = b.getX(), y0 = b.getY();
                double x1 = conv.getX(), y1 = conv.getY();

                double eps = Math.max(Math.abs(convertFrom(x0, w2) - convertFrom(x1, w2)),
                        Math.abs(convertFrom(y0, h2) - convertFrom(y1, h2)));
                if (eps < EPS) {
                    curOk.add(new Pair<>(pair.getKey(), pair.getValue()));
                }
            }
            if (inliners.size() < curOk.size()) {
                inliners = new ArrayList<>(curOk);
            }
        }
        if (inliners.size() <= n / 2)
            return null;
        return inliners;
    }

    public static double convertTo(int coord, int size) {
//        return coord;
        return (2D * coord - size) / size;
    }

    public static int convertFrom(double coord, int size) {
//        return (int) coord;
        return (int) ((coord * size + size) / 2);
    }

    public static Perspective getPerspective(List<Pair<Point, Point>> currentMatch) {
        double[][] matrix = new double[currentMatch.size() * 2][9];
        for (int i = 0; i < currentMatch.size(); i++) {
            Point a = currentMatch.get(i).getKey();
            Point b = currentMatch.get(i).getValue();
            matrix[i * 2][0] = a.getX();
            matrix[i * 2][1] = a.getY();
            matrix[i * 2][2] = 1;
            matrix[i * 2 + 1][3] = a.getX();
            matrix[i * 2 + 1][4] = a.getY();
            matrix[i * 2 + 1][5] = 1;
            matrix[i * 2][6] = -a.getX() * b.getX();
            matrix[i * 2][7] = -a.getY() * b.getX();
            matrix[i * 2][8] = -b.getX();
            matrix[i * 2 + 1][6] = -a.getX() * b.getY();
            matrix[i * 2 + 1][7] = -a.getY() * b.getY();
            matrix[i * 2 + 1][8] = -b.getY();
        }
        RealMatrix AMatrix = MatrixUtils.createRealMatrix(matrix);
        RealMatrix transposed = AMatrix.transpose();
        RealMatrix M = transposed.multiply(AMatrix);

        SingularValueDecomposition svd = new SingularValueDecomposition(M);

        double[] singularValues = svd.getSingularValues();
        int minIndex = getMinIndex(singularValues);

        RealMatrix U = svd.getU();
        double[] h = U.getColumn(minIndex);

        double h22 = h[8];
        for (int i = 0; i < 9; i++) h[i] /= h22;

        return new Perspective(h);
    }

    public static Perspective getReversePerspective(List<Pair<Point, Point>> currentMatch) {
        List<Pair<Point, Point>> reversed = new ArrayList<>(currentMatch.size());
        for (Pair<Point, Point> cur : currentMatch) {
            reversed.add(new Pair<>(cur.getValue(), cur.getKey()));
        }
        return getPerspective(reversed);
    }

    @Getter
    @AllArgsConstructor(staticName = "at")
    public static class Point {
        private double x;
        private double y;
    }

    @AllArgsConstructor
    public static class Perspective {
        private RealMatrix matrix;

        Perspective(double[] buffer) {
            double[][] h = new double[3][3];
            int ptr = 0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    h[i][j] = buffer[ptr++];
                }
            }
            matrix = MatrixUtils.createRealMatrix(h);
        }

        public Point apply(Point point) {
            double[][] coords = new double[3][1];
            coords[0][0] = point.getX();
            coords[1][0] = point.getY();
            coords[2][0] = 1;
            RealMatrix realMatrix = MatrixUtils.createRealMatrix(coords);
            realMatrix = matrix.multiply(realMatrix);
            double[][] buf = realMatrix.getData();
            double scale = buf[2][0];
            return Point.at(buf[0][0] / scale, buf[1][0] / scale);
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
