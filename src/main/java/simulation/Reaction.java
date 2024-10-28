package simulation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class Reaction {
    private List<ReactorComponent> reactants;
    private List<ReactorComponent> products;
    private ReactionRateLaw rateLaw;

    public double calculateReactionRate() {
        return rateLaw.calculateRate(reactants, Optional.of(products));
    }

    public String prettyPrint() {
        // Add context for the reaction equation
        StringBuilder output = new StringBuilder("Reaction Details:\n");

        // Format reactants
        String reactantsString = reactants.stream()
                .map(this::formatComponent)
                .collect(Collectors.joining(" + "));

        // Format products
        String productsString = products.stream()
                .map(this::formatComponent)
                .collect(Collectors.joining(" + "));

        // Combine to form the full reaction string
        output.append("Reaction: ").append(reactantsString).append(" -> ").append(productsString).append("\n");

        // Add the rate law pretty print
        output.append("\nRate Law:\n").append(rateLaw.prettyPrint());

        return output.toString();
    }

    private String formatComponent(ReactorComponent rc) {
        // Include stoichiometry if it's not 1
        return (rc.getStoichiometry() != 1 ? rc.getStoichiometry() + " " : "") + rc.getComponent().getName();
    }
}
