package simulation.odesolver;

import java.util.ArrayList;
import java.util.List;

public class RK4Solver {

    public static List<double[]> solve(ODESystem system, double[] y0, double x0, double xEnd, double h) {
        List<double[]> results = new ArrayList<>();
        double x = x0;
        double[] y = y0.clone();
        results.add(y.clone());

        while (x < xEnd) {
            double[] k1 = multiplyArray(system.computeDerivatives(x, y), h);
            double[] yTemp = addArrays(y, multiplyArray(k1, 0.5));

            double[] k2 = multiplyArray(system.computeDerivatives(x + h / 2.0, yTemp), h);
            yTemp = addArrays(y, multiplyArray(k2, 0.5));

            double[] k3 = multiplyArray(system.computeDerivatives(x + h / 2.0, yTemp), h);
            yTemp = addArrays(y, k3);

            double[] k4 = multiplyArray(system.computeDerivatives(x + h, yTemp), h);

            for (int i = 0; i < y.length; i++) {
                y[i] += (k1[i] + 2 * k2[i] + 2 * k3[i] + k4[i]) / 6.0;
            }

            x += h;
            results.add(y.clone());
        }

        return results;
    }

    public static double[] addArrays(double[] a, double[] b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Input arrays cannot be null.");
        }
        if (a.length != b.length) {
            throw new IllegalArgumentException("Input arrays must have the same length.");
        }

        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] + b[i];
        }
        return result;
    }

    public static double[] multiplyArray(double[] a, double scalar) {
        if (a == null) {
            throw new IllegalArgumentException("Input array cannot be null.");
        }

        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] * scalar;
        }
        return result;
    }
}
