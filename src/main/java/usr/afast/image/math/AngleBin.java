package usr.afast.image.math;

import lombok.Getter;
import org.jetbrains.annotations.Contract;
import usr.afast.image.wrapped.Matrix;

@Getter
public class AngleBin {
    private static final double MAX = 2 * Math.PI;
    private static final double EPS = 1e-6;
    private double[] bin;
    private int size;
    private final double step;

    public AngleBin(int size) {
        this.size = size;
        this.bin = new double[size];
        step = MAX / size;
    }

    public void addAngle(double angle, double value) {
        angle = normalize(angle);

        angle /= step;
        int binIdx = (int) angle;
        double binCenter = binIdx + 0.5;
        double weight = Math.abs(binCenter - angle);
        int neighbourIdx = (binIdx + 1) % size;
        if (angle <= binCenter) {
            neighbourIdx = (binIdx - 1 + size) % size;
        }

        bin[binIdx] += weight * value;
        bin[neighbourIdx] += (1 - weight) * value;
    }

    @Contract(pure = true)
    private double normalize(double angle) {
        while (angle < 0)
            angle += MAX;
        while (angle >= MAX)
            angle -= MAX;
        return angle;
    }

    public double[] getPeeks() {
        double[] result = new double[1];
        double max = Double.MIN_VALUE;
        double angle = -1;
        for (int i = 0; i < size; i++) {
            if (max < bin[i]) {
                max = bin[i];
                angle = (i + 0.5) * step;
            }
        }
        result[0] = angle;

        double bord = max * 0.8;
        double secondMax = Double.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            if (bin[i] > bord && bin[i] != max && bin[i] > secondMax) {
                secondMax = bin[i];
                angle = (i + 0.5) * step;
            }
        }
        if (secondMax != Double.MIN_VALUE) {
            double tmp = result[0];
            result = new double[2];
            result[0] = tmp;
            result[1] = angle;
        }

        return result;
    }
}