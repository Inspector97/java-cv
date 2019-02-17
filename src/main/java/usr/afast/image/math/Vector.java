package usr.afast.image.math;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Vector {
    private int length;
    private double[] vector;

    @NotNull
    @Contract("_ -> new")
    public static Vector of(@NotNull double[] vector) {
        int length = vector.length;
        return new Vector(length, vector);
    }

}
