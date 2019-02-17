package usr.afast.image.algo;

import usr.afast.image.enums.BorderHandling;
import usr.afast.image.math.ConvolutionMatrixFactory;
import usr.afast.image.math.ImageMatrixProcessor;
import usr.afast.image.wrapped.WrappedImage;

public class Pruitt extends SingleResultProcessor {
    @Override
    public WrappedImage apply(WrappedImage wrappedImage, BorderHandling borderHandling) {
        WrappedImage xImage = ImageMatrixProcessor.processWithConvolution(wrappedImage,
                                                                          ConvolutionMatrixFactory.getPruittXMatrix(),
                                                                          borderHandling);
        WrappedImage yImage = ImageMatrixProcessor.processWithConvolution(wrappedImage,
                                                                          ConvolutionMatrixFactory.getPruittYMatrix(),
                                                                          borderHandling);

        return WrappedImage.getGradient(xImage, yImage);
    }
}
