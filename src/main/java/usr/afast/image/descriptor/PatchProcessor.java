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
import static usr.afast.image.points.Harris.makeHarris;
import static usr.afast.image.points.PointsFilter.filterPointsFast;

public class PatchProcessor {
    private static final int POINTS = 30;

    public static List<PointsPair> processWithPatches(WrappedImage imageA, WrappedImage imageB, final int gridHalfSize,
                                                          final int cellHalfSize) {
        WrappedImage gradientA = getGradient(imageA);
        WrappedImage gradientB = getGradient(imageB);

        gradientA.normalize();
        gradientB.normalize();

        List<InterestingPoint> pointsA = filterPointsFast(makeHarris(imageA), POINTS);
        List<InterestingPoint> pointsB = filterPointsFast(makeHarris(imageB), POINTS);

        List<PatchDescriptor> descriptorsA = getDescriptors(gradientA, pointsA, gridHalfSize, cellHalfSize);
        List<PatchDescriptor> descriptorsB = getDescriptors(gradientB, pointsB, gridHalfSize, cellHalfSize);

        return match(descriptorsA, descriptorsB);
    }

    private static List<PointsPair> match(List<PatchDescriptor> descriptorsA, List<PatchDescriptor> descriptorsB) {
        List<PointsPair> pointsMatchings = new LinkedList<>();

        for (PatchDescriptor patchDescriptorA : descriptorsA) {
            PatchDescriptor closest = getClosest(patchDescriptorA, descriptorsB);
            PatchDescriptor closestB = getClosest(closest, descriptorsA);
            if (closestB != patchDescriptorA) continue;
            pointsMatchings.add(PointsPair.from(patchDescriptorA.getPoint(), closest.getPoint()));
        }

        return pointsMatchings;
    }

    private static PatchDescriptor getClosest(PatchDescriptor descriptor, @NotNull List<PatchDescriptor> descriptors) {
        double min = Double.MAX_VALUE;
        PatchDescriptor selected = null;
        for (PatchDescriptor patchDescriptor : descriptors) {
            double distance = AbstractDescriptor.distance(descriptor, patchDescriptor);
            if (AbstractDescriptor.distance(descriptor, patchDescriptor) < min) {
                min = distance;
                selected = patchDescriptor;
            }
        }
        return selected;
    }

    private static List<PatchDescriptor> getDescriptors(WrappedImage gradient,
                                                        @NotNull List<InterestingPoint> interestingPoints,
                                                        final int gridHalfSize,
                                                        final int cellHalfSize) {
        return interestingPoints.stream()
                                .map(interestingPoint -> PatchDescriptor.at(gradient,
                                                                            interestingPoint,
                                                                            gridHalfSize,
                                                                            cellHalfSize))
                                .collect(Collectors.toList());
    }

    private static WrappedImage getGradient(WrappedImage image) {
        WrappedImage x = getSobelX(image, BorderHandling.Mirror);
        WrappedImage y = getSobelY(image, BorderHandling.Mirror);
        return WrappedImage.getGradient(x, y);
    }
}
