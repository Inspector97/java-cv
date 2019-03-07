package usr.afast.image.algo;

import usr.afast.image.descriptor.*;
import usr.afast.image.util.Stopwatch;
import usr.afast.image.wrapped.Matrix;

import java.awt.image.BufferedImage;
import java.util.List;

import static usr.afast.image.points.PointMarker.markMatching;
import static usr.afast.image.util.ImageIO.*;
import static usr.afast.image.util.StringArgsUtil.getInt;

public class SiftAlgo implements Algorithm {
    @Override
    public void process(String path, String... args) {
        Matrix imageA = Matrix.of(read(path));
        Matrix imageB = Matrix.of(read(args[0]));
        int gridSize = getInt(1, args);
        int cellSize = getInt(2, args);
        int binCount = getInt(3, args);
        GistogramBasedDescriptor descriptor = (gradient, gradientAngle, interestingPoint) ->
                SIFTDescriptor.at(gradient,
                                  gradientAngle,
                                  interestingPoint,
                                  gridSize,
                                  cellSize,
                                  binCount);
        ToDraw matching = Stopwatch.measure(() -> HOGProcessor.process(imageA, imageB, descriptor));

        BufferedImage result = markMatching(imageA, imageB, matching);
        write(getSaveFilePath(path, "SIFT_MATCHING"), result);

        System.out.println("Matched " + matching.getPointsPairs().size());
    }
}
