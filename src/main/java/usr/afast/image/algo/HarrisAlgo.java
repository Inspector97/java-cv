package usr.afast.image.algo;

import usr.afast.image.points.Harris;
import usr.afast.image.points.InterestingPoint;
import usr.afast.image.wrapped.Matrix;

import java.util.List;

public class HarrisAlgo extends Detector {
    @Override
    public List<InterestingPoint> makeAlgorithm(Matrix matrix) {
        return Harris.makeHarris(matrix);
    }
}
