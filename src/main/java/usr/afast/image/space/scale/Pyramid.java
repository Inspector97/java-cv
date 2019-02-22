package usr.afast.image.space.scale;

import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.wrapped.WrappedImage;

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
    private WrappedImage original;
    private List<Octave> octaves;
    private Octave minusFirstOctave;

    public static Pyramid build(WrappedImage image, double initSigma, double startSigma, int octaveSize) {
        Pyramid pyramid = new Pyramid();
        pyramid.original = image;
        pyramid.initSigma = initSigma;
        pyramid.startSigma = startSigma;
        pyramid.octaves = new LinkedList<>();
        pyramid.depth = 0;

        WrappedImage current = makeFirstImage(image, initSigma, startSigma);

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

    private static WrappedImage makeFirstImage(WrappedImage original, double initSigma, double startSigma) {
        double delta = Math.sqrt(startSigma * startSigma - initSigma * initSigma);
        return makeGauss(original, delta, BorderHandling.Mirror);
    }

    private static WrappedImage upscale(@NotNull WrappedImage image) {
        WrappedImage upscaled = new WrappedImage(image.getWidth() * 2, image.getHeight() * 2);
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                upscaled.setPixel(i * 2, j * 2, image.getPixel(i, j) * 4);
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
        BufferedImage current = WrappedImage.save(original);
        String currentPath = getSaveFilePath(path, "[ original ]");
        write(currentPath, current);

        for (Octave octave : octaves) {
            for (OctaveLayer octaveLayer : octave.getImages()) {
                String suffix = String.format("[oct=%d, idx=%d, globalSigma=%.2f]",
                                              octave.getNumber(),
                                              octaveLayer.getIndex(),
                                              octaveLayer.getGlobalSigma());
                System.out.println(suffix);
                current = WrappedImage.save(octaveLayer.getImage());
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
            current = WrappedImage.save(octaveLayer.getImage());
            currentPath = getSaveFilePath(path, suffix);
            write(currentPath, current);
        }
    }
}
