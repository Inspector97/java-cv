package usr.afast.image.descriptor;

import org.jetbrains.annotations.NotNull;
import usr.afast.image.points.InterestingPoint;
import usr.afast.image.wrapped.Matrix;

import java.util.List;

@FunctionalInterface
public interface GistogramBasedDescriptor {
    AbstractDescriptor at(Matrix gradient,
                          Matrix gradientAngle,
                          InterestingPoint interestingPoint);
}
