package usr.afast.image.algo;

import usr.afast.image.enums.BorderHandling;
import usr.afast.image.math.ConvolutionMatrixFactory;
import usr.afast.image.math.ImageMatrixProcessor;
import usr.afast.image.wrapped.WrappedImage;

import static usr.afast.image.util.ImageIO.write;

public class Sobel extends SingleResultProcessor {
    @Override
    public WrappedImage apply(WrappedImage wrappedImage, BorderHandling borderHandling) {
        WrappedImage xImage = ImageMatrixProcessor.processWithConvolution(wrappedImage,
                                                                          ConvolutionMatrixFactory.getSobelXMatrix(),
                                                                          borderHandling);
        WrappedImage yImage = ImageMatrixProcessor.processWithConvolution(wrappedImage,
                                                                          ConvolutionMatrixFactory.getSobelYMatrix(),
                                                                          borderHandling);

        return WrappedImage.getGradient(xImage, yImage);
    }
}
