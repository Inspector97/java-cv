package usr.afast.image.impl.algo;

import usr.afast.image.annotation.ProcessingOf;
import usr.afast.image.annotation.Singleton;
import usr.afast.image.api.ImageProcessor;
import usr.afast.image.enums.AlgorithmType;
import usr.afast.image.wrapped.WrappedImage;

@ProcessingOf(AlgorithmType.Sobel)
@Singleton
public class Sobel implements ImageProcessor {
    @Override
    public WrappedImage process(WrappedImage image, AlgorithmType algorithmType, Object... args) {
        //just for fist time
        image.forEach(pixel -> pixel + 3);
        return image;
    }
}
