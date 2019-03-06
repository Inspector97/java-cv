package usr.afast.image.descriptor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.points.InterestingPoint;
import usr.afast.image.wrapped.Matrix;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PatchDescriptor extends AbstractDescriptor {
    private double[] descriptor;
    private InterestingPoint point;

    public static PatchDescriptor at(Matrix gradient, final InterestingPoint point, final int gridHalfSize,
                                     final int cellHalfSize) {
        PatchDescriptor patchDescriptor = new PatchDescriptor();
        patchDescriptor.point = point;
        int size = gridHalfSize * 2 + 1;
        patchDescriptor.descriptor = new double[size * size];

        int cellSize = cellHalfSize * 2 + 1;
        int ptr = 0;

        for (int cellX = -gridHalfSize; cellX <= gridHalfSize; cellX++) {
            for (int cellY = -gridHalfSize; cellY <= gridHalfSize; cellY++) {

                double sum = 0;

                for (int pixelX = -cellHalfSize; pixelX <= cellHalfSize; pixelX++) {
                    for (int pixelY = -cellHalfSize; pixelY <= cellHalfSize; pixelY++) {
                        int realX = point.getX() + cellX * cellSize + pixelX;
                        int realY = point.getY() + cellY * cellSize + pixelY;

                        sum += gradient.getAt(realX, realY, BorderHandling.Mirror);
                    }
                }

                sum /= cellSize * cellSize;
                patchDescriptor.descriptor[ptr++] = sum;
            }
        }

        return patchDescriptor;
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
