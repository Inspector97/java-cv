package usr.afast.image.descriptor;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class DescriptorUtil {
    public static List<PointsPair> match(List<? extends AbstractDescriptor> descriptorsA,
                                          List<? extends AbstractDescriptor> descriptorsB) {
        List<PointsPair> pointsMatchings = new LinkedList<>();

        for (AbstractDescriptor descriptorA : descriptorsA) {
            AbstractDescriptor closest = getClosest(descriptorA, descriptorsB);
            AbstractDescriptor closestB = getClosest(closest, descriptorsA);
            if (closestB != descriptorA) continue;
            pointsMatchings.add(PointsPair.from(descriptorA.getPoint(), closest.getPoint()));
        }

        return pointsMatchings;
    }

    private static AbstractDescriptor getClosest(AbstractDescriptor descriptor,
                                                 @NotNull List<? extends AbstractDescriptor> descriptors) {
        double min = Double.MAX_VALUE;
        AbstractDescriptor selected = null;
        for (AbstractDescriptor patchDescriptor : descriptors) {
            double distance = AbstractDescriptor.distance(descriptor, patchDescriptor);
            if (AbstractDescriptor.distance(descriptor, patchDescriptor) < min) {
                min = distance;
                selected = patchDescriptor;
            }
        }
        return selected;
    }
}
