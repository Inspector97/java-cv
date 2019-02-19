package usr.afast.image.algo;

import lombok.experimental.UtilityClass;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.math.ConvolutionMatrixFactory;
import usr.afast.image.math.ImageMatrixProcessor;
import usr.afast.image.wrapped.WrappedImage;

@UtilityClass
public class AlgoLib {
    public WrappedImage makeGauss(WrappedImage wrappedImage, double sigma, BorderHandling borderHandling) {
        return ImageMatrixProcessor.processWithConvolution(wrappedImage,
                                                           ConvolutionMatrixFactory.getGaussMatrix(sigma),
                                                           borderHandling);
    }
    public WrappedImage getSobelX(WrappedImage wrappedImage, BorderHandling borderHandling) {
        return ImageMatrixProcessor.processWithConvolution(wrappedImage,
                                                           ConvolutionMatrixFactory.getSobelXMatrix(),
                                                           borderHandling);
    }
    public WrappedImage getSobelY(WrappedImage wrappedImage, BorderHandling borderHandling) {
        return ImageMatrixProcessor.processWithConvolution(wrappedImage,
                                                           ConvolutionMatrixFactory.getSobelYMatrix(),
                                                           borderHandling);
    }
    public WrappedImage getScharrX(WrappedImage wrappedImage, BorderHandling borderHandling) {
        return ImageMatrixProcessor.processWithConvolution(wrappedImage,
                                                           ConvolutionMatrixFactory.getScharrXMatrix(),
                                                           borderHandling);
    }
    public WrappedImage getScharrY(WrappedImage wrappedImage, BorderHandling borderHandling) {
        return ImageMatrixProcessor.processWithConvolution(wrappedImage,
                                                           ConvolutionMatrixFactory.getScharrYMatrix(),
                                                           borderHandling);
    }
    public WrappedImage getPruittX(WrappedImage wrappedImage, BorderHandling borderHandling) {
        return ImageMatrixProcessor.processWithConvolution(wrappedImage,
                                                           ConvolutionMatrixFactory.getPruittXMatrix(),
                                                           borderHandling);
    }
    public WrappedImage getPruittY(WrappedImage wrappedImage, BorderHandling borderHandling) {
        return ImageMatrixProcessor.processWithConvolution(wrappedImage,
                                                           ConvolutionMatrixFactory.getPruittYMatrix(),
                                                           borderHandling);
    }
}
