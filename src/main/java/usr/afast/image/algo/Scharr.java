package usr.afast.image.algo;

import usr.afast.image.enums.BorderHandling;
import usr.afast.image.wrapped.WrappedImage;

import static usr.afast.image.algo.AlgoLib.getScharrX;
import static usr.afast.image.algo.AlgoLib.getScharrY;

public class Scharr extends GradientProcessor {
    @Override
    public WrappedImage getXImage(WrappedImage wrappedImage, BorderHandling borderHandling) {
        return getScharrX(wrappedImage, borderHandling);
    }

    @Override
    public WrappedImage getYImage(WrappedImage wrappedImage, BorderHandling borderHandling) {
        return getScharrY(wrappedImage, borderHandling);
    }
}
