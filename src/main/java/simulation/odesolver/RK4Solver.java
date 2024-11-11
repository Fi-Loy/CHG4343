package simulation.odesolver;

import lombok.NonNull;
import util.VectorUtils;
import java.util.Arrays;

public class RK4Solver implements ODESolver{

    @Override
    public double[][] solve(
            @NonNull ODESystem system,
            double@NonNull [] y0,
            double x0,
            double xEnd,
            double h
    ) {

        if (y0.length == 0) {
            throw new IllegalArgumentException("Initial conditions (y0) must not be empty.");
        }
        if (h <= 0) {
            throw new IllegalArgumentException("Step size (h) must be positive.");
        }
        if (xEnd <= x0) {
            throw new IllegalArgumentException("xEnd must be greater than x0.");
        }

        int steps = (int) Math.ceil((xEnd - x0) / h) + 1;
        double[][] results = new double[steps][y0.length];

        double x = x0;
        double[] y = Arrays.copyOf(y0, y0.length);
        results[0] = Arrays.copyOf(y, y.length);

        int step = 1;
        while (x < xEnd) {
            double[] k1 = VectorUtils.multiplyVector(system.computeDerivatives(x, y), h);
            double[] yTemp = VectorUtils.addVectors(y, VectorUtils.multiplyVector(k1, 0.5));

            double[] k2 = VectorUtils.multiplyVector(system.computeDerivatives(x + h / 2.0, yTemp), h);
            yTemp = VectorUtils.addVectors(y, VectorUtils.multiplyVector(k2, 0.5));

            double[] k3 = VectorUtils.multiplyVector(system.computeDerivatives(x + h / 2.0, yTemp), h);
            yTemp = VectorUtils.addVectors(y, k3);

            double[] k4 = VectorUtils.multiplyVector(system.computeDerivatives(x + h, yTemp), h);

            for (int i = 0; i < y.length; i++) {
                y[i] += (k1[i] + 2 * k2[i] + 2 * k3[i] + k4[i]) / 6.0;
            }

            x += h;
            results[step] = Arrays.copyOf(y, y.length);

            step++;
        }

        return results;
    }
}
