package usr.afast.image.points;

import org.jetbrains.annotations.NotNull;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.wrapped.WrappedImage;

import java.util.List;
import java.util.stream.Collectors;

import static usr.afast.image.algo.AlgoLib.makeGauss;
import static usr.afast.image.util.DetectorUtil.getCandidates;
import static usr.afast.image.util.Math.sqr;

@SuppressWarnings("Duplicates")
public class Moravec {
    private static double MIN_PROBABILITY = 0.05;
    private static int MAX_SIZE = 2000;
    private static final int[] dx = {-1, 0, 1, -1, 1, -1, 0, -1};
    private static final int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};
    private static final int WINDOW_RADIUS = 2;

    @NotNull
    public static List<InterestingPoint> makeMoravec(@NotNull WrappedImage image) {
        image = makeGauss(image, 0.66, BorderHandling.Mirror);
        int width = image.getWidth();
        int height = image.getHeight();
        double[][] mins = getMinimums(image, width, height);
        List<InterestingPoint> candidates = getCandidates(mins, width, height);
        candidates = candidates.stream()
                               .filter(candidate -> candidate.getProbability() > MIN_PROBABILITY)
                               .collect(Collectors.toList());
        candidates.sort((a, b) -> Double.compare(b.getProbability(), a.getProbability()));
        return candidates.subList(0, Math.min(candidates.size(), MAX_SIZE));
    }


    private static double[][] getMinimums(@NotNull WrappedImage image, int width, int height) {
        double[][] mins = new double[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double min = Double.MAX_VALUE;
                for (int k = 0; k < dx.length; k++) {
                    double sum = 0;
                    for (int u = -WINDOW_RADIUS; u <= WINDOW_RADIUS; u++) {
                        for (int v = -WINDOW_RADIUS; v <= WINDOW_RADIUS; v++) {
                            sum += sqr(image.getPixel(i + u, j + v, BorderHandling.Mirror) -
                                       image.getPixel(i + u + dx[k], j + v + dy[k], BorderHandling.Mirror));
                        }
                    }
                    min = Math.min(min, sum);
                }
                mins[i][j] = min;
            }
        }
        return mins;
    }

}
