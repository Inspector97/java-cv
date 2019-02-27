package usr.afast.image.descriptor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import usr.afast.image.points.InterestingPoint;

@AllArgsConstructor(staticName = "from")
@Getter
public class PointsPair {
    private InterestingPoint pointA;
    private InterestingPoint pointB;
}
