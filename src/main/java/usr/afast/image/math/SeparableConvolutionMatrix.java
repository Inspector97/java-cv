package usr.afast.image.math;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SeparableConvolutionMatrix extends ConvolutionMatrix {
    private Vector xVector;
    private Vector yVector;

    @Override
    public double get(int x, int y) {
        return xVector.getVector()[x] * yVector.getVector()[y];
    }
}
