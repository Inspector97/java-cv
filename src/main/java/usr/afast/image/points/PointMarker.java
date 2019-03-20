package usr.afast.image.points;

import org.jetbrains.annotations.NotNull;
import usr.afast.image.descriptor.AbstractDescriptor;
import usr.afast.image.descriptor.PointsPair;
import usr.afast.image.descriptor.ToDraw;
import usr.afast.image.wrapped.Matrix;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.Collectors;

public class PointMarker {
    private static final double SPECTRUM_OFFSET = 180D / 255;
    private static final double SQRT_2 = Math.sqrt(2);

    public static BufferedImage markPoints(@NotNull List<InterestingPoint> interestingPoints, Matrix image) {
        return markPoints(interestingPoints, Matrix.save(image));
    }

    public static BufferedImage markPoints(@NotNull List<InterestingPoint> interestingPoints, @NotNull BufferedImage image) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = result.createGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.setStroke(new BasicStroke(1));
        for (InterestingPoint interestingPoint : interestingPoints) {
            int radius = (int) (interestingPoint.getOriginalScale() * SQRT_2);
            int x = (int) (interestingPoint.getOriginalX() - radius);
            int y = (int) (interestingPoint.getOriginalY() - radius);
            graphics.setColor(getSpectrum(interestingPoint.getProbability()));
            graphics.drawOval(x, y, 2 * radius, 2 * radius);

            double angle = interestingPoint.getAngle();
            if (angle < -10) continue;
            int dx = (int) (Math.cos(angle) * 10);
            int dy = (int) (Math.sin(angle) * 10);

            graphics.drawLine((int) interestingPoint.getOriginalX(), (int) interestingPoint.getOriginalY(),
                              (int) interestingPoint.getOriginalX() + dx, (int) interestingPoint.getOriginalY() + dy);

        }
        graphics.dispose();

        return result;
    }

    public static BufferedImage markMatching(Matrix imageA, Matrix imageB, @NotNull ToDraw matching) {
        List<InterestingPoint> pointA = matching.getDescriptorsA()
                                                .stream()
                                                .map(AbstractDescriptor::getPoint)
                                                .collect(Collectors.toList());

        List<InterestingPoint> pointB = matching.getDescriptorsB()
                                                .stream()
                                                .map(AbstractDescriptor::getPoint)
                                                .collect(Collectors.toList());

        BufferedImage bufferedA = markPoints(pointA, Matrix.save(imageA));
        BufferedImage bufferedB = markPoints(pointB, Matrix.save(imageB));

        BufferedImage result = new BufferedImage(bufferedA.getWidth() + bufferedB.getWidth(),
                                                 Math.max(bufferedA.getHeight(), bufferedB.getHeight()),
                                                 BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = result.createGraphics();

        graphics.drawImage(bufferedA, 0, 0, null);
        graphics.drawImage(bufferedB, bufferedA.getWidth(), 0, null);

        int radius = 2;
        graphics.setStroke(new BasicStroke(2));
        for (PointsPair pointsPair : matching.getPointsPairs()) {
            int xA = (int) (pointsPair.getPointA().getOriginalX() - radius);
            int yA = (int) (pointsPair.getPointA().getOriginalY() - radius);
            graphics.setColor(getSpectrum(pointsPair.getPointA().getProbability()));
            graphics.drawOval(xA, yA, 2 * radius, 2 * radius);

            int xB = (int) (pointsPair.getPointB().getOriginalX() - radius + imageA.getWidth());
            int yB = (int) (pointsPair.getPointB().getOriginalY() - radius);
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
