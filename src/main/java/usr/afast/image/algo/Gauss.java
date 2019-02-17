package usr.afast.image.algo;

import usr.afast.image.enums.BorderHandling;
import usr.afast.image.math.ConvolutionMatrixFactory;
import usr.afast.image.math.ImageMatrixProcessor;
import usr.afast.image.util.Stopwatch;
import usr.afast.image.wrapped.WrappedImage;

import java.awt.image.BufferedImage;

import static usr.afast.image.util.StringArgsUtil.getBorderHandling;
import static usr.afast.image.util.ImageIO.*;
import static usr.afast.image.util.StringArgsUtil.getDouble;

public class Gauss implements Algorithm {
    @Override
    public void process(String path, String... args) {
        final BorderHandling borderHandling = getBorderHandling(0, args);
        double sigma = getDouble(1, args);
        System.out.println(String.format("Gauss with sigma %.3f", sigma));
        BufferedImage image = read(path);
        WrappedImage wrappedImage = WrappedImage.of(image);
        write(getSaveFilePath(path, "original"), WrappedImage.save(wrappedImage));
        WrappedImage result =
                Stopwatch.measure(() -> ImageMatrixProcessor.processWithConvolution(wrappedImage,
                                                                                    ConvolutionMatrixFactory.getGaussMatrix(sigma),
                                                                                    borderHandling));
        BufferedImage bufferedImageResult = WrappedImage.save(result);
        String newFilePath = getSaveFilePath(path, getClass().getSimpleName());
        write(newFilePath, bufferedImageResult);
    }
}
