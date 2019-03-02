package usr.afast.image;

import org.jetbrains.annotations.NotNull;
import usr.afast.image.algo.*;
import usr.afast.image.enums.AlgorithmType;

import java.io.File;
import java.util.Locale;

public class Main {
    public static void main(@NotNull String[] args) {
        Locale.setDefault(Locale.US);
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
            case Pyramid:
                new PyramidAlgo().process(path, restArgs);
                break;
            case Moravec:
                new MoravecAlgo().process(path, restArgs);
                break;
            case Harris:
                new HarrisAlgo().process(path, restArgs);
                break;
            case Canny:
                new CannyAlgo().process(path, restArgs);
                break;
            case Patch:
                new PatchAlgo().process(path, restArgs);
                break;
            case Sift:
                new SiftAlgo().process(path, restArgs);
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
