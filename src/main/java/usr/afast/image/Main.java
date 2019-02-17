package usr.afast.image;

import org.jetbrains.annotations.NotNull;
import usr.afast.image.algo.Gauss;
import usr.afast.image.algo.Pruitt;
import usr.afast.image.algo.Scharr;
import usr.afast.image.algo.Sobel;
import usr.afast.image.enums.AlgorithmType;

import java.io.File;

public class Main {
    public static void main(@NotNull String[] args) {
        if (args.length < 2) {
            System.out.println("Invalid args");
            return;
        }
        if (!checkPath(args[0])) {
            System.out.println("Invalid path");
            return;
        }
        AlgorithmType type = AlgorithmType.valueOf(args[1]);
        String path = args[0];
        String[] restArgs = restArgs(2, args);

        switch (type) {
            case Sobel:
                new Sobel().process(path, restArgs);
                break;
            case Pruitt:
                new Pruitt().process(path, restArgs);
                break;
            case Scharr:
                new Scharr().process(path, restArgs);
                break;
            case Lab1:
                new Sobel().process(path, restArgs);
                new Pruitt().process(path, restArgs);
                new Scharr().process(path, restArgs);
                break;
            case Gauss:
                new Gauss().process(path, restArgs);
                break;
            default:
                System.out.println("Not implemented yet");
        }
    }

    private static boolean checkPath(String path) {
        File file = new File(path);
        return file.canRead() && !file.isDirectory();
    }

    @SuppressWarnings("SameParameterValue")
    private static String[] restArgs(int skip, @NotNull String... args) {
        if (args.length <= skip)
            return new String[0];
        String[] restArgs = new String[args.length - skip];
        System.arraycopy(args, skip, restArgs, 0, args.length - skip);
        return restArgs;
    }
}
