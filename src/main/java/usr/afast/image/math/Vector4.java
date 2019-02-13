package usr.afast.image.math;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static java.lang.Double.min;
import static java.lang.Math.max;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class Vector4 {
    private double x;
    private double y;
    private double z;
    private double w;

    @NotNull
    @Contract("_ -> new")
    static Vector4 from(@NotNull Color color) {
        return new Vector4(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    @NotNull
    @Contract("_, _, _, _ -> new")
    static Vector4 from(double x, double y, double z, double w) {
        return new Vector4(x, y, z, w);
    }

    Vector4 add(Vector4 b) {
        return new Vector4(x + b.x, y + b.y, z + b.z, w + b.w);
    }

    Vector4 mult(double b) {
        return new Vector4(x * b, y * b, z * b, w * b);
    }

    Color getColor() {
        return new Color((float) max(0, min(1, x / 255)),
                         (float) max(0, min(1, y / 255)),
                         (float) max(0, min(1, z / 255)),
                         (float) max(0, min(1, w / 255)));
    }

    double get(int idx) {
        assert idx >= 0 && idx < 4;
        switch (idx) {
            case 0:
                return x;
            case 1:
                return y;
            case 2:
                return z;
            case 3:
                return w;
            default:
                return 0;
        }
    }
}
