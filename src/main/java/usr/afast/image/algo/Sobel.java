package usr.afast.image.algo;

import usr.afast.image.enums.BorderHandling;
import usr.afast.image.wrapped.Matrix;

import static usr.afast.image.algo.AlgoLib.getSobelX;
import static usr.afast.image.algo.AlgoLib.getSobelY;

public class Sobel extends GradientProcessor {
    @Override
    public Matrix getXImage(Matrix matrix, BorderHandling borderHandling) {
        return getSobelX(matrix, borderHandling);
    }

    @Override
    public Matrix getYImage(Matrix matrix, BorderHandling borderHandling) {
        return getSobelY(matrix, borderHandling);
    }
}
