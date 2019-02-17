package usr.afast.image.math;

import lombok.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Matrix {
    private int size;
    private double[][] matrix;

    @NotNull
    @Contract("_ -> new")
    public static Matrix ofSquare (@NotNull double[][] matrix) {
        int size = matrix.length;
        if (size == 0) throw new IllegalArgumentException("Матрица не может быть пустой");
        for (double[] row : matrix) {
            if (row.length != size) throw new IllegalArgumentException("Матрица не квадратна");
        }
        return new Matrix(size, matrix);
    }

    @NotNull
    @Contract(" -> new")
    public Matrix reverse() {
        double[][] reversedMatrix = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                reversedMatrix[i][j] = matrix[j][i];
            }
        }
        return new Matrix(size, reversedMatrix);
    }
}
