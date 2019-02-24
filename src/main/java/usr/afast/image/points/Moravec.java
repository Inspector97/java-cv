package usr.afast.image.points;

import org.jetbrains.annotations.NotNull;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.wrapped.WrappedImage;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static usr.afast.image.util.Math.sqr;

public class Moravec {
    private static double MIN_PROBABILITY = 0.1;
    private static int MAX_SIZE = 1000;
    private static final int[] dx = {-1, 0, 1, -1, 1, -1, 0, -1};
    private static final int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};

    public static List<InterestingPoint> makeMoravec(@NotNull WrappedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        double[][] errors = getErrors(image);
        double[][] mins = getMinimums(errors, width, height);
        List<InterestingPoint> candidates = getCandidates(mins, width, height);
        candidates = candidates.stream()
                               .filter(candidate -> candidate.getProbability() > MIN_PROBABILITY)
                               .collect(Collectors.toList());
        candidates.sort((a, b) -> Double.compare(b.getProbability(), a.getProbability()));
        return candidates.subList(0, Math.min(candidates.size(), MAX_SIZE));
    }

    private static List<InterestingPoint> getCandidates(double[][] mins, int width, int height) {
        List<InterestingPoint> candidates = new LinkedList<>();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                boolean ok = true;
                double currentValue = mins[i][j];
                for (int k = 0; k < dx.length && ok; k++) {
                    if (i + dx[k] < 0 ||
                        i + dx[k] >= width ||
                        j + dy[k] < 0 ||
                        j + dy[k] >= height) continue;
                    if (currentValue < mins[i + dx[k]][j + dy[k]])
                        ok = false;
                }
                if (ok) {
                    candidates.add(InterestingPoint.at(i, j, mins[i][j]));
                }
            }
        }
        return candidates;
    }

    private static double[][] getMinimums(double[][] errors, int width, int height) {
        double[][] mins = new double[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double min = Double.MAX_VALUE;
                for (int k = 0; k < dx.length; k++) {
                    if (i + dx[k] < 0 ||
                        i + dx[k] >= width ||
                        j + dy[k] < 0 ||
                        j + dy[k] >= height) continue;
                    min = Math.min(min, errors[i + dx[k]][j + dy[k]]);
                }
                mins[i][j] = min;
            }
        }
        return mins;
    }

    private static double[][] getErrors(@NotNull WrappedImage image) {
        double[][] c = new double[image.getWidth()][image.getHeight()];
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                double sum = 0;
                double pixel = image.getPixel(i, j);
                for (int k = 0; k < dx.length; k++) {
                    sum += sqr(pixel - image.getPixel(i + dx[k], j + dy[k], BorderHandling.Mirror));
                }
                c[i][j] = sum;
            }
        }
        return c;
    }

}
