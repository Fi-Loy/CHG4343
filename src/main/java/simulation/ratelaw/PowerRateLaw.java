package simulation.ratelaw;

import lombok.AllArgsConstructor;
import lombok.Data;
import simulation.components.ReactorComponent;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

@Data
@AllArgsConstructor
public class PowerRateLaw implements ReactionRateLaw {
    private final double rateConstant;
    private final Map<String, Double> reactantOrders;

    @Override
    public double calculateRate(List<ReactorComponent> reactantConcentrations,
                                Optional<List<ReactorComponent>> productConcentrations) {
        double rate = rateConstant;
        for (ReactorComponent reactant : reactantConcentrations) {
            String name = reactant.getComponent().getName();
            double order = reactantOrders.getOrDefault(name, 0.0);
            rate *= Math.pow(reactant.getConcentration(), order);
        }
        return rate;
    }

    public String prettyPrint() {
        StringBuilder output = new StringBuilder();

        output.append("Rate Law Type: Power Law\n");

        output.append("-ra = ").append(rateConstant);
        StringJoiner joiner = new StringJoiner(" * ");
        for (Map.Entry<String, Double> entry : reactantOrders.entrySet()) {
            String reactant = entry.getKey();
            double order = entry.getValue();
            joiner.add("[" + reactant + "]^" + order);
        }
        output.append(" * ").append(joiner).append("\n");

        output.append("\nReactant Orders Table:\n");
        output.append(String.format("%-10s | %-10s\n", "Reactant", "Order"));
        output.append("-----------|------------\n");
        for (Map.Entry<String, Double> entry : reactantOrders.entrySet()) {
            output.append(String.format("%-10s | %-10.2f\n", entry.getKey(), entry.getValue()));
        }

        return output.toString();
    }
}
