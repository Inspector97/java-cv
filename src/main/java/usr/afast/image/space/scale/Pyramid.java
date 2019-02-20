package usr.afast.image.space.scale;

import lombok.Getter;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.wrapped.WrappedImage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static usr.afast.image.algo.AlgoLib.makeGauss;

@Getter
public class Pyramid {
    private static int MIN_IMAGE_SIZE = 20;
    private int depth;
    private double initSigma;
    private double startSigma;
    private WrappedImage original;
    private List<Octave> octaves;

    public static Pyramid build(WrappedImage image, double initSigma, double startSigma, int octaveSize) {
        Pyramid pyramid = new Pyramid();
        pyramid.original = image;
        pyramid.initSigma = initSigma;
        pyramid.startSigma = startSigma;
        pyramid.octaves = new LinkedList<>();
        pyramid.depth = 0;

        WrappedImage current = makeFirstImage(image, initSigma, startSigma);
        while (current.getHeight() > MIN_IMAGE_SIZE && current.getWidth() > MIN_IMAGE_SIZE) {
            pyramid.depth++;
            Octave newOctave = Octave.build(current, pyramid.depth, startSigma, octaveSize);
            pyramid.octaves.add(newOctave);
            current = newOctave.getNextImage().downSample();
        }

        return pyramid;
    }

    private static WrappedImage makeFirstImage(WrappedImage original, double initSigma, double startSigma) {
        double delta = Math.sqrt(startSigma * startSigma - initSigma * initSigma);
        return makeGauss(original, delta, BorderHandling.Mirror);
    }
}
