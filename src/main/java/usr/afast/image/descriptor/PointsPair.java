package usr.afast.image.descriptor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import usr.afast.image.points.InterestingPoint;

import java.io.Serializable;

@AllArgsConstructor(staticName = "from")
@Getter
public class PointsPair implements Serializable {
    private InterestingPoint pointA;
    private InterestingPoint pointB;
}
