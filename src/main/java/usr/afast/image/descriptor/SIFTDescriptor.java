package usr.afast.image.descriptor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.math.AngleBin;
import usr.afast.image.points.InterestingPoint;
import usr.afast.image.util.SeparableMatrix;
import usr.afast.image.wrapped.Matrix;

import static usr.afast.image.math.ConvolutionMatrixFactory.getGaussMatrices;
import static usr.afast.image.math.ConvolutionMatrixFactory.separableMatrixFrom;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SIFTDescriptor extends AbstractDescriptor {
    private double[] descriptor;
    private InterestingPoint point;

    public static SIFTDescriptor at(Matrix gradient,
                                    Matrix gradientAngle,
                                    final InterestingPoint point,
                                    final int gridSize,
                                    final int cellSize,
                                    final int binsCount) {
        SIFTDescriptor siftDescriptor = new SIFTDescriptor();
        siftDescriptor.point = point;
        siftDescriptor.descriptor = new double[gridSize * gridSize * binsCount];

        AngleBin[][] bins = new AngleBin[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++)
            for (int j = 0; j < gridSize; j++)
                bins[i][j] = new AngleBin(binsCount);

        double mainAngle = getMainAngle(gradient, gradientAngle, point, gridSize, cellSize);

        int border = gridSize * cellSize;
        int halfBorder = border / 2;

        SeparableMatrix gauss = separableMatrixFrom(getGaussMatrices(halfBorder));
        int left = -halfBorder, right = border - halfBorder;

        for (int x = left; x < right; x++) {
            for (int y = left; y < right; y++) {
                int rotatedX = rotateX(x, y, mainAngle);
                int rotatedY = rotateY(x, y, mainAngle);

                if (rotatedX < left || rotatedX >= right || rotatedY < left || rotatedY >= right) continue;

                int realX = point.getX() + rotatedX;
                int realY = point.getY() + rotatedY;
                double phi = gradientAngle.getAt(realX, realY, BorderHandling.Mirror);
                double gradientValue = gradient.getAt(realX, realY, BorderHandling.Mirror);
                double gaussValue = gauss.getAt(halfBorder + rotatedX, halfBorder + rotatedY);

                putToBin(bins, rotatedX, rotatedY, left, cellSize, phi - mainAngle, gradientValue * gaussValue);
            }
        }
        int ptr = 0;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                System.arraycopy(bins[i][j].getBin(), 0, siftDescriptor.descriptor, ptr, binsCount);
                ptr += binsCount;
            }
        }

        return siftDescriptor;
    }

    private static void putToBin(@NotNull AngleBin[][] bins, int realX, int realY, int left, int cellSize,
                                 double angle, double value) {
        int x = (realX - left) / cellSize;
        int y = (realY - left) / cellSize;
        bins[x][y].addAngle(angle, value);
    }

    private static int rotateX(int x, int y, double angle) {
        return (int) (x * Math.cos(angle) + y * Math.sin(angle));
    }

    private static int rotateY(int x, int y, double angle) {
        return (int) (y * Math.cos(angle) - x * Math.sin(angle));
    }

    private static double getMainAngle(Matrix gradient,
                                       Matrix gradientAngle,
                                       final InterestingPoint point,
                                       final int gridSize,
                                       final int cellSize) {
        final int binSize = 36;
        AngleBin bin = new AngleBin(binSize);

        int border = gridSize * cellSize;
        int halfBorder = border / 2;

        SeparableMatrix gauss = separableMatrixFrom(getGaussMatrices(halfBorder));

        for (int x = -halfBorder; x < border - halfBorder; x++) {
            for (int y = -halfBorder; y < border - halfBorder; y++) {
                int realX = point.getX() + x;
                int realY = point.getY() + y;
                double phi = gradientAngle.getAt(realX, realY, BorderHandling.Mirror);
                double gradientValue = gradient.getAt(realX, realY, BorderHandling.Mirror);
                double gaussValue = gauss.getAt(halfBorder + x, halfBorder + y);

                bin.addAngle(phi, gradientValue * gaussValue);
            }
        }

        return bin.getPeek();
    }

    @Override
    void setDescriptor(double[] descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    double[] getDescriptor() {
        return descriptor;
    }

    @Override
    InterestingPoint getPoint() {
        return point;
    }

}
