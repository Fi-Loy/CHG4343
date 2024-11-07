package simulation.odesolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EulerSolver implements ODESolver {

    @Override
    public List<double[]> solve(ODESystem system, double[] y0, double x0, double xEnd, double h) {
        List<double[]> results = new ArrayList<>();
        double x = x0;
        double[] y = y0.clone();
        results.add(y.clone());

        while (x < xEnd) {
            double[] dydx = system.computeDerivatives(x, y);

            for (int i = 0; i < y.length; i++) {
                y[i] += h * dydx[i];
            }

            x += h;

            results.add(y.clone());
            System.out.println("New Y" + Arrays.toString(y.clone()) + Arrays.toString(dydx.clone()));
        }

        return results;
    }
}
