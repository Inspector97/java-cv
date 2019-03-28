package usr.afast.image.algo;

import usr.afast.image.descriptor.Matching;
import usr.afast.image.descriptor.PatchProcessor;
import usr.afast.image.util.Stopwatch;
import usr.afast.image.wrapped.Matrix;

import java.awt.image.BufferedImage;

import static usr.afast.image.points.PointMarker.markMatching;
import static usr.afast.image.util.ImageIO.*;
import static usr.afast.image.util.StringArgsUtil.getInt;

public class PatchAlgo implements Algorithm {
    @Override
    public void process(String path, String... args) {
        Matrix imageA = Matrix.of(read(path));
        Matrix imageB = Matrix.of(read(args[0]));
        int gridHalfSize = getInt(1, args);
        int cellHalfSize = getInt(2, args);
        Matching matching = Stopwatch.measure(() -> PatchProcessor.processWithPatches(imageA,
                                                                                              imageB,
                                                                                              gridHalfSize,
                                                                                              cellHalfSize));

        BufferedImage result = markMatching(imageA, imageB, matching);
        write(getSaveFilePath(path, "MATCHING"), result);

        System.out.println("Matched " + matching.getPointsPairs().size());
    }
}
