package usr.afast.image.descriptor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
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

        int gridHalfSize = gridSize / 2;

        SeparableMatrix gauss = separableMatrixFrom(getGaussMatrices(gridHalfSize * cellSize));

        int ptr = 0;
        int centerShift = gridHalfSize * cellSize;

        for (int cellX = -gridHalfSize; cellX < gridSize - gridHalfSize; cellX++) {
            for (int cellY = -gridHalfSize; cellY < gridSize - gridHalfSize; cellY++) {

                AngleBin bin = new AngleBin(binsCount);

                for (int pixelX = 0; pixelX < cellSize; pixelX++) {
                    for (int pixelY = 0; pixelY < cellSize; pixelY++) {
                        int realX = point.getX() + cellX * cellSize + pixelX;
                        int realY = point.getY() + cellY * cellSize + pixelY;

                        double phi = gradientAngle.getAt(realX, realY, BorderHandling.Mirror);
                        double gradientValue = gradient.getAt(realX, realY, BorderHandling.Mirror);
                        double gaussValue = gauss.getAt(centerShift + cellX * cellSize + pixelX,
                                                        centerShift + cellY * cellSize + pixelY);

                        bin.addAngle(phi, gradientValue * gaussValue);
                    }
                }

                System.arraycopy(bin.getBin(), 0, siftDescriptor.descriptor, ptr, binsCount);
                ptr += binsCount;
            }
        }

        return siftDescriptor;
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
