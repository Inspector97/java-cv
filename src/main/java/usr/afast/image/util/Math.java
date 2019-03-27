package usr.afast.image.util;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealVector;
import org.jetbrains.annotations.Contract;

public class Math {

    private static final double EPS = 1e-8;

    @Contract(pure = true)
    public static double sqr(double value) {
        return value * value;
    }

    @Contract(pure = true)
    public static int sign(double value) {
        if (java.lang.Math.abs(value) < EPS)
            return 0;
        return value < 0 ? -1 : 1;
    }

    @Contract(pure = true)
    public static int sign(int value) {
        if (value == 0)
            return 0;
        return value < 0 ? -1 : 1;
    }

    public static double[] solveLU(double[][] A, double[] B) {
        LUDecomposition decomposition = new LUDecomposition(new Array2DRowRealMatrix(A));
        RealVector vector = decomposition.getSolver().solve(new ArrayRealVector(B));
        return vector.toArray();
    }

    public static double dot(double[] a, double[] b) {
        double result = 0;
        for(int i = 0; i < a.length; i++) result += a[i] * b[i];
        return result;
    }
}
