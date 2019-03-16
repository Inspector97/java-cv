package usr.afast.image.descriptor;

import org.apache.commons.io.FileUtils;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.space.scale.OctaveLayer;
import usr.afast.image.space.scale.Pyramid;
import usr.afast.image.wrapped.Matrix;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static usr.afast.image.util.ImageIO.getSaveFilePath;
import static usr.afast.image.util.ImageIO.write;

public class BlobFinder {
    private static final double INIT_SIGMA = 1;
    private static final double START_SIGMA = 2;
    private static final int OCTAVE_SIZE = 6;
    private static final double EPS = 1e-4;

    public static List<Circle> findBlobs(Matrix matrix, String path) {
        Pyramid pyramid = Pyramid.build(matrix, INIT_SIGMA, START_SIGMA, OCTAVE_SIZE);
        save(pyramid, path);

        List<Circle> circles = new LinkedList<>();

        double sqrt2 = Math.sqrt(2);
        int pow = 1;

        for (int i = 0; i < pyramid.getDepth(); i++, pow *= 2) {
            List<OctaveLayer> layers = pyramid.getDoG(i);
            for (int j = 1; j <= OCTAVE_SIZE; j++) {
                Matrix prev = layers.get(j - 1).getImage();
                Matrix cur = layers.get(j).getImage();
                Matrix next = layers.get(j + 1).getImage();
                for (int x = 0; x < cur.getWidth(); x++) {
                    for (int y = 0; y < cur.getHeight(); y++) {
                        boolean okMax = true;
                        boolean okMin = true;
                        double pixel = cur.getAt(x, y);
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dy = -1; dy <= 1; dy++) {
                                okMax &= pixel > prev.getAt(x + dx, y + dy, BorderHandling.Mirror) + EPS;
                                okMin &= pixel < prev.getAt(x + dx, y + dy, BorderHandling.Mirror) - EPS;
                                okMax &= pixel > next.getAt(x + dx, y + dy, BorderHandling.Mirror) + EPS;
                                okMin &= pixel < next.getAt(x + dx, y + dy, BorderHandling.Mirror) - EPS;

                                if (dx != 0 || dy != 0) {
                                    okMax &= pixel > cur.getAt(x + dx, y + dy, BorderHandling.Mirror) + EPS;
                                    okMin &= pixel < cur.getAt(x + dx, y + dy, BorderHandling.Mirror) - EPS;
                                }

                            }
                        }

                        if (okMax || okMin) {
                            circles.add(new Circle(x * pow,
                                                   y * pow,
                                                   layers.get(j).getGlobalSigma() * sqrt2,
                                                   pixel * layers.get(j).getGlobalSigma()));
                        }
                    }
                }
            }
        }

        return circles;
    }

    private static void save(Pyramid pyramid, String path) {
        File file = new File(path);
        String folderPath = Paths.get(file.getParent(), "DoGs").toString();
        File newFolder = new File(folderPath);
        System.out.println(newFolder);
        try {
            FileUtils.deleteDirectory(newFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Files.createDirectory(Paths.get(folderPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        path = Paths.get(folderPath, file.getName()).toString();
        for (int i = 0; i < pyramid.getDepth(); i++) {
            List<OctaveLayer> layers = pyramid.getDoG(i);
            for (OctaveLayer octaveLayer : layers) {
                String suffix = String.format("[oct=%d, idx=%d, globalSigma=%.2f]",
                                              i,
                                              octaveLayer.getIndex(),
                                              octaveLayer.getGlobalSigma());
                System.out.println(suffix);
                write(getSaveFilePath(path, suffix), Matrix.save(octaveLayer.getImage()));
            }
        }
    }
}
