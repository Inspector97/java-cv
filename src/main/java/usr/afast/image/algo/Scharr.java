package usr.afast.image.algo;

import usr.afast.image.enums.BorderHandling;
import usr.afast.image.wrapped.Matrix;

import static usr.afast.image.algo.AlgoLib.getScharrX;
import static usr.afast.image.algo.AlgoLib.getScharrY;

public class Scharr extends GradientProcessor {
    @Override
    public Matrix getXImage(Matrix matrix, BorderHandling borderHandling) {
        return getScharrX(matrix, borderHandling);
    }

    @Override
    public Matrix getYImage(Matrix matrix, BorderHandling borderHandling) {
        return getScharrY(matrix, borderHandling);
    }
}
