package usr.afast.image.points;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.util.SeparableMatrix;
import usr.afast.image.wrapped.Matrix;

import java.util.List;
import java.util.stream.Collectors;

import static usr.afast.image.algo.AlgoLib.*;
import static usr.afast.image.math.ConvolutionMatrixFactory.getGaussMatrices;
import static usr.afast.image.math.ConvolutionMatrixFactory.separableMatrixFrom;
import static usr.afast.image.util.DetectorUtil.getCandidates;
import static usr.afast.image.util.ImageIO.write;
import static usr.afast.image.util.Math.sqr;

@SuppressWarnings("Duplicates")
public class Harris {
    private static final String TEMP_PATH = "E:\\test_images\\grid\\temp.png";
    private static double MIN_PROBABILITY = 0.01;
    private static int MAX_SIZE = 80000;
    private static final int WINDOW_RADIUS = 4;

    @NotNull
    public static List<InterestingPoint> makeHarris(@NotNull Matrix image) {
        image = makeGauss(image, WINDOW_RADIUS, BorderHandling.Mirror);
        int width = image.getWidth();
        int height = image.getHeight();

        Matrix harris = getHarrisMat(image, width, height, WINDOW_RADIUS);

//        write(TEMP_PATH, harris);

        List<InterestingPoint> candidates = getCandidates(harris, width, height);

        candidates.sort((a, b) -> Double.compare(b.getProbability(), a.getProbability()));
        candidates = candidates.stream()
                               .filter(candidate -> candidate.getProbability() > MIN_PROBABILITY)
                               .collect(Collectors.toList());

        return candidates.subList(0, Math.min(candidates.size(), MAX_SIZE));
    }

    public static Matrix getHarrisMat(Matrix image, int radius) {
        return getHarrisMat(image, image.getWidth(), image.getHeight(), radius);
    }

    private static Matrix getHarrisMat(Matrix image, int width, int height, int radius) {
        Matrix harrisMat = new Matrix(width, height);

        Matrix xDerivative = getSobelX(image, BorderHandling.Mirror);
        Matrix yDerivative = getSobelY(image, BorderHandling.Mirror);

        SeparableMatrix gauss = separableMatrixFrom(getGaussMatrices(radius));

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double[][] currentMatrix = new double[2][2];
                for (int u = -radius; u <= radius; u++) {
                    for (int v = -radius; v <= radius; v++) {
                        double Ix = xDerivative.getAt(x + u, y + v, BorderHandling.Mirror);
                        double Iy = yDerivative.getAt(x + u, y + v, BorderHandling.Mirror);
                        double gaussPoint = gauss.getAt(u + radius, v + radius);
                        currentMatrix[0][0] += sqr(Ix) * gaussPoint;
                        currentMatrix[0][1] += Ix * Iy * gaussPoint;
                        currentMatrix[1][0] += Ix * Iy * gaussPoint;
                        currentMatrix[1][1] += sqr(Iy) * gaussPoint;
                    }
                }
                double[] eigenvalues = Mat2.from(currentMatrix).getEigenvalues();
                harrisMat.setAt(x, y, Math.min(eigenvalues[0], eigenvalues[1]));
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
