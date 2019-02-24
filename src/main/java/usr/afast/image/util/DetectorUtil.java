package usr.afast.image.util;

import usr.afast.image.points.InterestingPoint;

import java.util.LinkedList;
import java.util.List;

public class DetectorUtil {
    private static final int[] dx = {-1, 0, 1, -1, 1, -1, 0, -1};
    private static final int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};
    public static List<InterestingPoint> getCandidates(double[][] values, int width, int height) {
        List<InterestingPoint> candidates = new LinkedList<>();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                boolean ok = true;
                double currentValue = values[i][j];
                for (int k = 0; k < dx.length && ok; k++) {
                    if (i + dx[k] < 0 ||
                        i + dx[k] >= width ||
                        j + dy[k] < 0 ||
                        j + dy[k] >= height) continue;
                    if (currentValue < values[i + dx[k]][j + dy[k]])
                        ok = false;
                }
                if (ok) {
                    candidates.add(InterestingPoint.at(i, j, values[i][j]));
                }
            }
        }
        return candidates;
    }
}
