package usr.afast.image.descriptor;

import org.jetbrains.annotations.NotNull;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.points.InterestingPoint;
import usr.afast.image.wrapped.WrappedImage;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static usr.afast.image.algo.AlgoLib.getSobelX;
import static usr.afast.image.algo.AlgoLib.getSobelY;
import static usr.afast.image.descriptor.DescriptorUtil.match;
import static usr.afast.image.points.Harris.makeHarris;
import static usr.afast.image.points.PointsFilter.filterPoints;

public class SIFTProcessor {
    private static final int POINTS = 30;

    public static List<PointsPair> processWithSift(WrappedImage imageA,
                                                   WrappedImage imageB,
                                                   final int gridSize,
                                                   final int cellSize,
                                                   final int binsCount) {
        WrappedImage xA = getSobelX(imageA, BorderHandling.Mirror);
        WrappedImage yA = getSobelY(imageA, BorderHandling.Mirror);
        WrappedImage xB = getSobelX(imageB, BorderHandling.Mirror);
        WrappedImage yB = getSobelY(imageB, BorderHandling.Mirror);

        WrappedImage gradientA = WrappedImage.getGradient(xA, yA);
        WrappedImage gradientAngleA = WrappedImage.getGradientAngle(xA, yA);
        WrappedImage gradientB = WrappedImage.getGradient(xB, yB);
        WrappedImage gradientAngleB = WrappedImage.getGradientAngle(xB, yB);

        List<InterestingPoint> pointsA = filterPoints(makeHarris(imageA), POINTS);
        List<InterestingPoint> pointsB = filterPoints(makeHarris(imageB), POINTS);

        List<SIFTDescriptor> descriptorsA = getDescriptors(gradientA,
                                                           gradientAngleA,
                                                           pointsA,
                                                           gridSize,
                                                           cellSize,
                                                           binsCount);

        List<SIFTDescriptor> descriptorsB = getDescriptors(gradientB,
                                                           gradientAngleB,
                                                           pointsB,
                                                           gridSize,
                                                           cellSize,
                                                           binsCount);

        return match(descriptorsA, descriptorsB);
    }

    private static List<SIFTDescriptor> getDescriptors(WrappedImage gradient,
                                                       WrappedImage gradientAngle,
                                                       @NotNull List<InterestingPoint> interestingPoints,
                                                       final int gridSize,
                                                       final int cellSize,
                                                       final int binsCount) {
        List<SIFTDescriptor> siftDescriptors =
                interestingPoints.stream()
                                 .map(interestingPoint -> SIFTDescriptor.at(gradient,
                                                                            gradientAngle,
                                                                            interestingPoint,
                                                                            gridSize,
                                                                            cellSize,
                                                                            binsCount))
                                 .collect(Collectors.toList());
        siftDescriptors.forEach(AbstractDescriptor::normalize);
        return siftDescriptors;
    }
}
