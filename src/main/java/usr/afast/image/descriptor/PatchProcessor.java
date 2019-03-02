package usr.afast.image.descriptor;

import org.jetbrains.annotations.NotNull;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.points.InterestingPoint;
import usr.afast.image.wrapped.WrappedImage;

import java.util.List;
import java.util.stream.Collectors;

import static usr.afast.image.algo.AlgoLib.getSobelX;
import static usr.afast.image.algo.AlgoLib.getSobelY;
import static usr.afast.image.descriptor.DescriptorUtil.match;
import static usr.afast.image.points.Harris.makeHarris;
import static usr.afast.image.points.PointsFilter.filterPoints;

public class PatchProcessor {
    private static final int POINTS = 30;

    public static List<PointsPair> processWithPatches(WrappedImage imageA, WrappedImage imageB, final int gridHalfSize,
                                                      final int cellHalfSize) {
        WrappedImage gradientA = getGradient(imageA);
        WrappedImage gradientB = getGradient(imageB);

        gradientA.normalize();
        gradientB.normalize();

        List<InterestingPoint> pointsA = filterPoints(makeHarris(imageA), POINTS);
        List<InterestingPoint> pointsB = filterPoints(makeHarris(imageB), POINTS);

        List<PatchDescriptor> descriptorsA = getDescriptors(gradientA, pointsA, gridHalfSize, cellHalfSize);
        List<PatchDescriptor> descriptorsB = getDescriptors(gradientB, pointsB, gridHalfSize, cellHalfSize);

        return match(descriptorsA, descriptorsB);
    }

    private static List<PatchDescriptor> getDescriptors(WrappedImage gradient,
                                                        @NotNull List<InterestingPoint> interestingPoints,
                                                        final int gridHalfSize,
                                                        final int cellHalfSize) {
        List<PatchDescriptor> patchDescriptors =
                interestingPoints.stream()
                                 .map(interestingPoint -> PatchDescriptor.at(gradient,
                                                                             interestingPoint,
                                                                             gridHalfSize,
                                                                             cellHalfSize))
                                 .collect(Collectors.toList());
        patchDescriptors.forEach(AbstractDescriptor::normalize);
        return patchDescriptors;
    }

    private static WrappedImage getGradient(WrappedImage image) {
        WrappedImage x = getSobelX(image, BorderHandling.Mirror);
        WrappedImage y = getSobelY(image, BorderHandling.Mirror);
        return WrappedImage.getGradient(x, y);
    }
}
