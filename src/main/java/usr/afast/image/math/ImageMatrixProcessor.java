package usr.afast.image.math;

import org.jetbrains.annotations.NotNull;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.wrapped.WrappedImage;

public class ImageMatrixProcessor {

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
                    sum += xVector.getVector()[xRadius - dx] * image.getPixel(x + dx, y, borderHandling);
                }
                firstResult.setPixel(x, y, sum);
            }
        }
        WrappedImage result = new WrappedImage(image.getWidth(), image.getHeight());
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                double sum = 0;
                for (int dy = -yRadius; dy <= yRadius; dy++) {
                    sum += yVector.getVector()[yRadius - dy] * firstResult.getPixel(x, y + dy, borderHandling);
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
                        sum += matrix.getMatrix()[xRadius - dx][yRadius - dy] *
                               image.getPixel(x + dx, y + dy, borderHandling);
                    }
                }
                result.setPixel(x, y, sum);
            }
        }
        return result;
    }


}
