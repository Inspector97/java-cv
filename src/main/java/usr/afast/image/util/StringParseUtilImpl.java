package usr.afast.image.util;

import usr.afast.image.annotation.Singleton;
import usr.afast.image.api.util.StringParseUtil;

import java.util.function.Supplier;

@Singleton
public class StringParseUtilImpl implements StringParseUtil {

    @Override
    public Double parseDouble(String s) {
        return successOrNull(() -> Double.parseDouble(s));
    }

    @Override
    public Integer parseInteger(String s) {
        return successOrNull(() -> Integer.parseInt(s));
    }

    @Override
    public Long parseLong(String s) {
        return successOrNull(() -> Long.parseLong(s));
    }

    private static <T> T successOrNull(Supplier<T> supplier) {
        T value = null;
        try {
            value = supplier.get();
        } catch (Exception e) {
            //
        }
        return value;
    }
}
