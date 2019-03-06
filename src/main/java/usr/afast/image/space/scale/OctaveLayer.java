package usr.afast.image.space.scale;

import lombok.AllArgsConstructor;
import lombok.Getter;
import usr.afast.image.wrapped.Matrix;

@Getter
@AllArgsConstructor
public class OctaveLayer {
    private int index;
    private double localSigma;
    private double globalSigma;
    private Matrix image;
}
