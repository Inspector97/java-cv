package usr.afast.image.api;

import usr.afast.image.enums.AlgorithmType;
import usr.afast.image.wrapped.WrappedImage;

public interface ImageProcessor {
    WrappedImage process(WrappedImage image, AlgorithmType algorithmType, Object... args);
}
