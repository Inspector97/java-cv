package usr.afast.image.math;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static java.lang.Math.exp;
import static java.lang.Math.sqrt;

public class ConvolutionMatrixFactory {
    private static final double[] SOBEL_A_VECTOR = {1, 2, 1};
    private static final double[] PRUITT_A_VERCTOR = {1, 1, 1};
    private static final double[] SCHARR_A_VERCTOR = {3, 10, 3};
    private static final double[] BORDER_B_VECTOR = {1, 0, -1};

    @NotNull
    public static ConvolutionMatrix getSobelXMatrix() {
        return new SeparableConvolutionMatrix(Vector.of(SOBEL_A_VECTOR), Vector.of(BORDER_B_VECTOR));
    }

    @NotNull
    public static ConvolutionMatrix getSobelYMatrix() {
        return new SeparableConvolutionMatrix(Vector.of(BORDER_B_VECTOR), Vector.of(SOBEL_A_VECTOR));
    }

    @NotNull
    public static ConvolutionMatrix getPruittXMatrix() {
        return new SeparableConvolutionMatrix(Vector.of(PRUITT_A_VERCTOR), Vector.of(BORDER_B_VECTOR));
    }

    @NotNull
    public static ConvolutionMatrix getPruittYMatrix() {
        return new SeparableConvolutionMatrix(Vector.of(BORDER_B_VECTOR), Vector.of(PRUITT_A_VERCTOR));
    }

    @NotNull
    public static ConvolutionMatrix getScharrXMatrix() {
        return new SeparableConvolutionMatrix(Vector.of(SCHARR_A_VERCTOR), Vector.of(BORDER_B_VECTOR));
    }

    @NotNull
    public static ConvolutionMatrix getScharrYMatrix() {
        return new SeparableConvolutionMatrix(Vector.of(BORDER_B_VECTOR), Vector.of(SCHARR_A_VERCTOR));
    }

    @NotNull
    public static ConvolutionMatrix getGaussMatrix(double sigma) {
        int halfSize = (int) Math.ceil(3 * sigma);
        double[] vector = new double[halfSize * 2 + 1];
        double coef = 1 / (sqrt(2 * Math.PI) * sigma);
        for (int x = -halfSize; x <= halfSize; x++) {
            vector[x + halfSize] = coef * exp(-sqr(x) / (2 * sqr(sigma)));
        }
        double sum = 0;
        for (int i = 0; i < 2 * halfSize + 1; i++) {
            for (int j = 0; j < 2 * halfSize + 1; j++) {
                sum += vector[i] * vector[j];
            }
        }
        sum = Math.sqrt(sum);
        for (int i = 0; i < 2 * halfSize + 1; i++) {
            vector[i] /= sum;
        }
        return new SeparableConvolutionMatrix(Vector.of(vector), Vector.of(vector));
    }


    @Contract(pure = true)
    private static double sqr(double value) {
        return value * value;
    }

}
