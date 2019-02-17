package usr.afast.image.math;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ConvolutionMatrixFactory {
    private static final double[][] SOBEL_MATRIX = {{1, 0, -1}, {2, 0, -2}, {1, 0, -1}};
    private static final double[][] PRUITT_MATRIX = {{1, 0, -1}, {1, 0, -1}, {1, 0, -1}};
    private static final double[][] SCHARR_MATRIX = {{3, 0, -3}, {10, 0, -10}, {3, 0, -3}};

    @Contract(" -> new")
    @NotNull
    public static ConvolutionMatrix getSobelXMatrix() {
        return new ConvolutionMatrix(Matrix.ofSquare(SOBEL_MATRIX), false);
    }

    @Contract(" -> new")
    @NotNull
    public static ConvolutionMatrix getSobelYMatrix() {
        return new ConvolutionMatrix(Matrix.ofSquare(SOBEL_MATRIX).reverse(), false);
    }

    @Contract(" -> new")
    @NotNull
    public static ConvolutionMatrix getPruittXMatrix() {
        return new ConvolutionMatrix(Matrix.ofSquare(PRUITT_MATRIX), false);
    }

    @Contract(" -> new")
    @NotNull
    public static ConvolutionMatrix getPruittYMatrix() {
        return new ConvolutionMatrix(Matrix.ofSquare(PRUITT_MATRIX).reverse(), false);
    }

    @Contract(" -> new")
    @NotNull
    public static ConvolutionMatrix getScharrXMatrix() {
        return new ConvolutionMatrix(Matrix.ofSquare(SCHARR_MATRIX), false);
    }

    @Contract(" -> new")
    @NotNull
    public static ConvolutionMatrix getScharrYMatrix() {
        return new ConvolutionMatrix(Matrix.ofSquare(SCHARR_MATRIX).reverse(), false);
    }
}
