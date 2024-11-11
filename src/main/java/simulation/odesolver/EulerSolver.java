package simulation.odesolver;

import lombok.NonNull;

public class EulerSolver implements ODESolver {

    @Override
    public double[][] solve(
            @NonNull ODESystem system,
            double @NonNull  [] y0,
            double x0,
            double xEnd,
            double h
    ) {
        int steps = (int) Math.ceil((xEnd - x0) / h) + 1;
        double[][] results = new double[steps][y0.length];

        double x = x0;
        double[] y = y0.clone();
        results[0] = y.clone();

        int step = 1;
        while (x < xEnd && step < steps) {
            double[] dydx = system.computeDerivatives(x, y);

            for (int i = 0; i < y.length; i++) {
                y[i] += h * dydx[i];
            }

            x += h;
            results[step] = y.clone();

            step++;
        }

        return results;
    }
}
