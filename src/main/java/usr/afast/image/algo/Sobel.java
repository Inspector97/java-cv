package usr.afast.image.algo;

import org.jetbrains.annotations.NotNull;
import usr.afast.image.enums.AlgorithmType;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.math.ConvolutionMatrixFactory;
import usr.afast.image.math.ImageMatrixProcessor;
import usr.afast.image.util.Stopwatch;
import usr.afast.image.wrapped.WrappedImage;

import java.awt.image.BufferedImage;

import static usr.afast.image.util.ImageIO.*;

public class Sobel {
    public static void process(String path, @NotNull String... args) {
        final BorderHandling borderHandling = args.length > 0 ? BorderHandling.of(args[0]) : BorderHandling.Copy;
        System.out.println("Using " + borderHandling.name() + " as border handling in Sobel");
        BufferedImage image = read(path);
        if (image == null) return;
        WrappedImage wrappedImage = WrappedImage.of(image);
        WrappedImage result = Stopwatch.measure(() -> {
            WrappedImage xImage = ImageMatrixProcessor.processWithConvolution(wrappedImage,
                                                                              ConvolutionMatrixFactory.getSobelXMatrix(),
                                                                              borderHandling);
            WrappedImage yImage = ImageMatrixProcessor.processWithConvolution(wrappedImage,
                                                                              ConvolutionMatrixFactory.getSobelYMatrix(),
                                                                              borderHandling);

            return WrappedImage.getGradient(xImage, yImage);
        });
        BufferedImage bufferedImageResult = result.save();
        String newFilePath = getSaveFilePath(path, AlgorithmType.Sobel);
        write(newFilePath, bufferedImageResult);
    }
}
