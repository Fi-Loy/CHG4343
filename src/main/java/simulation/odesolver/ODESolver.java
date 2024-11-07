package simulation.odesolver;

import java.util.List;

public interface ODESolver {
    double[][] solve(ODESystem system, double[] y0, double x0, double xEnd, double h);


}
