package usr.afast.image.algo;

import usr.afast.image.enums.BorderHandling;
import usr.afast.image.space.scale.Pyramid;
import usr.afast.image.util.Stopwatch;
import usr.afast.image.wrapped.Matrix;

import java.awt.image.BufferedImage;
import java.io.File;

import static usr.afast.image.util.ImageIO.read;
import static usr.afast.image.util.StringArgsUtil.*;

public class PyramidAlgo implements Algorithm {
    @Override
    public void process(String path, String... args) {
        if (args.length < 3) {
            System.out.println("invalid args");
            return;
        }
        double initSigma = getDouble(0, args);
        double startSigma = getDouble(1, args);
        int octaveSize = getInt(2, args);
        BufferedImage image = read(path);
        Matrix matrix = Matrix.of(image);

        System.out.println(String.format("Building pyramid with initSigma=%.3f, startSigma=%.3f, octaveSize=%d",
                initSigma,
                startSigma,
                octaveSize));

        Pyramid pyramid = Stopwatch.measure(() -> Pyramid.build(matrix, initSigma, startSigma, octaveSize));

        System.out.println(String.format("Pyramid built with depth=%d", pyramid.getDepth()));

        File file = new File(path);
        pyramid.save(path);
    }


}
