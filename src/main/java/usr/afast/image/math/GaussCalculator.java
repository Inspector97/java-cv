package usr.afast.image.math;

import static java.lang.Math.exp;
import static java.lang.Math.sqrt;
import static usr.afast.image.util.Math.sqr;

public class GaussCalculator {
    private double sigma;
    private double coef;

    public GaussCalculator(double sigma) {
        this.sigma = sigma;
        coef = 1 / (sqrt(2 * Math.PI) * sigma);
    }

    public double get(double x, double y) {
        return get(x) * get(y);
    }

    private double get(double value) {
        return coef * exp(-sqr(value) / (2 * sqr(sigma)));
    }
}
