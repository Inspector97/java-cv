package usr.afast.image.algo;

import usr.afast.image.points.Harris;
import usr.afast.image.points.InterestingPoint;
import usr.afast.image.points.Moravec;
import usr.afast.image.util.Stopwatch;
import usr.afast.image.wrapped.WrappedImage;

import java.awt.image.BufferedImage;
import java.util.List;

import static usr.afast.image.points.PointMarker.markPoints;
import static usr.afast.image.points.PointsFilter.filterPoints;
import static usr.afast.image.points.PointsFilter.filterPointsFast;
import static usr.afast.image.util.ImageIO.*;
import static usr.afast.image.util.StringArgsUtil.getInt;

public class HarrisAlgo extends Detector {
    @Override
    public List<InterestingPoint> makeAlgorithm(WrappedImage wrappedImage) {
        return Harris.makeHarris(wrappedImage);
    }
}
