package usr.afast.image.descriptor;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DescriptorUtil {
    public static ToDraw match(List<? extends AbstractDescriptor> descriptorsA,
                                          List<? extends AbstractDescriptor> descriptorsB) {
        List<PointsPair> pointsMatching = new LinkedList<>();

        for (AbstractDescriptor descriptorA : descriptorsA) {
            AbstractDescriptor closest = getClosest(descriptorA, descriptorsB);
            if (closest == null) continue;
            AbstractDescriptor closestB = getClosest(closest, descriptorsA);
            if (closestB != descriptorA) continue;
            pointsMatching.add(PointsPair.from(descriptorA.getPoint(), closest.getPoint()));
        }

        return new ToDraw(pointsMatching, new ArrayList<>(descriptorsA), new ArrayList<>(descriptorsB));
    }

    private static AbstractDescriptor getClosest(AbstractDescriptor descriptor,
                                                 @NotNull List<? extends AbstractDescriptor> descriptors) {
        double min = Double.MAX_VALUE;
        AbstractDescriptor selected = null;
        for (AbstractDescriptor patchDescriptor : descriptors) {
            double distance = AbstractDescriptor.distance(descriptor, patchDescriptor);
            if (distance < min) {
                min = distance;
                selected = patchDescriptor;
            }
        }
        return selected;
    }
}
