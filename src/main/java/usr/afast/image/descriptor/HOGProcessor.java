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

public class HOGProcessor {
    private static final int POINTS = 30;

    public static List<PointsPair> process(Matrix imageA,
                                           Matrix imageB,
                                           GistogramBasedDescriptor descriptor) {
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

        List<AbstractDescriptor> descriptorsA = getDescriptors(gradientA,
                                                               gradientAngleA,
                                                               pointsA,
                                                               descriptor);

        List<AbstractDescriptor> descriptorsB = getDescriptors(gradientB,
                                                               gradientAngleB,
                                                               pointsB,
                                                               descriptor);

        return match(descriptorsA, descriptorsB);
    }

    private static List<AbstractDescriptor> getDescriptors(Matrix gradient,
                                                           Matrix gradientAngle,
                                                           @NotNull List<InterestingPoint> interestingPoints,
                                                           GistogramBasedDescriptor descriptor) {
        List<AbstractDescriptor> HOGDescriptors =
                interestingPoints.stream()
                                 .map(interestingPoint -> descriptor.at(gradient,
                                                                        gradientAngle,
                                                                        interestingPoint))
                                 .collect(Collectors.toList());
        HOGDescriptors.forEach(AbstractDescriptor::normalize);
        return HOGDescriptors;
    }
}
