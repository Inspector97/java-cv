package usr.afast.image.points;

import lombok.*;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

import static java.lang.Math.sqrt;
import static usr.afast.image.util.Math.sqr;

@Getter
@RequiredArgsConstructor(staticName = "at")
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode
public class InterestingPoint implements Serializable {
    @NonNull
    private int x;

    @NonNull
    private int y;

    @NonNull
    private double probability;

    @Setter
    @Accessors(chain = true)
    private double scale;

    @Setter
    @Accessors(chain = true)
    private double angle;

    @Setter
    @Accessors(chain = true)
    private int octave, layer;

    public static double distance(@NotNull InterestingPoint a, @NotNull InterestingPoint b) {
        return sqrt(sqr(a.x - b.y) + sqr(a.x - b.y));
    }
}
