package usr.afast.image.points;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

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

    public static List<InterestingPoint> filterPointsFast(List<InterestingPoint> interestingPoints, int maxSize) {
        if (maxSize >= interestingPoints.size())
            return interestingPoints;
        if (maxSize < 0)
            return new ArrayList<>();

        Map<InterestingPoint, PointToCompare> closest = new HashMap<>();
        for (InterestingPoint point : interestingPoints) {
            double distance = Double.MAX_VALUE;
            for (InterestingPoint anotherPoint : interestingPoints) {
                if (point.equals(anotherPoint)) continue;
                if (anotherPoint.getProbability() > point.getProbability()) {
                    if (distance > InterestingPoint.distance(point, anotherPoint)) {
                        distance = InterestingPoint.distance(point, anotherPoint);
                    }
                }
            }
            closest.put(point, new PointToCompare(point, distance));
        }

        List<PointToCompare> pointsWithDistance = new ArrayList<>(closest.values());
        pointsWithDistance.sort(Collections.reverseOrder());

        return pointsWithDistance.subList(0, maxSize).stream()
                                 .map(pointToCompare -> pointToCompare.point)
                                 .collect(Collectors.toList());
    }

    @Getter
    @AllArgsConstructor
    static class PointToCompare implements Comparable<PointToCompare> {
        private InterestingPoint point;
        private Double distance;

        @Override
        public int compareTo(@NotNull PointsFilter.PointToCompare o) {
            return distance.compareTo(o.distance);
        }
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
