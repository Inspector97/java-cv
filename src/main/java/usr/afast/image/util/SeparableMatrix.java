package usr.afast.image.util;

import lombok.AllArgsConstructor;
import usr.afast.image.wrapped.Matrix;

@AllArgsConstructor
public class SeparableMatrix {
    private Matrix first;
    private Matrix second;

    public double getAt(int x, int y) {
        return first.getAt(x, 0) * second.getAt(0, y);
    }

}
