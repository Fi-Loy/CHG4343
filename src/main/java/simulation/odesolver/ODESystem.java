package simulation.odesolver;

public interface ODESystem {

    double[] computeDerivatives(double x, double[] y);

}
