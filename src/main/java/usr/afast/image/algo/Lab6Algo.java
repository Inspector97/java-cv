package usr.afast.image.algo;

import org.apache.commons.io.FileUtils;
import usr.afast.image.space.scale.Octave;
import usr.afast.image.space.scale.OctaveLayer;
import usr.afast.image.space.scale.Pyramid;
import usr.afast.image.util.Stopwatch;
import usr.afast.image.wrapped.Matrix;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static usr.afast.image.util.ImageIO.*;
import static usr.afast.image.util.StringArgsUtil.getDouble;
import static usr.afast.image.util.StringArgsUtil.getInt;

@SuppressWarnings("Duplicates")
public class Lab6Algo implements Algorithm {
    @Override
    public void process(String path, String... args) {
        if (args.length < 3) {
            System.out.println("invalid args");
            return;
        }
        double initSigma = getDouble(0, args);
        double startSigma = getDouble(1, args);
        int octaveSize = getInt(2, args);
        BufferedImage image = read(path);
        Matrix matrix = Matrix.of(image);

        System.out.println(String.format("Building pyramid with initSigma=%.3f, startSigma=%.3f, octaveSize=%d",
                                         initSigma,
                                         startSigma,
                                         octaveSize));

        Pyramid pyramid = Stopwatch.measure(() -> Pyramid.build(matrix, initSigma, startSigma, octaveSize));

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

        System.out.println(String.format("Pyramid built with depth=%d", pyramid.getDepth()));

//        pyramid.save(path);
    }


}
