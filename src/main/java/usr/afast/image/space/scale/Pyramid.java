package usr.afast.image.space.scale;

import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.wrapped.Matrix;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static usr.afast.image.algo.AlgoLib.makeGauss;
import static usr.afast.image.util.ImageIO.getSaveFilePath;
import static usr.afast.image.util.ImageIO.write;

@Getter
public class Pyramid {
    private static int MIN_IMAGE_SIZE = 20;
    private int depth;
    private double initSigma;
    private double startSigma;
    private int octaveSize;
    private Matrix original;
    private List<Octave> octaves;
    private Octave minusFirstOctave;

    public static Pyramid build(Matrix image, double initSigma, double startSigma, int octaveSize) {
        Pyramid pyramid = new Pyramid();
        pyramid.original = image;
        pyramid.initSigma = initSigma;
        pyramid.startSigma = startSigma;
        pyramid.octaveSize = octaveSize;
        pyramid.octaves = new LinkedList<>();
        pyramid.depth = 0;

        Matrix current = makeFirstImage(image, initSigma, startSigma);

        pyramid.minusFirstOctave = Octave.build(makeFirstImage(upscale(image), initSigma, startSigma),
                                                -1,
                                                startSigma,
                                                octaveSize);

        while (current.getHeight() > MIN_IMAGE_SIZE && current.getWidth() > MIN_IMAGE_SIZE) {
            Octave newOctave = Octave.build(current, pyramid.depth, startSigma, octaveSize);
            pyramid.octaves.add(newOctave);
            current = newOctave.getNextImage().downSample();
            pyramid.depth++;
        }

        return pyramid;
    }

    private static Matrix makeFirstImage(Matrix original, double initSigma, double startSigma) {
        double delta = Math.sqrt(startSigma * startSigma - initSigma * initSigma);
        if (Math.abs(delta) < 1e-3)
            return new Matrix(original);
        return makeGauss(original, delta, BorderHandling.Mirror);
    }

    public double getPixel(int x, int y, double sigma) {
        double step = Math.pow(2, 1.0 / octaveSize);
        double log = Math.log(sigma) / Math.log(step);
        int index = (int)Math.round(log) + 1;
        int octaveIndex = index / octaveSize;
        if (octaveIndex >= octaves.size()) {
            throw new IllegalArgumentException("Sigma too big");
        }
        Matrix image = octaves.get(octaveIndex).getImages().get(index % octaveSize).getImage();
        System.out.println(image.getHeight());
        int scale = (int) Math.ceil(Math.pow(2, octaveIndex));
        return image.getAt((x / scale), (y / scale));
    }

    private static Matrix upscale(@NotNull Matrix image) {
        Matrix upscaled = new Matrix(image.getWidth() * 2, image.getHeight() * 2);
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                upscaled.setAt(i * 2, j * 2, image.getAt(i, j) * 4);
            }
        }
        return makeGauss(upscaled, 2, 1, BorderHandling.Mirror);
    }

    public void save(String path) {
        File file = new File(path);
        String folderPath = Paths.get(file.getParent(), "pyramid").toString();
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
        BufferedImage current = Matrix.save(original);
        String currentPath = getSaveFilePath(path, "[ original ]");
        write(currentPath, current);

        for (Octave octave : octaves) {
            for (OctaveLayer octaveLayer : octave.getImages()) {
                String suffix = String.format("[oct=%d, idx=%d, globalSigma=%.2f]",
                                              octave.getNumber(),
                                              octaveLayer.getIndex(),
                                              octaveLayer.getGlobalSigma());
                System.out.println(suffix);
                current = Matrix.save(octaveLayer.getImage());
                currentPath = getSaveFilePath(path, suffix);
                write(currentPath, current);
            }
        }

        for (OctaveLayer octaveLayer : minusFirstOctave.getImages()) {
            String suffix = String.format("[oct=%d, idx=%d, globalSigma=%.2f]",
                                          -1,
                                          octaveLayer.getIndex(),
                                          octaveLayer.getGlobalSigma());
            System.out.println(suffix);
            current = Matrix.save(octaveLayer.getImage());
            currentPath = getSaveFilePath(path, suffix);
            write(currentPath, current);
        }
    }
}
