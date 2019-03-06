package usr.afast.image.algo;

import usr.afast.image.points.InterestingPoint;
import usr.afast.image.points.Moravec;
import usr.afast.image.wrapped.Matrix;

import java.util.List;

public class MoravecAlgo extends Detector {
    @Override
    public List<InterestingPoint> makeAlgorithm(Matrix matrix) {
        return Moravec.makeMoravec(matrix);
    }
}
