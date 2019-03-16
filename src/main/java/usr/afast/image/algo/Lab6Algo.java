package usr.afast.image.algo;

import org.apache.commons.io.FileUtils;
import usr.afast.image.descriptor.Circle;
import usr.afast.image.space.scale.Octave;
import usr.afast.image.space.scale.OctaveLayer;
import usr.afast.image.space.scale.Pyramid;
import usr.afast.image.util.Stopwatch;
import usr.afast.image.wrapped.Matrix;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static usr.afast.image.descriptor.BlobFinder.findBlobs;
import static usr.afast.image.points.PointMarker.drawCircles;
import static usr.afast.image.util.ImageIO.*;
import static usr.afast.image.util.StringArgsUtil.getDouble;
import static usr.afast.image.util.StringArgsUtil.getInt;

@SuppressWarnings("Duplicates")
public class Lab6Algo implements Algorithm {
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

        List<Circle> circles = Stopwatch.measure(() -> findBlobs(matrix, path));

        BufferedImage withBlobs = drawCircles(circles, matrix);

        write(getSaveFilePath(path, "BLOBS"), withBlobs);

//        pyramid.save(path);
    }


}
