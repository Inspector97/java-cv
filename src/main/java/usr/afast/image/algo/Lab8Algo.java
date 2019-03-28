package usr.afast.image.algo;

import usr.afast.image.descriptor.Matching;
import usr.afast.image.descriptor.PanoramaMaker;
import usr.afast.image.util.Stopwatch;
import usr.afast.image.wrapped.Matrix;

import java.awt.image.BufferedImage;

import static usr.afast.image.descriptor.BlobFinder.matchBlobs;
import static usr.afast.image.points.PointMarker.markMatching;
import static usr.afast.image.util.ImageIO.*;

@SuppressWarnings("Duplicates")
public class Lab8Algo implements Algorithm {
    @Override
    public void process(String path, String... args) {
        if (args.length < 1) {
            System.out.println("invalid args");
            return;
        }
        Matrix imageA = Matrix.of(read(path));
        Matrix imageB = Matrix.of(read(args[0]));

        Matching matching = Stopwatch.measure(() -> matchBlobs(imageA, imageB, path));

        Matrix panorama = Stopwatch.measure(() -> PanoramaMaker.makePanorama(imageA, imageB, matching));

        if (panorama != null) {
            write(getSaveFilePath(path, "PANORAMA"), panorama);
        }
//
//        BufferedImage withBlobs = markMatching(imageA, imageB, matching);
//
//        write(getSaveFilePath(path, "BLOBS"), withBlobs);
//
//        System.out.println("matched: " + matching.getPointsPairs().size());

//        pyramid.save(path);
    }


}
