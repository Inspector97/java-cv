package usr.afast.image.util;

import org.jetbrains.annotations.NotNull;
import usr.afast.image.wrapped.WrappedImage;

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
}
