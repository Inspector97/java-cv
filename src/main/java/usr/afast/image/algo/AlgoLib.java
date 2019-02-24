package usr.afast.image.algo;

import usr.afast.image.enums.BorderHandling;
import usr.afast.image.math.ConvolutionMatrixFactory;
import usr.afast.image.math.ImageMatrixProcessor;
import usr.afast.image.wrapped.WrappedImage;

public class AlgoLib {
    public static WrappedImage makeGauss(WrappedImage wrappedImage, double sigma, BorderHandling borderHandling) {
        return ImageMatrixProcessor.processWithConvolution(wrappedImage,
                                                           ConvolutionMatrixFactory.getGaussMatrix(sigma),
                                                           borderHandling);
    }

    public static WrappedImage makeGauss(WrappedImage wrappedImage, int halfSize, BorderHandling borderHandling) {
        return ImageMatrixProcessor.processWithConvolution(wrappedImage,
                                                           ConvolutionMatrixFactory.getGaussMatrix(halfSize),
                                                           borderHandling);
    }

    public static WrappedImage makeGauss(WrappedImage wrappedImage, int halfSize, double sigma,
                                         BorderHandling borderHandling) {
        return ImageMatrixProcessor.processWithConvolution(wrappedImage,
                                                           ConvolutionMatrixFactory.getGaussMatrix(halfSize, sigma),
                                                           borderHandling);
    }

    public static WrappedImage getSobelX(WrappedImage wrappedImage, BorderHandling borderHandling) {
        return ImageMatrixProcessor.processWithConvolution(wrappedImage,
                                                           ConvolutionMatrixFactory.getSobelXMatrix(),
                                                           borderHandling);
    }

    public static WrappedImage getSobelY(WrappedImage wrappedImage, BorderHandling borderHandling) {
        return ImageMatrixProcessor.processWithConvolution(wrappedImage,
                                                           ConvolutionMatrixFactory.getSobelYMatrix(),
                                                           borderHandling);
    }

    public static WrappedImage getScharrX(WrappedImage wrappedImage, BorderHandling borderHandling) {
        return ImageMatrixProcessor.processWithConvolution(wrappedImage,
                                                           ConvolutionMatrixFactory.getScharrXMatrix(),
                                                           borderHandling);
    }

    public static WrappedImage getScharrY(WrappedImage wrappedImage, BorderHandling borderHandling) {
        return ImageMatrixProcessor.processWithConvolution(wrappedImage,
                                                           ConvolutionMatrixFactory.getScharrYMatrix(),
                                                           borderHandling);
    }

    public static WrappedImage getPruittX(WrappedImage wrappedImage, BorderHandling borderHandling) {
        return ImageMatrixProcessor.processWithConvolution(wrappedImage,
                                                           ConvolutionMatrixFactory.getPruittXMatrix(),
                                                           borderHandling);
    }

    public static WrappedImage getPruittY(WrappedImage wrappedImage, BorderHandling borderHandling) {
        return ImageMatrixProcessor.processWithConvolution(wrappedImage,
                                                           ConvolutionMatrixFactory.getPruittYMatrix(),
                                                           borderHandling);
    }
}
