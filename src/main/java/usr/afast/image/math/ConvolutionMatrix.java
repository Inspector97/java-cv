package usr.afast.image.math;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ConvolutionMatrix {
    private Matrix matrix;
    boolean separable;
}
