package usr.afast.image.descriptor;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
@AllArgsConstructor
public class Matching implements Serializable {
    private List<PointsPair> pointsPairs;
    private List<AbstractDescriptor> descriptorsA;
    private List<AbstractDescriptor> descriptorsB;
}
