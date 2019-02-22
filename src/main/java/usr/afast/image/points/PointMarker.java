package usr.afast.image.points;

import org.jetbrains.annotations.NotNull;
import usr.afast.image.wrapped.WrappedImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class PointMarker {
    public static BufferedImage markPoints(@NotNull List<InterestingPoint> interestingPoints, WrappedImage image) {
        BufferedImage bufferedImage = WrappedImage.save(image);

        Graphics graphics = bufferedImage.createGraphics();
        graphics.setColor(Color.GREEN);
        int radius = 1;
        for (InterestingPoint interestingPoint : interestingPoints) {
            int x = interestingPoint.getX() - radius;
            int y = interestingPoint.getY() - radius;
            graphics.fillOval(x, y, 2 * radius, 2 * radius);
        }
        graphics.dispose();

        return bufferedImage;
    }
}
