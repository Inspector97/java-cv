package usr.afast.image.math;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Matrix {
    private int width;
    private int height;
    private double[][] matrix;

    @NotNull
    @Contract("_ -> new")
    public static Matrix ofSquare (@NotNull double[][] matrix) {
        int size = matrix.length;
        if (size == 0) throw new IllegalArgumentException("Матрица не может быть пустой");
        for (double[] row : matrix) {
            if (row.length != size) throw new IllegalArgumentException("Матрица не квадратна");
        }
        return new Matrix(size, size, matrix);
    }

    @NotNull
    @Contract(" -> new")
    public Matrix reverse() {
        double[][] reversedMatrix = new double[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                reversedMatrix[i][j] = matrix[j][i];
            }
        }
        return new Matrix(width, height, reversedMatrix);
    }
}
