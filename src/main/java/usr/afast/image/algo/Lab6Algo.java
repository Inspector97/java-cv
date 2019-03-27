package usr.afast.image.algo;

import usr.afast.image.descriptor.ToDraw;
import usr.afast.image.util.Stopwatch;
import usr.afast.image.wrapped.Matrix;

import java.awt.image.BufferedImage;

import static usr.afast.image.descriptor.BlobFinder.matchBlobs;
import static usr.afast.image.points.PointMarker.markMatching;
import static usr.afast.image.util.ImageIO.*;

@SuppressWarnings("Duplicates")
public class Lab6Algo implements Algorithm {
    @Override
    public void process(String path, String... args) {
        if (args.length < 1) {
            System.out.println("invalid args");
            return;
        }
        Matrix imageA = Matrix.of(read(path));
        Matrix imageB = Matrix.of(read(args[0]));

        ToDraw toDraw = Stopwatch.measure(() -> matchBlobs(imageA, imageB, path));

        BufferedImage withBlobs = markMatching(imageA, imageB, toDraw);

        write(getSaveFilePath(path, "BLOBS"), withBlobs);

        System.out.println("matched: " + toDraw.getPointsPairs().size());

//        pyramid.save(path);
    }


}
