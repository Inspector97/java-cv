package usr.afast.image.util;

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
}
