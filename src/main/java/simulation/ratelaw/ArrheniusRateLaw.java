package simulation.ratelaw;

import simulation.components.Species;
import simulation.reactors.ReactorState;
import util.Summarizes;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class ArrheniusRateLaw extends RateLaw {
    private final double k0;
    private final double E;
    private final double T0;
    private final Map<String, Double> orders;

    public ArrheniusRateLaw(double k0, double E, double T0, Map<String, Double> orders) {
        this.k0 = k0;
        this.E = E;
        this.T0 = T0;
        this.orders = orders;
    }


    @Override
    public double calculateRate(ReactorState state) {
        double R = 8.314; // J/(mol·K)
        double temperature = state.getTemperature();
        double rate = k0 * Math.exp((E / R) * (1 / T0 - 1 / temperature));

        for (String speciesName : orders.keySet()) {
            if (orders.containsKey(speciesName)) {
                Double order = orders.get(speciesName);
                double concentration = state.getConcentrations().get(speciesName);
                rate *= Math.pow(concentration, order);
            }
        }
        return -rate;
    }

    public void summarize() {
        StringBuilder output = new StringBuilder();
        output.append("Rate Law Summary\n");
        output.append("----------------\n");

        output.append("Rate Law Type: Arrhenius Power Law\n");
        output.append("Rate Law Equation:\n");
        output.append("-ra = k(T) ");

        StringJoiner joiner = new StringJoiner(" * ");
        for (Map.Entry<String, Double> entry : orders.entrySet()) {
            String reactant = entry.getKey();
            double order = entry.getValue();
            joiner.add("[" + reactant + "]^" + order);
        }
        output.append("* ").append(joiner).append("\n");

        output.append("\nWhere:\n");
        output.append("k(T) = k0 * exp((E / R) * (1 / T0 - 1 / T))\n");
        output.append(String.format("k0 = %.4e m^3/(mol-s)\n", k0));
        output.append(String.format("E  = %.2f J/mol\n", E));
        output.append("R  = 8.314 J/(mol-K)\n");
        output.append(String.format("T0 = %.2f K\n", T0));

//        output.append("\nReactant Orders:\n");
//        output.append(String.format("%-10s | %-10s\n", "Reactant", "Order"));
//        output.append("-----------|------------\n");
//        for (Map.Entry<String, Double> entry :orders.entrySet()) {
//            output.append(String.format("%-10s | %-10.2f\n", entry.getKey(), entry.getValue()));
//        }

        System.out.println(output.toString());
    }
}

