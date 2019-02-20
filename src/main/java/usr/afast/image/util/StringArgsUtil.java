package usr.afast.image.util;

import org.jetbrains.annotations.NotNull;
import usr.afast.image.enums.BorderHandling;

public class StringArgsUtil {
    public static BorderHandling getBorderHandling(int index, @NotNull String... args) {
        return args.length > 0 ? BorderHandling.of(args[0]) : BorderHandling.Copy;
    }
    public static double getDouble(int index, @NotNull String... args) {
        return args.length > 0 ? Double.parseDouble(args[index]) : 0;
    }
    public static int getInt(int index, @NotNull String... args) {
        return args.length > 0 ? Integer.parseInt(args[index]) : 0;
    }
}
