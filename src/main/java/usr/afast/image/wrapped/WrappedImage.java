package usr.afast.image.wrapped;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.DoubleFunction;

import static java.awt.image.BufferedImage.TYPE_BYTE_GRAY;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WrappedImage {
    private int width;
    private int height;

    @Getter(AccessLevel.NONE)
    private double[] buffer;

    public static WrappedImage of(@NotNull BufferedImage image) {
        WrappedImage wrappedImage = new WrappedImage();
        wrappedImage.width = image.getWidth();
        wrappedImage.height = image.getHeight();
        wrappedImage.buffer = new double[wrappedImage.width * wrappedImage.height];

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y));
                int gray = (int) (0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue());
                wrappedImage.setPixel(x, y, gray / 255D);
            }
        }
        return wrappedImage;
    }

    public WrappedImage(int width, int height) {
        if (width < 0 || height < 0)
            throw new IllegalArgumentException("Размер не может быть отрицательным");
        this.width = width;
        this.height = height;
        this.buffer = new double[width * height];
    }

    public BufferedImage save() {
        normalize();
        BufferedImage image = new BufferedImage(width, height, TYPE_BYTE_GRAY);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int gray = (int) Math.round(getPixel(x, y) * 255);
                Color color = new Color(gray, gray, gray);
                image.setRGB(x, y, color.getRGB());
            }
        }
        return image;
    }

    public void setPixel(int x, int y, double value) {
        buffer[getPosition(x, y)] = value;
    }

    public double getPixel(int x, int y) {
        return buffer[getPosition(x, y)];
    }

    public void forEach(DoubleFunction<Double> function) {
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = function.apply(buffer[i]);
        }
    }

    public static WrappedImage getGradient(@NotNull WrappedImage xImage, @NotNull WrappedImage yImage) {
        if (xImage.height != yImage.height || xImage.width != yImage.width)
            throw new IllegalArgumentException("Изображения разного размера");
        WrappedImage gradient = new WrappedImage(xImage.width, xImage.height);

        for (int i = 0; i < gradient.buffer.length; i++) {
            gradient.buffer[i] = Math.sqrt(sqr(xImage.buffer[i]) + sqr(yImage.buffer[i]));
        }

        return gradient;
    }

    @Contract(pure = true)
    private static double sqr(double value) {
        return value * value;
    }

    public void normalize() {
        double maxIntensity = 0, minIntensity = 1;
        double resultMaxIntensity = 1, resultMinIntensity = 0;
        for (double value : buffer) {
            maxIntensity = Math.max(maxIntensity, value);
            minIntensity = Math.min(minIntensity, value);
        }
        double coef = (resultMaxIntensity - resultMinIntensity) / (maxIntensity - minIntensity);
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (buffer[i] - minIntensity) * coef + resultMinIntensity;
        }
    }

    @Contract(pure = true)
    private int getPosition(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height)
            throw new IllegalArgumentException(String.format("Pixel position out of borders (%d, %d)", x, y));
        return x * height + y;
    }
}