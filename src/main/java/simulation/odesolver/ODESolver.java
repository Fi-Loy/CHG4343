package simulation.odesolver;

import lombok.NonNull;

public interface ODESolver {
    double[][] solve(ODESystem system, double @NonNull [] y0, double x0, double xEnd, double h);
}
