package usr.afast.image.algo;

import usr.afast.image.enums.BorderHandling;
import usr.afast.image.wrapped.WrappedImage;

import static usr.afast.image.algo.AlgoLib.getPruittX;
import static usr.afast.image.algo.AlgoLib.getPruittY;

public class Pruitt extends GradientProcessor {
    @Override
    public WrappedImage getXImage(WrappedImage wrappedImage, BorderHandling borderHandling) {
        return getPruittX(wrappedImage, borderHandling);
    }

    @Override
    public WrappedImage getYImage(WrappedImage wrappedImage, BorderHandling borderHandling) {
        return getPruittY(wrappedImage, borderHandling);
    }
}
