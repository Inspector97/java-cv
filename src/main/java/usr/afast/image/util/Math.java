package usr.afast.image.util;

import org.jetbrains.annotations.Contract;

public class Math {
    @Contract(pure = true)
    public static double sqr(double value) {
        return value * value;
    }
}
