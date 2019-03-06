package usr.afast.image.algo;

import usr.afast.image.util.Stopwatch;
import usr.afast.image.wrapped.Matrix;

import java.awt.image.BufferedImage;

import static usr.afast.image.edge.Canny.canny;
import static usr.afast.image.util.ImageIO.*;

public class CannyAlgo implements Algorithm {
    @Override
    public void process(String path, String... args) {
        BufferedImage image = read(path);
        Matrix matrix = Matrix.of(image);
        Matrix result = Stopwatch.measure(() -> canny(path, matrix));

        String newFilePath = getSaveFilePath(path, getClass().getSimpleName());
        write(newFilePath, result);
    }
}
