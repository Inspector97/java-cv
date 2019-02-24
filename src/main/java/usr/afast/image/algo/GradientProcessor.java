package usr.afast.image.algo;

import org.jetbrains.annotations.NotNull;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.util.Stopwatch;
import usr.afast.image.wrapped.WrappedImage;

import java.awt.image.BufferedImage;

import static usr.afast.image.util.ImageIO.*;
import static usr.afast.image.util.StringArgsUtil.getBorderHandling;

public abstract class GradientProcessor implements Algorithm {
    @Override
    public final void process(String path, @NotNull String... args) {
        final BorderHandling borderHandling = getBorderHandling(0, args);
        BufferedImage image = read(path);
        WrappedImage wrappedImage = WrappedImage.of(image);
        WrappedImage result = Stopwatch.measure(() -> calcGradient(path, wrappedImage, borderHandling));
        String newFilePath = getSaveFilePath(path, getClass().getSimpleName());
        write(newFilePath, result);
    }

    private WrappedImage calcGradient(String path, WrappedImage wrappedImage, BorderHandling borderHandling) {
        WrappedImage xImage = getXImage(wrappedImage, borderHandling);
        WrappedImage yImage = getYImage(wrappedImage, borderHandling);
        WrappedImage result = WrappedImage.getGradient(xImage, yImage);
        write(getSaveFilePath(path, getClass().getSimpleName() + "_X"), xImage);
        write(getSaveFilePath(path, getClass().getSimpleName() + "_Y"), yImage);
        return result;
    }

    public abstract WrappedImage getXImage(WrappedImage wrappedImage, BorderHandling borderHandling);

    public abstract WrappedImage getYImage(WrappedImage wrappedImage, BorderHandling borderHandling);
}
