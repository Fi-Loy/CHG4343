package simulation.odesolver;

import lombok.NonNull;

public class RK4Solver implements ODESolver{

    @Override
    public double[][] solve(@NonNull ODESystem system, double@NonNull [] y0, double x0, double xEnd, double h) {
        int steps = (int) Math.ceil((xEnd - x0) / h) + 1;
        double[][] results = new double[steps][y0.length];

        double x = x0;
        double[] y = y0.clone();
        results[0] = y.clone();

        int step = 1;
        while (x < xEnd && step < steps) {
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
            results[step] = y.clone();

            step++;
        }

        return results;
    }

    public static double[] addArrays(double @NonNull  [] a, double @NonNull [] b) {
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] + b[i];
        }
        return result;
    }

    public static double[] multiplyArray(double @NonNull [] a, double scalar) {
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] * scalar;
        }
        return result;
    }

}
