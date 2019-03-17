package usr.afast.image.descriptor;

import org.apache.commons.io.FileUtils;
import usr.afast.image.enums.BorderHandling;
import usr.afast.image.points.InterestingPoint;
import usr.afast.image.space.scale.OctaveLayer;
import usr.afast.image.space.scale.Pyramid;
import usr.afast.image.wrapped.Matrix;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static usr.afast.image.algo.AlgoLib.getSobelX;
import static usr.afast.image.algo.AlgoLib.getSobelY;
import static usr.afast.image.descriptor.DescriptorUtil.match;
import static usr.afast.image.points.Harris.getHarrisMat;
import static usr.afast.image.util.ImageIO.getSaveFilePath;
import static usr.afast.image.util.ImageIO.write;

public class BlobFinder {
    private static final double INIT_SIGMA = 0.5;
    private static final double START_SIGMA = 1;
    private static final int OCTAVE_SIZE = 4;
    private static final double EPS = 1e-4;
    private static final double MIN_HARRIS = 0.03;

    public static ToDraw matchBlobs(Matrix aMatrix, Matrix bMatrix, String path) {
        List<AbstractDescriptor> abstractDescriptorsA = findBlobs(aMatrix, path);
        List<AbstractDescriptor> abstractDescriptorsB = findBlobs(bMatrix, path);

        return match(abstractDescriptorsA, abstractDescriptorsB);
    }

    public static List<AbstractDescriptor> findBlobs(Matrix matrix, String path) {
        Pyramid pyramid = Pyramid.build(matrix, INIT_SIGMA, START_SIGMA, OCTAVE_SIZE);
//        save(pyramid, path);

        List<AbstractDescriptor> descriptors = new LinkedList<>();
        double sqrt2 = Math.sqrt(2);
        int pow = 1;

        for (int i = 0; i < pyramid.getDepth(); i++, pow *= 2) {
            List<OctaveLayer> layers = pyramid.getDoG(i);
            Matrix startImage = pyramid.getOctaves().get(i).getImages().get(0).getImage();
            for (int j = 1; j <= OCTAVE_SIZE; j++) {
                Matrix prev = layers.get(j - 1).getImage();
                Matrix cur = layers.get(j).getImage();
                Matrix next = layers.get(j + 1).getImage();

                int radius = (int) (layers.get(j).getLocalSigma() * sqrt2);
                Matrix curImage = pyramid.getOctaves().get(i).getImages().get(j - 1).getImage();

                Matrix xImage = getSobelX(curImage, BorderHandling.Mirror);
                Matrix yImage = getSobelY(curImage, BorderHandling.Mirror);

                Matrix gradient = Matrix.getGradient(xImage, yImage);
                Matrix gradientAngle = Matrix.getGradientAngle(xImage, yImage);
                Matrix harris = Matrix.normalize(getHarrisMat(startImage, radius));

                for (int x = 0; x < cur.getWidth(); x++) {
                    for (int y = 0; y < cur.getHeight(); y++) {
                        boolean okMax = true;
                        boolean okMin = true;
                        double pixel = cur.getAt(x, y);
                        if (Math.abs(pixel) < 0.03) continue;
                        double harrisValue = harris.getAt(x, y);
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

                        if ((okMax || okMin) && harrisValue > MIN_HARRIS) {
//                            System.out.println(harrisValue + " " + pixel);
                            InterestingPoint at = InterestingPoint.at(x * pow + pow / 2,
                                                                      y * pow + pow / 2,
                                                                      pixel * 100,
                                                                      layers.get(j).getGlobalSigma() * sqrt2,
                                                                      0);
                            List<SIFTDescriptor> locals =
                                    SIFTDescriptor.at(gradient,
                                                      gradientAngle,
                                                      at,
                                                      8,
                                                      (int) Math.ceil(layers.get(j).getLocalSigma() * sqrt2 / 4),
                                                      36,
                                                      pow);
                            descriptors.addAll(locals);
                        }
                    }
                }
            }
        }

        return descriptors;
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
