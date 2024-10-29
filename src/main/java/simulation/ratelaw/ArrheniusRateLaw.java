package simulation.ratelaw;

import simulation.components.ReactorComponent;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

public class ArrheniusRateLaw implements ReactionRateLaw {
    private final double k0; // Pre-exponential factor
    private final double E;  // Activation energy in J/mol
    private final Map<String, Double> reactantOrders;

    public ArrheniusRateLaw(double k0, double E, Map<String, Double> reactantOrders) {
        this.k0 = k0;
        this.E = E;
        this.reactantOrders = reactantOrders;
    }

    @Override
    public double calculateRate(List<ReactorComponent> reactantConcentrations,
                                Optional<List<ReactorComponent>> productConcentrations) {
        // Assume temperature is the same for all components
        double T = reactantConcentrations.get(0).getTemperature();

        // Calculate rate constant at current temperature
        double R = 8.314; // J/(mol·K)
        double k = k0 * Math.exp(-E / (R * T));

        double rate = k;
        for (ReactorComponent reactant : reactantConcentrations) {
            String name = reactant.getComponent().getName();
            double order = reactantOrders.getOrDefault(name, 0.0);
            rate *= Math.pow(reactant.getConcentration(), order);
        }
        return rate;
    }


    @Override
    public String prettyPrint() {
        StringBuilder output = new StringBuilder();

        output.append("Rate Law Type: Arrhenius Power Law\n");

        output.append("Rate Law Equation:\n");
        output.append("-ra = k(T) ");
        StringJoiner joiner = new StringJoiner(" * ");
        for (Map.Entry<String, Double> entry : reactantOrders.entrySet()) {
            String reactant = entry.getKey();
            double order = entry.getValue();
            joiner.add("[" + reactant + "]^" + order);
        }
        output.append("* ").append(joiner).append("\n");

        output.append("\nWhere:\n");
        output.append("k(T) = k₀ * exp(-E / (R * T))\n");
        output.append(String.format("k₀ = %.4e m^3/(mol-s)\n", k0));
        output.append(String.format("E  = %.2f J/mol\n", E));
        output.append("R  = 8.314 J/(mol·K)\n");

        double T0 = 450.0;
        double R = 8.314;
        double kAtT0 = k0 * Math.exp(-E / (R * T0));
        output.append(String.format("At T0 = %.2f K, k(T0) = %.4e m^3/(mol-s)\n", T0, kAtT0));

        output.append("\nReactant Orders:\n");
        output.append(String.format("%-10s | %-10s\n", "Reactant", "Order"));
        output.append("-----------|------------\n");
        for (Map.Entry<String, Double> entry : reactantOrders.entrySet()) {
            output.append(String.format("%-10s | %-10.2f\n", entry.getKey(), entry.getValue()));
        }

        return output.toString();
    }


}

