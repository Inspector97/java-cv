package usr.afast.image.space.scale;

import lombok.Getter;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.wrapped.Matrix;

import java.util.ArrayList;
import java.util.List;

import static usr.afast.image.algo.AlgoLib.makeGauss;

@Getter
public class Octave {
    private int number;
    private double startSigma;
    private List<OctaveLayer> images;
    private Matrix nextImage;

    public static Octave build(Matrix image, int number, double startSigma, int size, int overlap) {
        Octave octave = new Octave();
        octave.images = new ArrayList<>(size + 1 + overlap);
        octave.startSigma = startSigma;
        octave.number = number;

        double step = Math.pow(2, 1.0 / size);
        double localSigma = startSigma;
        double globalSigma = startSigma * Math.pow(2, number);

        Matrix current = new Matrix(image);
        octave.images.add(new OctaveLayer(0, localSigma, globalSigma, current));
        for (int i = 0; i < size + overlap; i++) {
            double nextSigma = localSigma * step;
            globalSigma = globalSigma * step;
            double delta = Math.sqrt(nextSigma * nextSigma - localSigma * localSigma);

            current = makeGauss(current, delta, BorderHandling.Mirror);
            octave.images.add(new OctaveLayer(i + 1, nextSigma, globalSigma, current));

            localSigma = nextSigma;
        }

        octave.nextImage = octave.images.get(size).getImage();

        return octave;
    }
}
