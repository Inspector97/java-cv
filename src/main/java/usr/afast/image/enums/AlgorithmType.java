package usr.afast.image.enums;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

@Getter
public enum AlgorithmType {
    Gauss, Pruitt, Scharr, Sobel
}
