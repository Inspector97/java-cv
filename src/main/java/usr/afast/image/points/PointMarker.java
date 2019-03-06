package usr.afast.image.points;

import org.jetbrains.annotations.NotNull;
import usr.afast.image.descriptor.PointsPair;
import usr.afast.image.wrapped.Matrix;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class PointMarker {
    private static final double SPECTRUM_OFFSET = 180D / 255;

    public static BufferedImage markPoints(@NotNull List<InterestingPoint> interestingPoints, Matrix image) {
        BufferedImage bufferedImage = Matrix.save(image);

        Graphics2D graphics = bufferedImage.createGraphics();
        int radius = 2;
        graphics.setStroke(new BasicStroke(2));
        for (InterestingPoint interestingPoint : interestingPoints) {
            int x = interestingPoint.getX() - radius;
            int y = interestingPoint.getY() - radius;
            graphics.setColor(getSpectrum(interestingPoint.getProbability()));
            graphics.drawOval(x, y, 2 * radius, 2 * radius);
        }
        graphics.dispose();

        return bufferedImage;
    }

    public static BufferedImage markMatching(Matrix imageA, Matrix imageB, @NotNull List<PointsPair> matching) {
        BufferedImage bufferedA = Matrix.save(imageA);
        BufferedImage bufferedB = Matrix.save(imageB);
        BufferedImage result = new BufferedImage(bufferedA.getWidth() + bufferedB.getWidth(),
                                                 Math.max(bufferedA.getHeight(), bufferedB.getHeight()),
                                                 BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = result.createGraphics();

        graphics.drawImage(bufferedA, 0, 0, null);
        graphics.drawImage(bufferedB, bufferedA.getWidth(), 0, null);

        int radius = 2;
        graphics.setStroke(new BasicStroke(2));
        for (PointsPair pointsPair : matching) {
            int xA = pointsPair.getPointA().getX() - radius;
            int yA = pointsPair.getPointA().getY() - radius;
            graphics.setColor(getSpectrum(pointsPair.getPointA().getProbability()));
            graphics.drawOval(xA, yA, 2 * radius, 2 * radius);

            int xB = pointsPair.getPointB().getX() - radius + imageA.getWidth();
            int yB = pointsPair.getPointB().getY() - radius;
            graphics.drawOval(xB, yB, 2 * radius, 2 * radius);

            graphics.drawLine(xA + radius, yA + radius, xB + radius, yB + radius);
        }

        graphics.dispose();

        return result;
    }

    private static Color getSpectrum(double value) {
        value = 1 - value * SPECTRUM_OFFSET + SPECTRUM_OFFSET;
        return Color.getHSBColor((float) value, 1F, 1F);
    }
}
