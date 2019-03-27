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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static usr.afast.image.algo.AlgoLib.makeGauss;
import static usr.afast.image.space.scale.OctaveLayer.downSample;
import static usr.afast.image.util.ImageIO.getSaveFilePath;
import static usr.afast.image.util.ImageIO.write;

@Getter
public class Pyramid {
    private static final int MIN_IMAGE_SIZE = 20;
    private static final int OVERLAP = 2;
    private int depth;
    private double initSigma;
    private double startSigma;
    private int octaveSize;
    private Matrix original;
    private List<Octave> octaves;
    private List<List<OctaveLayer>> DoGs;
    private Octave minusFirstOctave;

    public static Pyramid build(Matrix image, double initSigma, double startSigma, int octaveSize) {
        Pyramid pyramid = new Pyramid();
        pyramid.original = image;
        pyramid.initSigma = initSigma;
        pyramid.startSigma = startSigma;
        pyramid.octaveSize = octaveSize;
        pyramid.octaves = new LinkedList<>();
        pyramid.depth = 0;

        pyramid.minusFirstOctave = Octave.build(makeFirstImage(upscale(image), initSigma, startSigma),
                                                -1,
                                                startSigma,
                                                octaveSize,
                                                OVERLAP);

        Matrix current = pyramid.minusFirstOctave.getNextImage().downSample();

        while (current.getHeight() > MIN_IMAGE_SIZE && current.getWidth() > MIN_IMAGE_SIZE) {
            Octave newOctave = Octave.build(current, pyramid.depth, startSigma, octaveSize, OVERLAP);
            pyramid.octaves.add(newOctave);
            current = newOctave.getNextImage().downSample();
            pyramid.depth++;
        }

        pyramid.initDoGs();

        return pyramid;
    }

    private void initDoGs() {
        DoGs = new ArrayList<>(depth);
        for (int index = 0; index < depth; index++) {
            Octave previous = getByIndex(index - 1);
            Octave current = getByIndex(index);
            List<OctaveLayer> gaussians = new ArrayList<>(octaveSize + 4);
            gaussians.add(downSample(previous.getImages().get(octaveSize - 2)));
            gaussians.add(downSample(previous.getImages().get(octaveSize - 1)));
            for (int i = 0; i < octaveSize + 2; i++)
                gaussians.add(current.getImages().get(i));

            List<OctaveLayer> DoGs = new ArrayList<>(octaveSize + 2);
            for (int i = 1; i < octaveSize + 4; i++) {
                OctaveLayer cur = gaussians.get(i);
                OctaveLayer prev = gaussians.get(i - 1);
                Matrix delta = Matrix.subtract(cur.getImage(), prev.getImage());
                DoGs.add(new OctaveLayer(i - 1, cur.getLocalSigma(), cur.getGlobalSigma(), delta));
            }
            this.DoGs.add(DoGs);
        }
    }

    private static Matrix makeFirstImage(Matrix original, double initSigma, double startSigma) {
        double delta = Math.sqrt(startSigma * startSigma - initSigma * initSigma);
        if (Math.abs(delta) < 1e-3)
            return new Matrix(original);
        return makeGauss(original, delta, BorderHandling.Mirror);
    }

    public Octave getByIndex(int index) {
        if (index >= 0)
            return octaves.get(index);
        if (index < -1)
            throw new IllegalArgumentException();
        return minusFirstOctave;
    }

    public List<OctaveLayer> getDoG(int index) {
        if (index < 0 || index >= depth)
            throw new IllegalArgumentException();
        return DoGs.get(index);
    }

    public double getPixel(int x, int y, double sigma) {
        double step = Math.pow(2, 1.0 / octaveSize);
        double log = Math.log(sigma) / Math.log(step);
        int index = (int) Math.round(log) + 1;
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
