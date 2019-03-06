package usr.afast.image.edge;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.wrapped.Matrix;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static usr.afast.image.algo.AlgoLib.*;
import static usr.afast.image.util.ImageIO.getSaveFilePath;
import static usr.afast.image.util.ImageIO.write;

public class Canny {

    private static Map<Angle, Shift> angleShiftMap;
    private static final double LOW_THRESHOLD = 0.05;
    private static final double HIGH_THRESHOLD = 0.25;

    static {
        angleShiftMap = new HashMap<>();
        angleShiftMap.put(Angle._90, new Shift(1, 0, -1, 0));
        angleShiftMap.put(Angle._135, new Shift(1, -1, -1, 1));
        angleShiftMap.put(Angle._0, new Shift(0, -1, 0, 1));
        angleShiftMap.put(Angle._45, new Shift(-1, -1, 1, 1 ));
    }

    public static Matrix canny(String path, @NotNull Matrix image) {
        int width = image.getWidth();
        int height = image.getHeight();
        Matrix blurred = makeGauss(image, 2, 1.4, BorderHandling.Mirror);

        Matrix xImage = getSobelX(blurred, BorderHandling.Mirror);
        Matrix yImage = getSobelY(blurred, BorderHandling.Mirror);

        Matrix gradient = Matrix.getGradient(xImage, yImage);
        gradient = Matrix.normalize(gradient);
        Angle[][] gradientDirection = getGradientDirection(xImage, yImage);

        write(getSaveFilePath(path, "TEMP_1"), gradient);

        Matrix nonMaximumSuppressed = suppressNonMaximum(width, height, gradient, gradientDirection);

        write(getSaveFilePath(path, "TEMP_2"), nonMaximumSuppressed);

        boolean[][] marked = new boolean[width][height];
        Queue<Point> queue = new LinkedList<>();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (isHigh(nonMaximumSuppressed.getAt(i, j))) {
                    marked[i][j] = true;
                    queue.add(Point.at(i, j));
                }
            }
        }

        while (!queue.isEmpty()) {
            Point cur = queue.poll();
            for (int dx = -2; dx <= 2; dx++) {
                for (int dy = -2; dy <= 2; dy++) {
                    int nx = cur.x + dx;
                    int ny = cur.y + dy;
                    if (nx < 0 || nx >= width || ny < 0 || ny >= height) continue;
                    if (isLow(nonMaximumSuppressed.getAt(nx, ny)) && !marked[nx][ny]) {
                        marked[nx][ny] = true;
                        queue.add(Point.at(nx, ny));
                    }
                }
            }
        }

        Matrix result = new Matrix(width, height);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (marked[i][j]) {
                    result.setAt(i, j, 1);
                }
            }
        }

        return result;
    }

    @Contract(pure = true)
    private static boolean isLow(double value) {
        return value > LOW_THRESHOLD;
    }

    @Contract(pure = true)
    private static boolean isHigh(double value) {
        return value > HIGH_THRESHOLD;
    }

    private static Matrix suppressNonMaximum(int width, int height, Matrix gradient, Angle[][] gradientDirection) {
        Matrix result = new Matrix(width, height);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double pixel = gradient.getAt(i, j);
                Shift shift = angleShiftMap.get(gradientDirection[i][j]);
                double pixelA = getPixel(gradient, i + shift.dx1, j + shift.dy1);
                double pixelB = getPixel(gradient, i + shift.dx2, j + shift.dy2);
                if (pixel > pixelA - 1e-3 && pixel > pixelB - 1e-3)
                    result.setAt(i, j, pixel);
            }
        }

        return result;
    }

    private static double getPixel(@NotNull Matrix gradient, int x, int y) {
        return gradient.getAt(x, y, BorderHandling.Mirror);
    }

    private static Angle[][] getGradientDirection(@NotNull Matrix xImage, Matrix yImage) {
        int width = xImage.getWidth();
        int height = xImage.getHeight();
        Angle[][] gradientDirection = new Angle[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                gradientDirection[i][j] = Angle.of(Math.atan2(yImage.getAt(i, j), xImage.getAt(i, j)));
            }
        }
        return gradientDirection;
    }

    enum Angle {
        _0, _45, _90, _135;

        @Contract(pure = true)
        static Angle of(double angle) {
            if (angle < 0)
                angle += Math.PI;
            angle = Math.toDegrees(angle);
            if (angle < 22.5 || angle > 157.5)
                return _0;
            if (angle <= 67.5)
                return _45;
            if (angle <= 112.5)
                return _90;
            return _135;
        }
    }

    @AllArgsConstructor
    @Getter
    private static class Shift {
        private int dx1;
        private int dy1;
        private int dx2;
        private int dy2;
    }

    @AllArgsConstructor(staticName = "at")
    @Getter
    private static class Point {
        private int x;
        private int y;
    }
}
