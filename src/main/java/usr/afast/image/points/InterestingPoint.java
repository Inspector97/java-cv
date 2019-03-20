package usr.afast.image.points;

import lombok.*;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import static java.lang.Math.sqrt;
import static usr.afast.image.util.Math.sqr;

@Getter
@RequiredArgsConstructor(staticName = "at")
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode
public class InterestingPoint {
    @NonNull
    private double originalX;
    @NonNull
    private double originalY;
    @NonNull
    private double probability;
    @NonNull
    private double scale;
    @NonNull
    private double originalScale;

    @NonNull
    private double scaledX;
    @NonNull
    private double scaledY;

    @Setter
    @Accessors(chain = true)
    private double angle;

    public static double distance(@NotNull InterestingPoint a, @NotNull InterestingPoint b) {
        return sqrt(sqr(a.originalX - b.originalX) + sqr(a.originalY - b.originalY));
    }
}
