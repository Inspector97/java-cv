package usr.afast.image.descriptor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.math.AngleBin;
import usr.afast.image.points.InterestingPoint;
import usr.afast.image.util.SeparableMatrix;
import usr.afast.image.wrapped.Matrix;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static usr.afast.image.math.ConvolutionMatrixFactory.getGaussMatrices;
import static usr.afast.image.math.ConvolutionMatrixFactory.separableMatrixFrom;

@SuppressWarnings("Duplicates")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HOGDescriptor extends AbstractDescriptor {
    private double[] descriptor;
    private InterestingPoint point;

    public static List<HOGDescriptor> at(Matrix gradient,
                                         Matrix gradientAngle,
                                         final InterestingPoint point,
                                         final int gridSize,
                                         final int cellSize,
                                         final int binsCount) {
        HOGDescriptor hogDescriptor = new HOGDescriptor();
        hogDescriptor.point = point;
        hogDescriptor.descriptor = new double[gridSize * gridSize * binsCount];

        AngleBin[][] bins = new AngleBin[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++)
            for (int j = 0; j < gridSize; j++)
                bins[i][j] = new AngleBin(binsCount);

        int border = gridSize * cellSize;
        int halfBorder = border / 2;

        SeparableMatrix gauss = separableMatrixFrom(getGaussMatrices(halfBorder));
        int left = -halfBorder, right = border - halfBorder;

        for (int x = left; x < right; x++) {
            for (int y = left; y < right; y++) {
                int realX = point.getX() + x;
                int realY = point.getY() + y;
                double phi = gradientAngle.getAt(realX, realY, BorderHandling.Mirror);
                double gradientValue = gradient.getAt(realX, realY, BorderHandling.Mirror);
                double gaussValue = gauss.getAt(halfBorder + x, halfBorder + y);

                putToBin(bins, x, y, left, cellSize, phi, gradientValue * gaussValue);
            }
        }
        int ptr = 0;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                System.arraycopy(bins[i][j].getBin(), 0, hogDescriptor.descriptor, ptr, binsCount);
                ptr += binsCount;
            }
        }

        return Collections.singletonList(hogDescriptor);
    }

    private static void putToBin(@NotNull AngleBin[][] bins, int realX, int realY, int left, int cellSize,
                                 double angle, double value) {
        int x = (realX - left) / cellSize;
        int y = (realY - left) / cellSize;
        bins[x][y].addAngle(angle, value);
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
