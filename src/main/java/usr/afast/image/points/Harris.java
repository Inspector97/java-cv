package usr.afast.image.points;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.math.ConvolutionMatrix;
import usr.afast.image.wrapped.WrappedImage;

import java.util.List;
import java.util.stream.Collectors;

import static usr.afast.image.algo.AlgoLib.*;
import static usr.afast.image.math.ConvolutionMatrixFactory.getGaussMatrix;
import static usr.afast.image.util.DetectorUtil.getCandidates;
import static usr.afast.image.util.Math.sqr;

@SuppressWarnings("Duplicates")
public class Harris {
    private static final String TEMP_PATH = "E:\\test_images\\grid\\temp.png";
    private static double MIN_PROBABILITY = 0.05;
    private static int MAX_SIZE = 800;
    private static final int WINDOW_RADIUS = 4;

    @NotNull
    public static List<InterestingPoint> makeHarris(@NotNull WrappedImage image) {
        image = makeGauss(image, WINDOW_RADIUS, BorderHandling.Mirror);
        int width = image.getWidth();
        int height = image.getHeight();

        double[][] harris = getHarrisMat(image, width, height);

        WrappedImage temp = new WrappedImage(width, height);
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                temp.setPixel(i, j, harris[i][j]);

//        write(TEMP_PATH, temp);

        List<InterestingPoint> candidates = getCandidates(harris, width, height);

        candidates.sort((a, b) -> Double.compare(b.getProbability(), a.getProbability()));
        candidates = candidates.stream()
                               .filter(candidate -> candidate.getProbability() > MIN_PROBABILITY)
                               .collect(Collectors.toList());

        return candidates.subList(0, Math.min(candidates.size(), MAX_SIZE));
    }

    private static double[][] getHarrisMat(WrappedImage image, int width, int height) {
        double[][] harrisMat = new double[width][height];

        WrappedImage xDerivative = getSobelX(image, BorderHandling.Mirror);
        WrappedImage yDerivative = getSobelY(image, BorderHandling.Mirror);

        ConvolutionMatrix gauss = getGaussMatrix(WINDOW_RADIUS);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double[][] currentMatrix = new double[2][2];
                for (int u = -WINDOW_RADIUS; u <= WINDOW_RADIUS; u++) {
                    for (int v = -WINDOW_RADIUS; v <= WINDOW_RADIUS; v++) {
                        double Ix = xDerivative.getPixel(x + u, y + v, BorderHandling.Mirror);
                        double Iy = yDerivative.getPixel(x + u, y + v, BorderHandling.Mirror);
                        double gaussPoint = gauss.get(u + WINDOW_RADIUS, v + WINDOW_RADIUS);
                        currentMatrix[0][0] += sqr(Ix) * gaussPoint;
                        currentMatrix[0][1] += Ix * Iy * gaussPoint;
                        currentMatrix[1][0] += Ix * Iy * gaussPoint;
                        currentMatrix[1][1] += sqr(Iy) * gaussPoint;
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
            if (Math.abs(d) < 1e-4)
                d = 0;
            if (d < 0) {
                return eigenvalues;
            }

            eigenvalues[0] = (-b + Math.sqrt(d)) / (2 * a);
            eigenvalues[1] = (-b - Math.sqrt(d)) / (2 * a);

            return eigenvalues;
        }
    }

}
