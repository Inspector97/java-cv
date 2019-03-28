package usr.afast.image.descriptor;

import org.jetbrains.annotations.NotNull;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.points.InterestingPoint;
import usr.afast.image.wrapped.Matrix;

import java.util.LinkedList;
import java.util.List;

import static usr.afast.image.algo.AlgoLib.getSobelX;
import static usr.afast.image.algo.AlgoLib.getSobelY;
import static usr.afast.image.descriptor.DescriptorUtil.match;
import static usr.afast.image.points.Harris.makeHarris;
import static usr.afast.image.points.PointsFilter.filterPoints;

public class HOGProcessor {
    private static final int POINTS = 50;

    public static Matching process(Matrix imageA,
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

        List<? extends AbstractDescriptor> descriptorsA = getDescriptors(gradientA,
                gradientAngleA,
                pointsA,
                descriptor);

        List<? extends AbstractDescriptor> descriptorsB = getDescriptors(gradientB,
                gradientAngleB,
                pointsB,
                descriptor);

        return match(descriptorsA, descriptorsB);
    }

    private static List<? extends AbstractDescriptor> getDescriptors(Matrix gradient,
                                                                     Matrix gradientAngle,
                                                                     @NotNull List<InterestingPoint> interestingPoints,
                                                                     GistogramBasedDescriptor descriptor) {
        List<AbstractDescriptor> descriptors = new LinkedList<>();
        for (InterestingPoint point : interestingPoints) {
            List<? extends AbstractDescriptor> at = descriptor.at(gradient, gradientAngle, point);
            descriptors.addAll(at);
        }
        descriptors.forEach(AbstractDescriptor::normalize);
        return descriptors;
    }
}
