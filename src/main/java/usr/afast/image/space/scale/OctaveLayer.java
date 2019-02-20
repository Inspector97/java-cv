package usr.afast.image.space.scale;

import lombok.AllArgsConstructor;
import lombok.Getter;
import usr.afast.image.wrapped.WrappedImage;

@Getter
@AllArgsConstructor
public class OctaveLayer {
    private double localSigma;
    private double globalSigma;
    private WrappedImage image;
}
