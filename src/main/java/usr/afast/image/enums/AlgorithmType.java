package usr.afast.image.enums;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public enum AlgorithmType {
    Gauss("gs"),
    Pruitt("pr"),
    Scharr("schr"),
    Sobel("sb");

    static {
        Set<String> keysSet = new HashSet<>();
        for (AlgorithmType type : AlgorithmType.values()) {
            keysSet.add(type.code);
        }
        if (keysSet.size() != AlgorithmType.values().length) {
            throw new RuntimeException("Invalid AlgorithmType codes. Found duplicates.");
        }
    }

    String code;

    AlgorithmType(String code) {
        this.code = code;
    }
}
