package usr.afast.image.algo;

import usr.afast.image.enums.BorderHandling;
import usr.afast.image.math.ConvolutionMatrixFactory;
import usr.afast.image.math.ImageMatrixProcessor;
import usr.afast.image.wrapped.WrappedImage;

public class Sobel extends GradientProcessor {
    @Override
    public WrappedImage getXImage(WrappedImage wrappedImage, BorderHandling borderHandling) {
        return ImageMatrixProcessor.processWithConvolution(wrappedImage,
                                                           ConvolutionMatrixFactory.getSobelXMatrix(),
                                                           borderHandling);
    }

    @Override
    public WrappedImage getYImage(WrappedImage wrappedImage, BorderHandling borderHandling) {
        return ImageMatrixProcessor.processWithConvolution(wrappedImage,
                                                           ConvolutionMatrixFactory.getSobelYMatrix(),
                                                           borderHandling);
    }
}
