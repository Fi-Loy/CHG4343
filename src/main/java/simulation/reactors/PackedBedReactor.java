package simulation.reactors;

import simulation.reaction.Reaction;
import simulation.components.ReactorComponent;
import simulation.odesolver.ODESystem;
import simulation.odesolver.RK4Solver;

import java.util.List;
import java.util.Optional;

public class PackedBedReactor implements Reactor, ODESystem {

    private final Reaction reaction;
    private final List<ReactorComponent> components;
    private final double W0;
    private final double Wf;
    private final double h;
    private final double T0;
    private final double F_T0;
    private final double P0;
    private final double v0;
    private final double deltaHr;
    private final double Cp;
    private final double alpha;
    private List<double[]> simulationData;

    public PackedBedReactor(Reaction reaction, List<ReactorComponent> components,
                            double W0, double Wf, double h,
                            double T0, double P0, double F_T0, double v0,
                            double deltaHr, double Cp, double alpha) {
        this.reaction = reaction;
        this.components = components;
        this.W0 = W0;
        this.Wf = Wf;
        this.h = h;
        this.T0 = T0;
        this.deltaHr = deltaHr;
        this.Cp = Cp;
        this.alpha = alpha;
        this.F_T0 = F_T0;
        this.P0 = P0;
        this.v0 = v0;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void runSimulation() {
        double[] y0 = getInitialState();
        simulationData = RK4Solver.solve(this, y0, W0, Wf, h);
    }

    @Override
    public void printResults() {
        for (double[] state : simulationData) {
            System.out.println("F_A: " + state[0] + ", T: " + state[components.size()] + ", p: " + state[components.size() + 1]);
        }
    }

    @Override
    public List<double[]> getResults() {
        return simulationData;
    }

    @Override
    public double[] computeDerivatives(double W, double[] y) {
        int nComp = components.size();
        double[] F = new double[nComp];
        for (int i = 0; i < nComp; i++) {
            F[i] = y[i];
        }
        double T = y[nComp];
        double p = y[nComp + 1];

        // Total molar flow rate
        double F_T = 0;
        for (double Fi : F) {
            F_T += Fi;
        }

        double[] C = new double[nComp];
        for (int i = 0; i < nComp; i++) {
            C[i] = (F[i] / F_T) * (p / (8.314 * T));
            components.get(i).setConcentration(C[i]);
        }

        double r1 = reaction.getRateLaw().calculateRate(reaction.getReactants(), Optional.empty());

        double[] dFdW = new double[nComp];
        for (int i = 0; i < nComp; i++) {
            double stoich = components.get(i).getStoichiometry();
            dFdW[i] = stoich * r1;
        }

        double Q = 0;
        double dTdW = (-deltaHr * r1 + Q) / (F_T * Cp);

        double dpdW = -(alpha / (2 * p)) * (F_T / F_T) * (T / T0);

        double[] dydW = new double[nComp + 2];
        System.arraycopy(dFdW, 0, dydW, 0, nComp);
        dydW[nComp] = dTdW;
        dydW[nComp + 1] = dpdW;

        return dydW;
    }

    private double[] getInitialState() {
        int nComp = components.size();
        double[] y0 = new double[nComp + 2];

        for (int i = 0; i < nComp; i++) {
            y0[i] = components.get(i).getInitialConcentration();
        }

        y0[nComp] = T0;
        y0[nComp + 1] = P0;

        return y0;
    }

    public String prettyPrint() {
        StringBuilder sb = new StringBuilder();
        sb.append("Packed Bed Reactor Conditions:\n");
        sb.append("-------------------------------\n");
        sb.append(String.format("Initial Catalyst Weight (W0): %.2f kg\n", W0));
        sb.append(String.format("Final Catalyst Weight (Wf): %.2f kg\n", Wf));
        sb.append(String.format("Integration Step Size (h): %.2f kg\n", h));
        sb.append(String.format("Initial Temperature (T0): %.2f K\n", T0));
        sb.append(String.format("Initial Pressure (P0): %.2f Pa\n", P0));
        sb.append(String.format("Initial Total Molar Flow Rate (F_T0): %.4e mol/s\n", F_T0));
        sb.append(String.format("Initial Volumetric Flow Rate (v0): %.4e m³/s\n", v0));
        sb.append(String.format("Heat of Reaction (ΔH_r): %.2f J/mol\n", deltaHr));
        sb.append(String.format("Pressure Drop Coefficient (α): %.4e kg⁻¹\n", alpha));
        sb.append("\nComponents:\n");
        sb.append(String.format("%-10s | %-15s | %-15s\n", "Name", "Initial Flow Rate", "Stoichiometry"));
        sb.append("-----------------------------------------------------\n");
        for (ReactorComponent component : components) {
            String name = component.getComponent().getName();
            double flowRate = component.getInitialConcentration();
            double stoich = component.getStoichiometry();
            sb.append(String.format("%-10s | %-15.4e | %-15.2f\n", name, flowRate, stoich));
        }
        return sb.toString();
    }
}
