package usr.afast.image.algo;

import usr.afast.image.descriptor.PatchProcessor;
import usr.afast.image.descriptor.PointsPair;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.util.Stopwatch;
import usr.afast.image.wrapped.WrappedImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import static usr.afast.image.points.PointMarker.markMatching;
import static usr.afast.image.util.ImageIO.*;
import static usr.afast.image.util.StringArgsUtil.getBorderHandling;
import static usr.afast.image.util.StringArgsUtil.getInt;

public class PatchAlgo implements Algorithm {
    @Override
    public void process(String path, String... args) {
        WrappedImage imageA = WrappedImage.of(read(path));
        WrappedImage imageB = WrappedImage.of(read(args[0]));
        int gridHalfSize = getInt(1, args);
        int cellHalfSize = getInt(2, args);
        List<PointsPair> matching = Stopwatch.measure(() -> PatchProcessor.processWithPatches(imageA,
                                                                                              imageB,
                                                                                              gridHalfSize,
                                                                                              cellHalfSize));

        BufferedImage result = markMatching(imageA, imageB, matching);
        write(getSaveFilePath(path, "MATCHING"), result);

        System.out.println("Matched " + matching.size());
    }
}
