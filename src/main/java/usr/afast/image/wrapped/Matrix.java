package usr.afast.image.wrapped;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import usr.afast.image.enums.BorderHandling;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.function.DoubleFunction;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

@SuppressWarnings("Duplicates")
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Matrix {
    private static final double BLACK = 0;
    private static final double WHITE = 1;
    private int width;
    private int height;

    @Getter(AccessLevel.NONE)
    private double[] buffer;

    public static Matrix of(@NotNull BufferedImage image) {
        Matrix matrix = new Matrix();
        matrix.width = image.getWidth();
        matrix.height = image.getHeight();
        matrix.buffer = new double[matrix.width * matrix.height];

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                double gray = 0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue();
                matrix.setAt(x, y, gray / 255);
            }
        }
        return matrix;
    }

    public static Matrix of(@NotNull double[][] buffer) {
        Matrix matrix = new Matrix();
        matrix.height = buffer.length;
        if (buffer.length > 0) {
            matrix.width = buffer[0].length;
        } else {
            matrix.width = 0;
        }
        matrix.buffer = new double[matrix.width * matrix.height];
        for (int i = 0; i < matrix.width; i++) {
            for (int j = 0; j < matrix.height; j++) {
                matrix.setAt(i, j, buffer[j][i]);
            }
        }
        return matrix;
    }

    public Matrix(@NotNull Matrix matrix) {
        width = matrix.width;
        height = matrix.height;
        buffer = new double[width * height];
        System.arraycopy(matrix.buffer, 0, buffer, 0, buffer.length);
    }

    public Matrix(int width, int height) {
        if (width < 0 || height < 0)
            throw new IllegalArgumentException("Размер не может быть отрицательным");
        this.width = width;
        this.height = height;
        this.buffer = new double[width * height];
    }

    public static BufferedImage save(Matrix matrix) {
        Matrix copied = normalize(matrix);
        BufferedImage image = new BufferedImage(copied.width, copied.height, TYPE_INT_RGB);
        for (int x = 0; x < copied.width; x++) {
            for (int y = 0; y < copied.height; y++) {
                int gray = (int) Math.round(copied.getAt(x, y) * 255);
                Color color = new Color(gray, gray, gray);
                image.setRGB(x, y, color.getRGB());
            }
        }
        return image;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public static Matrix transform(Matrix matrix) {
        Matrix transformed = new Matrix(matrix.height, matrix.width);
        for (int x = 0; x < matrix.width; x++) {
            for (int y = 0; y < matrix.height; y++) {
                transformed.setAt(y, x, matrix.getAt(x, y));
            }
        }
        return transformed;
    }

    public void setAt(int x, int y, double value) {
        buffer[getPosition(x, y)] = value;
    }

    public double getAt(int x, int y) {
        return buffer[getPosition(x, y)];
    }


    public double getAt(int x, int y, @NotNull BorderHandling borderHandling) {
        switch (borderHandling) {
            case Black:
                if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight())
                    return BLACK;
                return getAt(x, y);
            case White:
                if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight())
                    return WHITE;
                return getAt(x, y);
            case Copy:
                x = border(x, 0, getWidth() - 1);
                y = border(y, 0, getHeight() - 1);
                return getAt(x, y);
            case Wrap:
                x = (x + getWidth()) % getWidth();
                y = (y + getHeight()) % getHeight();
                return getAt(x, y);
            case Mirror:
                x = Math.abs(x);
                y = Math.abs(y);
                if (x >= getWidth()) x = getWidth() - (x - getWidth() + 1);
                if (y >= getHeight()) y = getHeight() - (y - getHeight() + 1);
                return getAt(x, y);
            default:
                return BLACK;
        }
    }

    @SuppressWarnings("SameParameterValue")
    private int border(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }


    public void forEach(DoubleFunction<Double> function) {
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = function.apply(buffer[i]);
        }
    }

    public static Matrix getGradient(@NotNull Matrix xImage, @NotNull Matrix yImage) {
        if (xImage.height != yImage.height || xImage.width != yImage.width)
            throw new IllegalArgumentException("Изображения разного размера");
        Matrix gradient = new Matrix(xImage.width, xImage.height);

        for (int i = 0; i < gradient.buffer.length; i++) {
            gradient.buffer[i] = Math.sqrt(sqr(xImage.buffer[i]) + sqr(yImage.buffer[i]));
        }

        return gradient;
    }

    public static Matrix getGradientAngle(@NotNull Matrix xImage, @NotNull Matrix yImage) {
        if (xImage.height != yImage.height || xImage.width != yImage.width)
            throw new IllegalArgumentException("Изображения разного размера");
        Matrix gradientAngle = new Matrix(xImage.width, xImage.height);

        for (int i = 0; i < gradientAngle.buffer.length; i++) {
            gradientAngle.buffer[i] = Math.atan2(yImage.buffer[i], xImage.buffer[i]);
        }

        return gradientAngle;
    }

    public static Matrix subtract(@NotNull Matrix aMatrix, @NotNull Matrix bMatrix) {
        if (aMatrix.height != bMatrix.height || aMatrix.width != bMatrix.width)
            throw new IllegalArgumentException("Изображения разного размера");
        Matrix result = new Matrix(aMatrix.width, aMatrix.height);
        for (int i = 0; i < result.width; i++) {
            for (int j = 0; j < result.height; j++) {
                result.setAt(i, j, aMatrix.getAt(i, j) - bMatrix.getAt(i, j));
            }
        }
        return result;
    }

    @Contract(pure = true)
    private static double sqr(double value) {
        return value * value;
    }

    public static Matrix normalize(Matrix matrix) {
        Matrix copied = new Matrix(matrix);
        double maxIntensity = 0, minIntensity = 1;
        double resultMaxIntensity = 1, resultMinIntensity = 0;
        for (double value : copied.buffer) {
            maxIntensity = Math.max(maxIntensity, value);
            minIntensity = Math.min(minIntensity, value);
        }
        double coef = (resultMaxIntensity - resultMinIntensity) / (maxIntensity - minIntensity);
        for (int i = 0; i < copied.buffer.length; i++) {
            copied.buffer[i] = (copied.buffer[i] - minIntensity) * coef + resultMinIntensity;
        }
        return copied;
    }

    @Contract(pure = true)
    private int getPosition(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height)
            throw new IllegalArgumentException(String.format("Pixel position out of borders (%d, %d)", x, y));
        return x * height + y;
    }

    public Matrix downSample() {
        int newWidth = width / 2;
        int newHeight = height / 2;
        Matrix result = new Matrix(newWidth, newHeight);
        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                result.setAt(i, j, getAt(i * 2, j * 2));
            }
        }
        return result;
    }

    public int hashCode() {
        return Arrays.hashCode(buffer);
    }
}
