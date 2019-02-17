package usr.afast.image.algo;

import org.jetbrains.annotations.NotNull;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.util.Stopwatch;
import usr.afast.image.wrapped.WrappedImage;

import java.awt.image.BufferedImage;
import java.nio.file.Watchable;

import static usr.afast.image.util.ImageIO.*;

public abstract class GradientProcessor {
    public final void process(String path, @NotNull String... args) {
        final BorderHandling borderHandling = args.length > 0 ? BorderHandling.of(args[0]) : BorderHandling.Copy;
        System.out.println("Using " + borderHandling.name() + " as border handling");
        BufferedImage image = read(path);
        if (image == null) return;
        WrappedImage wrappedImage = WrappedImage.of(image);
        WrappedImage result = Stopwatch.measure(() -> calcGradient(path, wrappedImage, borderHandling));
        BufferedImage bufferedImageResult = WrappedImage.save(result);
        String newFilePath = getSaveFilePath(path, getClass().getSimpleName());
        write(newFilePath, bufferedImageResult);
    }

    private WrappedImage calcGradient(String path, WrappedImage wrappedImage, BorderHandling borderHandling) {
        WrappedImage xImage = getXImage(wrappedImage, borderHandling);
        WrappedImage yImage = getYImage(wrappedImage, borderHandling);
        WrappedImage result = WrappedImage.getGradient(xImage, yImage);
        write(getSaveFilePath(path, getClass().getSimpleName() + "_X"), WrappedImage.save(xImage));
        write(getSaveFilePath(path, getClass().getSimpleName() + "_Y"), WrappedImage.save(yImage));
        return result;
    }

    public abstract WrappedImage getXImage(WrappedImage wrappedImage, BorderHandling borderHandling);

    public abstract WrappedImage getYImage(WrappedImage wrappedImage, BorderHandling borderHandling);
}
