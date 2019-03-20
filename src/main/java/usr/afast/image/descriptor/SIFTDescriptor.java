package usr.afast.image.descriptor;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.math.AngleBin;
import usr.afast.image.points.InterestingPoint;
import usr.afast.image.util.SeparableMatrix;
import usr.afast.image.wrapped.Matrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static usr.afast.image.math.ConvolutionMatrixFactory.getGaussMatrices;
import static usr.afast.image.math.ConvolutionMatrixFactory.separableMatrixFrom;
import static usr.afast.image.util.Math.sign;
import static usr.afast.image.util.Math.sqr;

@SuppressWarnings("Duplicates")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SIFTDescriptor extends AbstractDescriptor {
    private static final double SQRT_2 = Math.sqrt(2);
    private double[] descriptor;
    private InterestingPoint point;

//    public static List<SIFTDescriptor> at(final Matrix gradient,
//                                          final Matrix gradientAngle,
//                                          final InterestingPoint point,
//                                          final int gridSize,
//                                          final int cellSize,
//                                          final int binsCount) {
//        return at(gradient, gradientAngle, point, gridSize, cellSize, binsCount, 1, 1);
//    }

    public static List<SIFTDescriptor> at(final Matrix gradient,
                                          final Matrix gradientAngle,
                                          final InterestingPoint point,
                                          final int gridSize,
                                          final int binsCount) {
        List<SIFTDescriptor> descriptorList = new LinkedList<>();
        double[] mainAngles = getMainAngles(gradient, gradientAngle, point, gridSize);

        for (double mainAngle : mainAngles) {
            SIFTDescriptor siftDescriptor = new SIFTDescriptor();
            siftDescriptor.descriptor = new double[gridSize * gridSize * binsCount];

            AngleBin[][] bins = new AngleBin[gridSize][gridSize];
            for (int i = 0; i < gridSize; i++)
                for (int j = 0; j < gridSize; j++)
                    bins[i][j] = new AngleBin(binsCount);

            siftDescriptor.point = point.toBuilder().build().setAngle(mainAngle);

            int border = (int) Math.ceil(point.getScale() * SQRT_2 * 2);
            int halfBorder = border / 2;

            SeparableMatrix gauss = separableMatrixFrom(getGaussMatrices(halfBorder, 0.5 * halfBorder));
            int left = -halfBorder, right = border - halfBorder;

            double cellSize = border * 1.0 / gridSize;

            for (int x = left; x < right; x++) {
                for (int y = left; y < right; y++) {
                    double rotatedX = rotateX(x, y, mainAngle);
                    double rotatedY = rotateY(x, y, mainAngle);

                    if (rotatedX < left || rotatedX >= right || rotatedY < left || rotatedY >= right) continue;

                    int realX = (int) (point.getScaledX() + x);
                    int realY = (int) (point.getScaledY() + y);
                    double phi = gradientAngle.getAt(realX, realY, BorderHandling.Mirror);
                    double gradientValue = gradient.getAt(realX, realY, BorderHandling.Mirror);
                    double gaussValue = gauss.getAt((int) (halfBorder + rotatedX), (int) (halfBorder + rotatedY));

                    putToBin(bins, rotatedX, rotatedY, left, cellSize, phi + mainAngle, gradientValue * gaussValue);
                }
            }
            int ptr = 0;
            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    System.arraycopy(bins[i][j].getBin(), 0, siftDescriptor.descriptor, ptr, binsCount);
                    ptr += binsCount;
                }
            }
            descriptorList.add(siftDescriptor);
        }

        return descriptorList;
    }

    private static void putToBin(@NotNull AngleBin[][] bins, double realX, double realY, int left, double cellSize,
                                 double angle, double value) {
        int x = (int) ((realX - left) / cellSize);
        int y = (int) ((realY - left) / cellSize);

        double cellRadius = cellSize / 2D;
        double cellCenterX = left + x * cellSize + cellRadius;
        double cellCenterY = left + y * cellSize + cellRadius;
        int pointXSign = sign(realX - cellCenterX);
        int pointYSign = sign(realY - cellCenterY);

        Distribution[] distributions = new Distribution[4];
        int ptr = 0;

        int binsSize = bins.length;
        double sum = 0;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (x + dx < 0 || x + dx >= binsSize || y + dy < 0 || y + dy >= binsSize)
                    continue;
                if (dx * pointXSign >= 0 && dy * pointYSign >= 0) {
                    double neighbourX = left + (x + dx) * cellSize + cellRadius;
                    double neighbourY = left + (y + dy) * cellSize + cellRadius;

                    double distance = Math.sqrt(sqr(neighbourX - realX) + sqr(neighbourY - realY));

                    distributions[ptr++] = new Distribution(x + dx, y + dy, distance);
                    sum += distance;
                }
            }
        }

        for (int i = 0; i < ptr; i++) {
            bins[distributions[i].x][distributions[i].y].addAngle(angle,
                                                                  value * (1 - distributions[i].distance / sum));
        }
    }

    @AllArgsConstructor
    @Getter
    private static class Distribution {
        private int x, y;
        private double distance;
    }

    private static double rotateX(double x, double y, double angle) {
        return (x * Math.cos(angle) + y * Math.sin(angle));
    }

    private static double rotateY(double x, double y, double angle) {
        return (y * Math.cos(angle) - x * Math.sin(angle));
    }

    private static double[] getMainAngles(Matrix gradient,
                                          Matrix gradientAngle,
                                          final InterestingPoint point,
                                          final int gridSize) {
        final int binSize = 36;
        AngleBin bin = new AngleBin(binSize);

        double radius = point.getScale() * SQRT_2;

        int border = (int) Math.ceil(radius * 2);
        int halfBorder = border / 2;

        SeparableMatrix gauss = separableMatrixFrom(getGaussMatrices(halfBorder, 1.5 * point.getScale()));

        int actualX = (int) point.getScaledX();
        int actualY = (int) point.getScaledY();

        for (int x = -halfBorder; x < border - halfBorder; x++) {
            for (int y = -halfBorder; y < border - halfBorder; y++) {
                int realX = actualX + x;
                int realY = actualY + y;
                double phi = gradientAngle.getAt(realX, realY, BorderHandling.Mirror);
                double gradientValue = gradient.getAt(realX, realY, BorderHandling.Mirror);
                double gaussValue = gauss.getAt(halfBorder + x, halfBorder + y);

                bin.addAngle(phi, gradientValue * gaussValue);
            }
        }

        double[] peeks = bin.getPeeks();
        for (int i = 0; i < peeks.length; i++)
            peeks[i] = 2 * Math.PI - peeks[i];

        return peeks;
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
    public InterestingPoint getPoint() {
        return point;
    }

}
