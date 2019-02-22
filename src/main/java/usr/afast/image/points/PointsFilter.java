package usr.afast.image.points;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PointsFilter {

    public static List<InterestingPoint> filterPoints(@NotNull List<InterestingPoint> interestingPoints, int maxSize) {
        if (maxSize >= interestingPoints.size())
            return interestingPoints;
        if (maxSize < 0)
            return new ArrayList<>();

        double l = 0, r = 1e9;
        int cnt = 60;
        while (cnt --> 0) {
            double middle = (l + r) / 2;
            if (filter(interestingPoints, middle).size() > maxSize) {
                l = middle;
            } else {
                r = middle;
            }
        }
        return filter(interestingPoints, l);
    }

    private static List<InterestingPoint> filter(@NotNull List<InterestingPoint> interestingPoints, double radius) {
        List<InterestingPoint> filtered = new LinkedList<>();
        for (InterestingPoint point : interestingPoints) {
            boolean ok = true;
            for (InterestingPoint anotherPoint : interestingPoints) {
                if (point.equals(anotherPoint)) continue;
                if (InterestingPoint.distance(point, anotherPoint) < radius) {
                   ok = false;
                   break;
                }
            }
            if (ok) {
                filtered.add(point);
            }
        }
        return filtered;
    }
}
