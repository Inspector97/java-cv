package usr.afast.image.descriptor;

import org.jetbrains.annotations.NotNull;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.points.InterestingPoint;
import usr.afast.image.wrapped.Matrix;

import java.util.List;
import java.util.stream.Collectors;

import static usr.afast.image.algo.AlgoLib.getSobelX;
import static usr.afast.image.algo.AlgoLib.getSobelY;
import static usr.afast.image.descriptor.DescriptorUtil.match;
import static usr.afast.image.points.Harris.makeHarris;
import static usr.afast.image.points.PointsFilter.filterPoints;

public class SIFTProcessor {
    private static final int POINTS = 30;

    public static List<PointsPair> processWithSift(Matrix imageA,
                                                   Matrix imageB,
                                                   final int gridSize,
                                                   final int cellSize,
                                                   final int binsCount) {
        Matrix xA = getSobelX(imageA, BorderHandling.Mirror);
        Matrix yA = getSobelY(imageA, BorderHandling.Mirror);
        Matrix xB = getSobelX(imageB, BorderHandling.Mirror);
        Matrix yB = getSobelY(imageB, BorderHandling.Mirror);

        Matrix gradientA = Matrix.getGradient(xA, yA);
        Matrix gradientAngleA = Matrix.getGradientAngle(xA, yA);
        Matrix gradientB = Matrix.getGradient(xB, yB);
        Matrix gradientAngleB = Matrix.getGradientAngle(xB, yB);

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

    private static List<SIFTDescriptor> getDescriptors(Matrix gradient,
                                                       Matrix gradientAngle,
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
