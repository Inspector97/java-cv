package usr.afast.image.algo;

import usr.afast.image.enums.BorderHandling;
import usr.afast.image.math.ConvolutionMatrixFactory;
import usr.afast.image.math.ImageMatrixProcessor;
import usr.afast.image.wrapped.Matrix;

public class AlgoLib {
    public static Matrix makeGauss(Matrix matrix, double sigma, BorderHandling borderHandling) {
        return ImageMatrixProcessor.processWithConvolution(matrix,
                                                           borderHandling,
                                                           ConvolutionMatrixFactory.getGaussMatrices(sigma));
    }

    public static Matrix makeGauss(Matrix matrix, int halfSize, BorderHandling borderHandling) {
        return ImageMatrixProcessor.processWithConvolution(matrix,
                                                           borderHandling,
                                                           ConvolutionMatrixFactory.getGaussMatrices(halfSize));
    }

    public static Matrix makeGauss(Matrix matrix, int halfSize, double sigma, BorderHandling borderHandling) {
        return ImageMatrixProcessor.processWithConvolution(matrix,
                                                           borderHandling,
                                                           ConvolutionMatrixFactory.getGaussMatrices(halfSize, sigma));
    }

    public static Matrix getSobelX(Matrix matrix, BorderHandling borderHandling) {
        return ImageMatrixProcessor.processWithConvolution(matrix,
                                                           borderHandling,
                                                           ConvolutionMatrixFactory.getSobelXMatrices());
    }

    public static Matrix getSobelY(Matrix matrix, BorderHandling borderHandling) {
        return ImageMatrixProcessor.processWithConvolution(matrix,
                                                           borderHandling,
                                                           ConvolutionMatrixFactory.getSobelYMatrices());
    }

    public static Matrix getScharrX(Matrix matrix, BorderHandling borderHandling) {
        return ImageMatrixProcessor.processWithConvolution(matrix,
                                                           borderHandling,
                                                           ConvolutionMatrixFactory.getScharrXMatrices());
    }

    public static Matrix getScharrY(Matrix matrix, BorderHandling borderHandling) {
        return ImageMatrixProcessor.processWithConvolution(matrix,
                                                           borderHandling,
                                                           ConvolutionMatrixFactory.getScharrYMatrices());
    }

    public static Matrix getPruittX(Matrix matrix, BorderHandling borderHandling) {
        return ImageMatrixProcessor.processWithConvolution(matrix,
                                                           borderHandling,
                                                           ConvolutionMatrixFactory.getPruittXMatrices());
    }

    public static Matrix getPruittY(Matrix matrix, BorderHandling borderHandling) {
        return ImageMatrixProcessor.processWithConvolution(matrix,
                                                           borderHandling,
                                                           ConvolutionMatrixFactory.getPruittYMatrices());
    }
}
