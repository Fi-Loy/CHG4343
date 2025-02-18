package simulation.ratelaw;

import com.google.common.collect.ImmutableMap;
import lombok.NonNull;
import simulation.reactor.ReactorState;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public record ArrheniusRateLaw(
        double k0,
        double E,
        double T0,
        @NonNull ImmutableMap<String,Double> orders
) implements RateLaw {

    // calculate raw, including T dependent rate constant using ReactorState
    @Override
    public double calculateRate(@NonNull ReactorState state) {
        double R = 8.314;
        double temperature = state.temperature();
        double rate = k0 * Math.exp((E / R) * (1 / T0 - 1 / temperature));

        for (String speciesName : orders.keySet()) {
            if (orders.containsKey(speciesName)) {
                Double order = orders.get(speciesName);
                double concentration = state.concentrations().get(speciesName);
                rate *= Math.pow(concentration, order);
            }
        }
        return -rate;
    }

    // summary of the rate law    @Override
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
        output.append(String.format("k0 = %.4e\n", k0));
        output.append(String.format("E  = %.2f\n", E));
        output.append("R  = 8.314\n");
        output.append(String.format("T0 = %.2f\n", T0));


        System.out.println(output);
    }
}

