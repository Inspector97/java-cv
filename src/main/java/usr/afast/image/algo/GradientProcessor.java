package usr.afast.image.algo;

import org.jetbrains.annotations.NotNull;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.util.Stopwatch;
import usr.afast.image.wrapped.WrappedImage;

import java.awt.image.BufferedImage;

import static usr.afast.image.util.StringArgsUtil.getBorderHandling;
import static usr.afast.image.util.ImageIO.*;

public abstract class GradientProcessor implements Algorithm {
    @Override
    public final void process(String path, @NotNull String... args) {
        final BorderHandling borderHandling = getBorderHandling(0, args);
        BufferedImage image = read(path);
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
