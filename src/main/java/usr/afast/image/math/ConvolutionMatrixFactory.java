package usr.afast.image.math;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import usr.afast.image.util.SeparableMatrix;
import usr.afast.image.wrapped.Matrix;

import static java.lang.Math.exp;
import static java.lang.Math.sqrt;
import static usr.afast.image.util.Math.sqr;

public class ConvolutionMatrixFactory {
    private static final double[][] SOBEL_A_VECTOR = {{1, 2, 1}};
    private static final double[][] PRUITT_A_VERCTOR = {{1, 1, 1}};
    private static final double[][] SCHARR_A_VERCTOR = {{3, 10, 3}};
    private static final double[][] BORDER_B_VECTOR = {{1, 0, -1}};

    @Contract(" -> new")
    @NotNull
    public static Matrix[] getSobelXMatrices() {
        return new Matrix[]{Matrix.of(SOBEL_A_VECTOR), Matrix.transform(Matrix.of(BORDER_B_VECTOR))};
    }

    @Contract(" -> new")
    @NotNull
    public static Matrix[] getSobelYMatrices() {
        return new Matrix[]{Matrix.of(BORDER_B_VECTOR), Matrix.transform(Matrix.of(SOBEL_A_VECTOR))};
    }

    @Contract(" -> new")
    @NotNull
    public static Matrix[] getPruittXMatrices() {
        return new Matrix[]{Matrix.of(PRUITT_A_VERCTOR), Matrix.transform(Matrix.of(BORDER_B_VECTOR))};
    }

    @Contract(" -> new")
    @NotNull
    public static Matrix[] getPruittYMatrices() {
        return new Matrix[]{Matrix.of(BORDER_B_VECTOR), Matrix.transform(Matrix.of(PRUITT_A_VERCTOR))};
    }

    @Contract(" -> new")
    @NotNull
    public static Matrix[] getScharrXMatrices() {
        return new Matrix[]{Matrix.of(SCHARR_A_VERCTOR), Matrix.transform(Matrix.of(BORDER_B_VECTOR))};
    }

    @Contract(" -> new")
    @NotNull
    public static Matrix[] getScharrYMatrices() {
        return new Matrix[]{Matrix.of(BORDER_B_VECTOR), Matrix.transform(Matrix.of(SCHARR_A_VERCTOR))};
    }

    @NotNull
    public static Matrix[] getGaussMatrices(double sigma) {
        return getGaussMatrices((int) Math.ceil(3 * sigma), sigma);
    }

    @NotNull
    public static Matrix[] getGaussMatrices(int halfSize) {
        return getGaussMatrices(halfSize, halfSize / 3D);
    }

    @Contract("_, _ -> new")
    @NotNull
    public static Matrix[] getGaussMatrices(int halfSize, double sigma) {
        double[][] vector = new double[1][halfSize * 2 + 1];
        double coef = 1 / (sqrt(2 * Math.PI) * sigma);
        for (int x = -halfSize; x <= halfSize; x++) {
            vector[0][x + halfSize] = coef * exp(-sqr(x) / (2 * sqr(sigma)));
        }
        double sum = 0;
        for (int i = 0; i < 2 * halfSize + 1; i++) {
            sum += vector[0][i];
        }
        for (int i = 0; i < 2 * halfSize + 1; i++) {
            vector[0][i] /= sum;
        }
        return new Matrix[]{Matrix.of(vector), Matrix.transform(Matrix.of(vector))};
    }

    @NotNull
    @Contract("_ -> new")
    public static SeparableMatrix separableMatrixFrom(@NotNull Matrix[] matrices) {
        return new SeparableMatrix(matrices[0], matrices[1]);
    }

}
