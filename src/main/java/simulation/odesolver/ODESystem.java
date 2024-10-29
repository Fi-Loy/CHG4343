package simulation.odesolver;

public interface ODESystem {

    /**
     * Computes the derivatives dy/dx at a given x and state y.
     * @param x The independent variable (e.g., catalyst weight W)
     * @param y The current state vector
     * @return The array of derivatives dy/dx
     */
    double[] computeDerivatives(double x, double[] y);
}
