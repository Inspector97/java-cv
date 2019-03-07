package usr.afast.image.descriptor;

import org.jetbrains.annotations.NotNull;
import usr.afast.image.points.InterestingPoint;

import java.util.Arrays;

import static usr.afast.image.util.Math.sqr;

public abstract class AbstractDescriptor {
    public void normalize() {
        double[] descriptor = getDescriptor();
        double sum = Arrays.stream(descriptor).sum();
        if (Math.abs(sum) < 1e-3)
            return;
        setDescriptor(Arrays.stream(descriptor).map(operand -> operand / sum).toArray());
    }

    public static double distance(@NotNull AbstractDescriptor descriptorA, @NotNull AbstractDescriptor descriptorB) {
        double[] descA = descriptorA.getDescriptor();
        double[] descB = descriptorB.getDescriptor();
        if (descA.length != descB.length)
            throw new IllegalArgumentException("DESCRIPTORS LENGTH DIFFER!");
        double sum = 0;
        for (int i = 0; i < descA.length; i++) {
            sum += sqr(descA[i] - descB[i]);
        }
        return Math.sqrt(sum);
    }

    abstract void setDescriptor(double[] descriptor);

    abstract double[] getDescriptor();

    public abstract InterestingPoint getPoint();
}
