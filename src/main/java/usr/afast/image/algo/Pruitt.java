package usr.afast.image.algo;

import usr.afast.image.enums.BorderHandling;
import usr.afast.image.wrapped.Matrix;

import static usr.afast.image.algo.AlgoLib.getPruittX;
import static usr.afast.image.algo.AlgoLib.getPruittY;

public class Pruitt extends GradientProcessor {
    @Override
    public Matrix getXImage(Matrix matrix, BorderHandling borderHandling) {
        return getPruittX(matrix, borderHandling);
    }

    @Override
    public Matrix getYImage(Matrix matrix, BorderHandling borderHandling) {
        return getPruittY(matrix, borderHandling);
    }
}
