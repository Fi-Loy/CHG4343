package simulation.reaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import simulation.components.ReactorComponent;
import simulation.ratelaw.ReactionRateLaw;

import java.util.ArrayList;
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
        StringBuilder output = new StringBuilder("Reaction Details:\n");

        String reactantsString = reactants.stream()
                .map(this::formatComponent)
                .collect(Collectors.joining(" + "));

        String productsString = products.stream()
                .map(this::formatComponent)
                .collect(Collectors.joining(" + "));

        output.append("Reaction: ").append(reactantsString).append(" -> ").append(productsString).append("\n");

        output.append("\nRate Law:\n").append(rateLaw.prettyPrint());

        return output.toString();
    }

    public List<ReactorComponent> getAllComponents() {
        List<ReactorComponent> allComponents = new ArrayList<>();
        allComponents.addAll(reactants);
        allComponents.addAll(products);
        return allComponents;
    }


    private String formatComponent(ReactorComponent rc) {
        return (rc.getStoichiometry() != 1 ? rc.getStoichiometry() + " " : "") + rc.getComponent().getName();
    }

}
