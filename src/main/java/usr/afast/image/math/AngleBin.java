package usr.afast.image.math;

import lombok.Getter;
import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.Contract;
import usr.afast.image.wrapped.Matrix;

import java.util.LinkedList;
import java.util.List;

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

    public Double[] getPeeks() {
        List<Pair<Integer, Double>> values = new LinkedList<>();

        for (int i = 0; i < size; i++) {
            values.add(new Pair<>(i, bin[i]));
        }

        values.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        List<Double> result = new LinkedList<>();

        for (int i = 0; i < 2; i++) {
            if (i == 1) {
                if (values.get(i).getValue() < result.get(0) * 0.8)
                    break;
            }
            int idx = values.get(i).getKey();
            double x = values.get(i).getKey() + 0.5;
            int prevIdx = (idx - 1 + size) % size;
            double prevX = x - 1;
            int nextIdx = (idx + 1) % size;
            double nextX = x + 1;

            double value = bin[idx];
            double prevValue = bin[prevIdx];
            double nextValue = bin[nextIdx];

            result.add(normalize(getRealPeek(prevX, prevValue, x, value, nextX, nextValue) * step));

        }

        return result.toArray(new Double[0]);
    }

    @Contract(pure = true)
    private double getRealPeek(double x1, double y1, double x2, double y2, double x3, double y3) {
        double a = (y3 - (x3 * (y2 - y1) + x2 * y1 - x1 * y2) / (x2 - x1)) /
                   (x3 * (x3 - x1 - x2) + x1 * x2);
        double b = (y2 - y1) / (x2 - x1) - a * (x1 + x2);
        return -b / (2 * a);
    }
}
