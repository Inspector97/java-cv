package usr.afast.image.descriptor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.math.AngleBin;
import usr.afast.image.math.ConvolutionMatrix;
import usr.afast.image.math.ConvolutionMatrixFactory;
import usr.afast.image.points.InterestingPoint;
import usr.afast.image.wrapped.WrappedImage;

import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SIFTDescriptor extends AbstractDescriptor {
    private double[] descriptor;
    private InterestingPoint point;

    public static SIFTDescriptor at(WrappedImage gradient,
                                    WrappedImage gradientAngle,
                                    final InterestingPoint point,
                                    final int gridSize,
                                    final int cellSize,
                                    final int binsCount) {
        SIFTDescriptor siftDescriptor = new SIFTDescriptor();
        siftDescriptor.point = point;
        siftDescriptor.descriptor = new double[gridSize * gridSize * binsCount];

        int gridHalfSize = gridSize / 2;

        ConvolutionMatrix gauss = ConvolutionMatrixFactory.getGaussMatrix(gridHalfSize * cellSize);

        int ptr = 0;
        int centerShift = gridHalfSize * cellSize;

        for (int cellX = -gridHalfSize; cellX < gridSize - gridHalfSize; cellX++) {
            for (int cellY = -gridHalfSize; cellY < gridSize - gridHalfSize; cellY++) {

                AngleBin bin = new AngleBin(binsCount);

                for (int pixelX = 0; pixelX < cellSize; pixelX++) {
                    for (int pixelY = 0; pixelY < cellSize; pixelY++) {
                        int realX = point.getX() + cellX * cellSize + pixelX;
                        int realY = point.getY() + cellY * cellSize + pixelY;

                        double phi = gradientAngle.getPixel(realX, realY, BorderHandling.Mirror);
                        double gradientValue = gradient.getPixel(realX, realY, BorderHandling.Mirror);
                        double gaussValue = gauss.get(centerShift + cellX * cellSize + pixelX,
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
