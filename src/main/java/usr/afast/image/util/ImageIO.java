package usr.afast.image.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import usr.afast.image.enums.AlgorithmType;

import java.awt.image.BufferedImage;
import java.io.File;

public class ImageIO {
    private static final String FILE_EXTENSION = ".png";
    private static final String FILE_FORMAT = "png";

    public static BufferedImage read(String path) {
        BufferedImage image = null;
        try {
            image = javax.imageio.ImageIO.read(new File(path));
        } catch (Exception e) {
            System.out.println("Unable to read image from file, see the exception below");
            e.printStackTrace();
        }
        return image;
    }

    public static boolean write(String path, BufferedImage image) {
        System.out.println("Saving to " + path);
        try {
            javax.imageio.ImageIO.write(image, FILE_FORMAT, new File(path));
            return true;
        } catch (Exception e) {
            System.out.println("Unable to write image to file, see the exception below");
            e.printStackTrace();
        }
        return false;
    }

    @NotNull
    public static String getSaveFilePath(String path, String suffix) {
        File file = new File(path);
        String directory = file.getParentFile().getAbsolutePath();
        String fileName = cropExtension(file.getName());
        return directory + File.separator + appendFileName(fileName, suffix) + FILE_EXTENSION;
    }

    @NotNull
    @Contract(pure = true)
    private static String appendFileName(String fileName, String suffix) {
        return fileName + "_" + suffix;
    }

    private static String cropExtension(@NotNull String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index == -1) {
            return fileName;
        }
        return fileName.substring(0, index);
    }
}
