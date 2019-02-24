package usr.afast.image.math;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NonSeparableConvolutionMatrix extends ConvolutionMatrix {
    private Matrix matrix;

    @Override
    public double get(int x, int y) {
        return matrix.getMatrix()[x][y];
    }
}
