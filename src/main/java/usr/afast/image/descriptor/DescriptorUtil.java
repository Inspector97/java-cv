package usr.afast.image.descriptor;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class DescriptorUtil {
    private static final double NEXT_NEAREST_DISTANCE_RATIO = 0.8;
    public static ToDraw match(List<? extends AbstractDescriptor> descriptorsA,
                                          List<? extends AbstractDescriptor> descriptorsB) {
        List<PointsPair> pointsMatching = new LinkedList<>();

        for (AbstractDescriptor descriptorA : descriptorsA) {
            AbstractDescriptor closest = getClosest(descriptorA, descriptorsB);
            if (closest == null) continue;
//            AbstractDescriptor closestB = getClosest(closest, descriptorsA);
//            if (closestB != descriptorA) continue;
            pointsMatching.add(PointsPair.from(descriptorA.getPoint(), closest.getPoint()));
        }

        return new ToDraw(pointsMatching, new ArrayList<>(descriptorsA), new ArrayList<>(descriptorsB));
    }

    private static AbstractDescriptor getClosest(AbstractDescriptor descriptor,
                                                 @NotNull List<? extends AbstractDescriptor> descriptors) {
        double[] distances =
                descriptors.stream()
                           .mapToDouble(o -> AbstractDescriptor.distance(descriptor, o))
                           .toArray();
        int a = getClosest(distances, -1);
        int b = getClosest(distances, a);

        double r = distances[a] / distances[b];
        if(r <= NEXT_NEAREST_DISTANCE_RATIO) return descriptors.get(a);
        return null;
    }

    private static int getClosest(double[] distances, int exclude) {
        int selectedIndex = -1;
        for(int i = 0; i < distances.length; i++)
            if(i != exclude && (selectedIndex == -1 || distances[i] < distances[selectedIndex]))
                selectedIndex = i;

        return selectedIndex;
    }
}
