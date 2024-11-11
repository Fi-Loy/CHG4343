package simulation.odesolver;

import lombok.NonNull;

public interface ODESystem {
    double[] computeDerivatives(double x, double @NonNull [] y);
}
