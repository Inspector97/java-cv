package usr.afast.image.algo;

import usr.afast.image.enums.BorderHandling;
import usr.afast.image.space.scale.Pyramid;
import usr.afast.image.util.Stopwatch;
import usr.afast.image.wrapped.WrappedImage;

import java.awt.image.BufferedImage;
import java.io.File;

import static usr.afast.image.util.ImageIO.read;
import static usr.afast.image.util.StringArgsUtil.*;

public class PyramidAlgo implements Algorithm {
    @Override
    public void process(String path, String... args) {
        if (args.length < 4) {
            System.out.println("invalid args");
            return;
        }
        final BorderHandling borderHandling = getBorderHandling(0, args);
        double initSigma = getDouble(1, args);
        double startSigma = getDouble(2, args);
        int octaveSize = getInt(3, args);
        BufferedImage image = read(path);
        WrappedImage wrappedImage = WrappedImage.of(image);

        System.out.println(String.format("Building pyramid with initSigma=%.3f, startSigma=%.3f, octaveSize=%d",
                initSigma,
                startSigma,
                octaveSize));

        Pyramid pyramid = Stopwatch.measure(() -> Pyramid.build(wrappedImage, initSigma, startSigma, octaveSize));

        System.out.println(String.format("Pyramid built with depth=%d", pyramid.getDepth()));

        File file = new File(path);
        pyramid.save(path);
//        pyramid.getPixel(2, 5, 4);

//        System.out.println(String.format("Gauss with sigma %.3f", sigma));
//        BufferedImage image = read(path);
//        WrappedImage wrappedImage = WrappedImage.of(image);
//        write(getSaveFilePath(path, "original"), WrappedImage.save(wrappedImage));
//        BufferedImage bufferedImageResult = WrappedImage.save(result);
//        String newFilePath = getSaveFilePath(path, getClass().getSimpleName());
//        write(newFilePath, bufferedImageResult);
    }


}
