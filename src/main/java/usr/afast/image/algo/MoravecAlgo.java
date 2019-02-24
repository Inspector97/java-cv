package usr.afast.image.algo;

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

public class MoravecAlgo implements Algorithm {
    @Override
    public void process(String path, String... args) {
        int maxPoints = getInt(0, args);
        BufferedImage image = read(path);
        WrappedImage wrappedImage = WrappedImage.of(image);
        List<InterestingPoint> interestingPoints = Stopwatch.measure(() -> Moravec.makeMoravec(wrappedImage));
        System.out.println("Points: " + interestingPoints.size());

        System.out.println("Filtering simple");
        List<InterestingPoint> filtered = Stopwatch.measure(() -> filterPoints(interestingPoints, maxPoints));
        System.out.println("Filtered points: " + filtered.size());

        System.out.println("Filtering fast");
        List<InterestingPoint> filtered2 = Stopwatch.measure(() -> filterPointsFast(interestingPoints, maxPoints));
        System.out.println("Filtered fast points: " + filtered2.size());

        BufferedImage resultAll = markPoints(interestingPoints, wrappedImage);
        BufferedImage resultFiltered = markPoints(filtered, wrappedImage);
        BufferedImage resultFiltered2 = markPoints(filtered2, wrappedImage);

        String newFilePathAll = getSaveFilePath(path, getClass().getSimpleName()+"_ALL");
        write(newFilePathAll, resultAll);
        String newFilePathFiltered = getSaveFilePath(path, getClass().getSimpleName()+"_FILTERED");
        write(newFilePathFiltered, resultFiltered);
        String newFilePathFiltered2 = getSaveFilePath(path, getClass().getSimpleName()+"_FILTERED_FAST");
        write(newFilePathFiltered2, resultFiltered2);
    }
}
