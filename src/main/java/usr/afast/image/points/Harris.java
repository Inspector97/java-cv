package usr.afast.image.points;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.wrapped.WrappedImage;

import java.util.List;
import java.util.stream.Collectors;

import static usr.afast.image.algo.AlgoLib.*;
import static usr.afast.image.util.DetectorUtil.getCandidates;
import static usr.afast.image.util.Math.sqr;

@SuppressWarnings("Duplicates")
public class Harris {
    private static double MIN_PROBABILITY = 0.5;
    private static int MAX_SIZE = 2000;
    private static final int WINDOW_RADIUS = 2;

    @NotNull
    public static List<InterestingPoint> makeHarris(@NotNull WrappedImage image) {
        image = makeGauss(image, 0.66, BorderHandling.Mirror);
        int width = image.getWidth();
        int height = image.getHeight();

        double[][] harris = getHarrisMat(image, width, height);

        List<InterestingPoint> candidates = getCandidates(harris, width, height);

        candidates = candidates.stream()
                               .filter(candidate -> candidate.getProbability() > MIN_PROBABILITY)
                               .collect(Collectors.toList());

        candidates.sort((a, b) -> Double.compare(b.getProbability(), a.getProbability()));
        return candidates.subList(0, Math.min(candidates.size(), MAX_SIZE));
    }

    private static double[][] getHarrisMat(WrappedImage image, int width, int height) {
        double[][] harrisMat = new double[width][height];

        WrappedImage xDerivative = getSobelX(image, BorderHandling.Mirror);
        WrappedImage yDerivative = getSobelY(image, BorderHandling.Mirror);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double[][] currentMatrix = new double[2][2];
                for (int u = -WINDOW_RADIUS; u <= WINDOW_RADIUS; u++) {
                    for (int v = -WINDOW_RADIUS; v <= WINDOW_RADIUS; v++) {
                        double Ix = xDerivative.getPixel(x + u, y + v, BorderHandling.Mirror);
                        double Iy = yDerivative.getPixel(x + u, y + v, BorderHandling.Mirror);
                        currentMatrix[0][0] += sqr(Ix);
                        currentMatrix[0][1] += Ix * Iy;
                        currentMatrix[1][0] += Ix * Iy;
                        currentMatrix[1][1] += sqr(Iy);
                    }
                }
                double[] eigenvalues = Mat2.from(currentMatrix).getEigenvalues();
                harrisMat[x][y] = Math.min(eigenvalues[0], eigenvalues[1]);
            }
        }
        return harrisMat;
    }


    @AllArgsConstructor(staticName = "from")
    static class Mat2 {
        double[][] matrix;

        double[] getEigenvalues() {
            double[] eigenvalues = new double[2];

            double a = 1;
            double b = -matrix[0][0] - matrix[1][1];
            double c = matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
            double d = sqr(b) - 4 * a * c;

            if (d < 0) {
                return eigenvalues;
            }

            eigenvalues[0] = (-b + Math.sqrt(d)) / (2 * a);
            eigenvalues[1] = (-b - Math.sqrt(d)) / (2 * a);

            return eigenvalues;
        }
    }

}
