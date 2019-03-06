package usr.afast.image.algo;

import usr.afast.image.enums.BorderHandling;
import usr.afast.image.util.Stopwatch;
import usr.afast.image.wrapped.Matrix;

import java.awt.image.BufferedImage;

import static usr.afast.image.algo.AlgoLib.makeGauss;
import static usr.afast.image.util.ImageIO.*;
import static usr.afast.image.util.StringArgsUtil.getBorderHandling;
import static usr.afast.image.util.StringArgsUtil.getDouble;

public class Gauss implements Algorithm {
    @Override
    public void process(String path, String... args) {
        final BorderHandling borderHandling = getBorderHandling(0, args);
        double sigma = getDouble(1, args);
        System.out.println(String.format("Gauss with sigma %.3f", sigma));
        BufferedImage image = read(path);
        Matrix matrix = Matrix.of(image);
        write(getSaveFilePath(path, "original"), Matrix.save(matrix));
        Matrix result = Stopwatch.measure(() -> makeGauss(matrix, sigma, borderHandling));
        BufferedImage bufferedImageResult = Matrix.save(result);
        String newFilePath = getSaveFilePath(path, getClass().getSimpleName());
        write(newFilePath, bufferedImageResult);
    }


}
