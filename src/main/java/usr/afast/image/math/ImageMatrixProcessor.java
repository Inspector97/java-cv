package usr.afast.image.math;

import org.jetbrains.annotations.NotNull;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.wrapped.WrappedImage;

public class ImageMatrixProcessor {

    public static WrappedImage processWithConvolution(@NotNull WrappedImage image,
                                                      @NotNull ConvolutionMatrix convolutionMatrix,
                                                      BorderHandling borderHandling) {
        WrappedImage result;
        if (convolutionMatrix.separable) {
            result = processSeparable(image, convolutionMatrix.getMatrix(), borderHandling);
        } else {
            result = processNonSeparable(image, convolutionMatrix.getMatrix(), borderHandling);
        }
        return result;
    }

    private static WrappedImage processSeparable(@NotNull WrappedImage image,
                                                 Matrix matrix,
                                                 BorderHandling borderHandling) {
        WrappedImage result = new WrappedImage(image.getWidth(), image.getHeight());

        return result;
    }

    private static WrappedImage processNonSeparable(@NotNull WrappedImage image,
                                                    Matrix matrix,
                                                    BorderHandling borderHandling) {
        WrappedImage result = new WrappedImage(image.getWidth(), image.getHeight());

        return result;
    }

}
