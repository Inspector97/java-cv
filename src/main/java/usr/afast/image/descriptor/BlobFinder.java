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
import static usr.afast.image.util.Math.*;

public class BlobFinder {
    private static final double INIT_SIGMA = 0.5;
    private static final double START_SIGMA = 1.6;
    private static final int OCTAVE_SIZE = 6;
    private static final double EPS = 1e-4;
    private static final double MIN_HARRIS = 0.001;
    private static final int MAX_CLARIFICATION_STEPS = 5;
    private static final int IMAGE_BORDER = 3;

    private static final double CONTRAST_THRESHOLD = 0.04;
    private static final double EDGE_THRESHOLD = 10;

    public static ToDraw matchBlobs(Matrix aMatrix, Matrix bMatrix, String path) {
        List<AbstractDescriptor> abstractDescriptorsA = findBlobs(aMatrix, path);
        List<AbstractDescriptor> abstractDescriptorsB = findBlobs(bMatrix, path);

        return match(abstractDescriptorsA, abstractDescriptorsB);
    }

    private static List<AbstractDescriptor> findBlobs(Matrix matrix, String path) {
        Pyramid pyramid = Pyramid.build(matrix, INIT_SIGMA, START_SIGMA, OCTAVE_SIZE);
        save(pyramid, path);

        List<AbstractDescriptor> descriptors = new LinkedList<>();
        double sqrt2 = Math.sqrt(2);
        int pow = 1;

        for (int i = 0; i < pyramid.getDepth(); i++, pow *= 2) {
            List<OctaveLayer> layers = pyramid.getDoG(i);
            Matrix startImage = pyramid.getOctaves().get(i).getImages().get(0).getImage();

            Matrix xImage = getSobelX(startImage, BorderHandling.Mirror);
            Matrix yImage = getSobelY(startImage, BorderHandling.Mirror);
//            ImageIO.write(getSaveFilePath("E:\\GitHub\\java-cv\\images\\cats\\temp\\layer.png", i+"" ),
//                          Matrix.save(startImage));

            Matrix gradient = Matrix.getGradient(xImage, yImage);
            Matrix gradientAngle = Matrix.getGradientAngle(xImage, yImage);
            for (int j = 1; j < OCTAVE_SIZE; j++) {
                Matrix prev = layers.get(j - 1).getImage();
                Matrix cur = layers.get(j).getImage();
                Matrix next = layers.get(j + 1).getImage();

                int radius = (int) Math.ceil(layers.get(j).getLocalSigma() * sqrt2);
                Matrix harris = getHarrisMat(startImage, radius);
//                ImageIO.write(getSaveFilePath("E:\\GitHub\\java-cv\\images\\cats\\temp\\harris.png", i + "_" + j),
//                              Matrix.save(harris));

                for (int x = IMAGE_BORDER; x < cur.getWidth() - IMAGE_BORDER; x++) {
                    for (int y = IMAGE_BORDER; y < cur.getHeight() - IMAGE_BORDER; y++) {
                        boolean okMax = true;
                        boolean okMin = true;
                        double pixel = cur.getAt(x, y);
                        double harrisValue = harris.getAt(x, y);
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dy = -1; dy <= 1; dy++) {
                                okMax &= pixel > prev.getAt(x + dx, y + dy, BorderHandling.Mirror);
                                okMin &= pixel < prev.getAt(x + dx, y + dy, BorderHandling.Mirror);
                                okMax &= pixel > next.getAt(x + dx, y + dy, BorderHandling.Mirror);
                                okMin &= pixel < next.getAt(x + dx, y + dy, BorderHandling.Mirror);

                                if (dx != 0 || dy != 0) {
                                    okMax &= pixel > cur.getAt(x + dx, y + dy, BorderHandling.Mirror);
                                    okMin &= pixel < cur.getAt(x + dx, y + dy, BorderHandling.Mirror);
                                }
                            }
                        }

                        if ((okMax || okMin) && harrisValue > MIN_HARRIS) {
                            InterestingPoint point = adjust(layers, j, i, x, y);

                            if (point != null) {
                                List<SIFTDescriptor> locals =
                                        SIFTDescriptor.at(gradient,
                                                          gradientAngle,
                                                          point,
                                                          8,
                                                          16);
                                descriptors.addAll(locals);
                            }
                        }
                    }
                }
            }
        }

        return descriptors;
    }

    private static InterestingPoint adjust(List<OctaveLayer> dogs, int layer, int octave, int x, int y) {
        double[] dD = null, X = null;
        double dxx = 0, dyy = 0, dxy = 0;

        int step;
        for (step = 0; step < MAX_CLARIFICATION_STEPS; step++) {
            Matrix img = dogs.get(layer).getImage();
            Matrix prev = dogs.get(layer - 1).getImage(), next = dogs.get(layer + 1).getImage();

            dD = new double[]{
                    (img.getAt(x + 1, y) - img.getAt(x - 1, y)) / 2,
                    (img.getAt(x, y + 1) - img.getAt(x, y - 1)) / 2,
                    (next.getAt(x, y) - prev.getAt(x, y)) / 2
            };

            double v2 = img.getAt(x, y) * 2;

            dxx = img.getAt(x + 1, y) + img.getAt(x - 1, y) - v2;
            dyy = img.getAt(x, y + 1) + img.getAt(x, y - 1) - v2;
            double dss = next.getAt(x, y) + prev.getAt(x, y) - v2;

            dxy = (img.getAt(x + 1, y + 1) - img.getAt(x - 1, y + 1) - img.getAt(x + 1, y - 1) + img.getAt(x - 1, y - 1)) / 4;
            double dxs = (next.getAt(x + 1, y) - next.getAt(x - 1, y) - prev.getAt(x + 1, y) + prev.getAt(x - 1, y)) / 4;
            double dys = (next.getAt(x, y + 1) - next.getAt(x, y - 1) - prev.getAt(x, y + 1) + prev.getAt(x, y - 1)) / 4;

            double[][] d2D = {
                    {dxx, dxy, dxs},
                    {dxy, dyy, dys},
                    {dxs, dys, dss},
            };

            try {
                X = solveLU(d2D, dD);
                for (int i = 0; i < X.length; i++) X[i] *= -1;
            } catch (Exception e) {
                return null; // singular matrix
            }

            if (Math.abs(X[0]) < 0.5 && Math.abs(X[1]) < 0.5 && Math.abs(X[2]) < 0.5) break;

            x += (int) Math.round(X[0]);
            y += (int) Math.round(X[1]);
            layer += (int) Math.round(X[2]);

            if (layer < 1 || layer > OCTAVE_SIZE ||
                x < IMAGE_BORDER || x >= img.getWidth() - IMAGE_BORDER ||
                y < IMAGE_BORDER || y >= img.getHeight() - IMAGE_BORDER)
                return null;
        }

        if (step >= MAX_CLARIFICATION_STEPS) return null;

        double contr;
        {
            Matrix img = dogs.get(layer).getImage();

            contr = img.getAt(x, y) + dot(dD, X) * 0.5;
            if (Math.abs(contr) * OCTAVE_SIZE < CONTRAST_THRESHOLD) return null;

            double tr = dxx + dyy;
            double det = dxx * dyy - dxy * dxy;

            if (det <= 0 || sqr(tr) * EDGE_THRESHOLD >= sqr(EDGE_THRESHOLD + 1) * det) return null;
        }

        double newSigma = START_SIGMA * Math.pow(2, octave + (layer + X[2]) / OCTAVE_SIZE + 1);

        InterestingPoint point = InterestingPoint.at(getCoordinate(x + X[0], octave),
                                                     getCoordinate(y + X[1], octave),
                                                     contr * 100);
        return point.setOctave(octave).setLayer(layer).setScale(newSigma);
    }

    private static int getCoordinate(double value, int octave) {
        if (octave == -1) return (int) Math.round(value / 2);
        return (int) Math.round(value * (1 << octave));
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
