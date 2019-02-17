package usr.afast.image.math;

import org.jetbrains.annotations.NotNull;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.wrapped.WrappedImage;

public class ImageMatrixProcessor {
    private static final double BLACK = 0;
    private static final double WHITE = 1;

    public static WrappedImage processWithConvolution(@NotNull WrappedImage image,
                                                      @NotNull ConvolutionMatrix convolutionMatrix,
                                                      BorderHandling borderHandling) {
        WrappedImage result = null;
        if (convolutionMatrix instanceof SeparableConvolutionMatrix) {
            SeparableConvolutionMatrix separableConvolutionMatrix = (SeparableConvolutionMatrix) convolutionMatrix;
            result = processSeparable(image,
                                      separableConvolutionMatrix.getXVector(),
                                      separableConvolutionMatrix.getYVector(),
                                      borderHandling);
        }
        if (convolutionMatrix instanceof NonSeparableConvolutionMatrix) {
            result = processNonSeparable(image, ((NonSeparableConvolutionMatrix) convolutionMatrix).getMatrix(),
                                         borderHandling);
        }
        return result;
    }

    private static WrappedImage processSeparable(@NotNull WrappedImage image,
                                                 Vector xVector,
                                                 Vector yVector,
                                                 BorderHandling borderHandling) {
        WrappedImage firstResult = new WrappedImage(image.getWidth(), image.getHeight());
        int xRadius = (xVector.getLength() - 1) / 2;
        int yRadius = (yVector.getLength() - 1) / 2;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                double sum = 0;
                for (int dx = -xRadius; dx <= xRadius; dx++) {
                    sum += xVector.getVector()[dx + xRadius] * getPixel(image, x + dx, y, borderHandling);
                }
                firstResult.setPixel(x, y, sum);
            }
        }
        WrappedImage result = new WrappedImage(image.getWidth(), image.getHeight());
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                double sum = 0;
                for (int dy = -yRadius; dy <= yRadius; dy++) {
                    sum += yVector.getVector()[dy + yRadius] * getPixel(firstResult, x, y + dy, borderHandling);
                }
                result.setPixel(x, y, sum);
            }
        }
        return result;
    }

    private static WrappedImage processNonSeparable(@NotNull WrappedImage image,
                                                    @NotNull Matrix matrix,
                                                    BorderHandling borderHandling) {
        WrappedImage result = new WrappedImage(image.getWidth(), image.getHeight());
        int xRadius = (matrix.getWidth() - 1) / 2;
        int yRadius = (matrix.getHeight() - 1) / 2;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                double sum = 0;
                for (int dx = -xRadius; dx <= xRadius; dx++) {
                    for (int dy = -yRadius; dy <= yRadius; dy++) {
                        sum += matrix.getMatrix()[dx + xRadius][dy + yRadius] *
                               getPixel(image, x + dx, y + dy, borderHandling);
                    }
                }
                result.setPixel(x, y, sum);
            }
        }
        return result;
    }

    private static double getPixel(@NotNull WrappedImage image, int x, int y, @NotNull BorderHandling borderHandling) {
        switch (borderHandling) {
            case Black:
                if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight())
                    return BLACK;
                return image.getPixel(x, y);
            case White:
                if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight())
                    return WHITE;
                return image.getPixel(x, y);
            case Copy:
                x = border(x, 0, image.getWidth() - 1);
                y = border(y, 0, image.getHeight() - 1);
                return image.getPixel(x, y);
            case Wrap:
                x = (x + image.getWidth()) % image.getWidth();
                y = (y + image.getHeight()) % image.getHeight();
                return image.getPixel(x, y);
            case Mirror:
                x = Math.abs(x);
                y = Math.abs(y);
                if (x >= image.getWidth()) x = image.getWidth() - (x - image.getWidth() + 1);
                if (y >= image.getHeight()) y = image.getHeight() - (y - image.getHeight() + 1);
                return image.getPixel(x, y);
            default:
                return BLACK;
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static int border(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

}
