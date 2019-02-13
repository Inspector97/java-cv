package usr.afast.image;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import usr.afast.image.api.ImageProcessor;
import usr.afast.image.api.util.StringParseUtil;
import usr.afast.image.config.ProcessorsConfiguration;
import usr.afast.image.enums.AlgorithmType;
import usr.afast.image.wrapped.WrappedImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Main {
    private static final String FILE_EXTENSION = ".png";
    private static final String FILE_FORMAT = "png";

    public static void main(@NotNull String[] args) {
        if (args.length < 2) {
            System.out.println("Invalid args");
            return;
        }
        if (!checkPath(args[0])) {
            System.out.println("Invalid path");
            return;
        }
        AlgorithmType type = getAlgorithmType(args[1]);
        if (type == null) {
            System.out.println("Invalid algorithm code");
            return;
        }
        String path = args[0];

        BufferedImage image = read(path);
        ImageProcessor imageProcessor = ProcessorsConfiguration.getImageProcessor(type);

        WrappedImage wrappedImage = WrappedImage.of(image);
        System.out.println("Processing");
        long startTime = System.currentTimeMillis();
        WrappedImage result = imageProcessor.process(wrappedImage, type);
        double timeSeconds = (System.currentTimeMillis() - startTime) / 1000D;
        System.out.println(String.format("Processed in %.3f s.", timeSeconds));
        BufferedImage bufferedImageResult = result.save();

        String newFilePath = getSaveFilePath(path, type);
        System.out.println("Saving to " + newFilePath);
        write(newFilePath, bufferedImageResult);
    }

    private static boolean checkPath(String path) {
        File file = new File(path);
        return file.canRead() && !file.isDirectory();
    }

    @Nullable
    private static AlgorithmType getAlgorithmType(String arg) {
        arg = arg.toLowerCase();
        for (AlgorithmType algorithmType : AlgorithmType.values()) {
            if (algorithmType.getCode().equals(arg)) {
                return algorithmType;
            }
        }
        return null;
    }

    private static BufferedImage read(String path) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(path));
        } catch (Exception e) {
            System.out.println("Unable to read image from file, see the exception below");
            e.printStackTrace();
        }
        return image;
    }

    private static boolean write(String path, BufferedImage image) {
        try {
            ImageIO.write(image, FILE_FORMAT, new File(path));
            return true;
        } catch (Exception e) {
            System.out.println("Unable to write image to file, see the exception below");
            e.printStackTrace();
        }
        return false;
    }

    @NotNull
    private static String getSaveFilePath(String path, @NotNull AlgorithmType algorithmType) {
        File file = new File(path);
        String directory = file.getParentFile().getAbsolutePath();
        String fileName = cropExtension(file.getName());
        return directory + File.separator + appendFileName(fileName, algorithmType) + FILE_EXTENSION;
    }

    @NotNull
    @Contract(pure = true)
    private static String appendFileName(String fileName, @NotNull AlgorithmType algorithmType) {
        return fileName + "_" + algorithmType.name();
    }

    private static String cropExtension(@NotNull String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index == -1) {
            return fileName;
        }
        return fileName.substring(0, index);
    }

}
