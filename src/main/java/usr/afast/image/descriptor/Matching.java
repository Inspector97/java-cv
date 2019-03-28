package usr.afast.image.descriptor;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class Matching {
    private List<PointsPair> pointsPairs;
    private List<AbstractDescriptor> descriptorsA;
    private List<AbstractDescriptor> descriptorsB;
}
