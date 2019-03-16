package usr.afast.image.points;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import static java.lang.Math.sqrt;
import static usr.afast.image.util.Math.sqr;

@Getter
@AllArgsConstructor(staticName = "at")
@ToString
@EqualsAndHashCode
public class InterestingPoint {
    private int x;
    private int y;
    private double probability;
    private double radius;
    private double angle;

    public static double distance(@NotNull InterestingPoint a, @NotNull InterestingPoint b) {
        return sqrt(sqr(a.x - b.x) + sqr(a.y - b.y));
    }
}
