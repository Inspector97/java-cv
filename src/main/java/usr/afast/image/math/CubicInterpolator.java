package usr.afast.image.math;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class CubicInterpolator {
    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    private static Vector4 cubicInterpolate(@NotNull Vector4[] points, double x) {
        Vector4 result = points[1];
        Vector4 temp = points[0].mult(-0.5);
        temp = temp.add(points[2].mult(0.5));
        result = result.add(temp.mult(x));
        temp = points[0];
        temp = temp.add(points[1].mult(-2.5));
        temp = temp.add(points[2].mult(2));
        temp = temp.add(points[3].mult(-0.5));
        result = result.add(temp.mult(x * x));
        temp = points[0].mult(-0.5);
        temp = temp.add(points[1].mult(1.5));
        temp = temp.add(points[2].mult(-1.5));
        temp = temp.add(points[3].mult(0.5));
        result = result.add(temp.mult(x * x * x));
        return result;
    }

    @NotNull
    @Contract("_, _, _ -> new")
    public static Color bicubicInterpolate(Color[][] p, double x, double y) {
        Vector4[] tmp = new Vector4[4];
        Vector4[][] data = new Vector4[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                data[i][j] = Vector4.from(p[i][j]);
            }
            tmp[i] = cubicInterpolate(data[i], y);
        }
        Vector4 res = cubicInterpolate(tmp, x);
        return res.getColor();
    }
}
