package usr.afast.image.algo;

import org.jetbrains.annotations.NotNull;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.util.Stopwatch;
import usr.afast.image.wrapped.Matrix;

import java.awt.image.BufferedImage;

import static usr.afast.image.util.ImageIO.*;
import static usr.afast.image.util.StringArgsUtil.getBorderHandling;

public abstract class GradientProcessor implements Algorithm {
    @Override
    public final void process(String path, @NotNull String... args) {
        final BorderHandling borderHandling = getBorderHandling(0, args);
        BufferedImage image = read(path);
        Matrix matrix = Matrix.of(image);
        Matrix result = Stopwatch.measure(() -> calcGradient(path, matrix, borderHandling));
        String newFilePath = getSaveFilePath(path, getClass().getSimpleName());
        write(newFilePath, result);
    }

    private Matrix calcGradient(String path, Matrix matrix, BorderHandling borderHandling) {
        Matrix xImage = getXImage(matrix, borderHandling);
        Matrix yImage = getYImage(matrix, borderHandling);
        Matrix result = Matrix.getGradient(xImage, yImage);
        write(getSaveFilePath(path, getClass().getSimpleName() + "_X"), xImage);
        write(getSaveFilePath(path, getClass().getSimpleName() + "_Y"), yImage);
        return result;
    }

    public abstract Matrix getXImage(Matrix matrix, BorderHandling borderHandling);

    public abstract Matrix getYImage(Matrix matrix, BorderHandling borderHandling);
}
