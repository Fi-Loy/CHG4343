package simulation.odesolver;

import lombok.NonNull;

import java.util.List;

public interface ODESolver {
    double[][] solve(ODESystem system, double @NonNull [] y0, double x0, double xEnd, double h);


}
