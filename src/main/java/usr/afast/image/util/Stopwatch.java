package usr.afast.image.util;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class Stopwatch {
    public static <T> T measure(@NotNull Supplier<T> supplier) {
        T result;
        System.out.println("Start processing");
        long startTime = System.currentTimeMillis();
        result = supplier.get();
        double time = (System.currentTimeMillis() - startTime) / 1000D;
        System.out.println(String.format("Processed in %.3f s.", time));
        return result;
    }
    public static void measure(@NotNull Runnable runnable) {
        System.out.println("Start processing");
        long startTime = System.currentTimeMillis();
        runnable.run();
        double time = (System.currentTimeMillis() - startTime) / 1000D;
        System.out.println(String.format("Processed in %.3f s.", time));
    }
    public static double inSeconds(@NotNull Runnable runnable) {
        long startTime = System.currentTimeMillis();
        runnable.run();
        return (System.currentTimeMillis() - startTime) / 1000D;
    }
}
