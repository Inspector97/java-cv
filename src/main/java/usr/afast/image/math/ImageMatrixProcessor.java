package usr.afast.image.math;

import org.jetbrains.annotations.NotNull;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.wrapped.Matrix;

public class ImageMatrixProcessor {

    public static Matrix processWithConvolution(@NotNull Matrix image,
                                                BorderHandling borderHandling,
                                                @NotNull Matrix... convolutions) {
        Matrix result = new Matrix(image);
        for (Matrix convolution : convolutions) {
            result = process(result, convolution, borderHandling);
        }
        return result;
    }

    private static Matrix process(@NotNull Matrix image,
                                  @NotNull Matrix convolution,
                                  BorderHandling borderHandling) {
        Matrix result = new Matrix(image.getWidth(), image.getHeight());
        int xRadius = (convolution.getWidth() - 1) / 2;
        int yRadius = (convolution.getHeight() - 1) / 2;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                double sum = 0;
                for (int dx = -xRadius; dx <= xRadius; dx++) {
                    for (int dy = -yRadius; dy <= yRadius; dy++) {
                        sum += convolution.getAt(xRadius - dx, yRadius - dy) *
                               image.getAt(x + dx, y + dy, borderHandling);
                    }
                }
                result.setAt(x, y, sum);
            }
        }
        return result;
    }


}
