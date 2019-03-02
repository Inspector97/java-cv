package usr.afast.image.algo;

import usr.afast.image.descriptor.PatchProcessor;
import usr.afast.image.descriptor.PointsPair;
import usr.afast.image.descriptor.SIFTDescriptor;
import usr.afast.image.descriptor.SIFTProcessor;
import usr.afast.image.util.Stopwatch;
import usr.afast.image.wrapped.WrappedImage;

import java.awt.image.BufferedImage;
import java.util.List;

import static usr.afast.image.points.PointMarker.markMatching;
import static usr.afast.image.util.ImageIO.*;
import static usr.afast.image.util.StringArgsUtil.getInt;

public class SiftAlgo implements Algorithm {
    @Override
    public void process(String path, String... args) {
        WrappedImage imageA = WrappedImage.of(read(path));
        WrappedImage imageB = WrappedImage.of(read(args[0]));
        int gridSize = getInt(1, args);
        int cellSize = getInt(2, args);
        int binCount = getInt(3, args);
        List<PointsPair> matching = Stopwatch.measure(() -> SIFTProcessor.processWithSift(imageA,
                                                                                          imageB,
                                                                                          gridSize,
                                                                                          cellSize,
                                                                                          binCount));

        BufferedImage result = markMatching(imageA, imageB, matching);
        write(getSaveFilePath(path, "SIFT_MATCHING"), result);

        System.out.println("Matched " + matching.size());
    }
}
