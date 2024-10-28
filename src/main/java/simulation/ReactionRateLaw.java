package simulation;

import java.util.List;
import java.util.Optional;

public interface ReactionRateLaw {
    /**
     * Calculates the rate of reaction given the concentrations of reactants and, if available, products.
     *
     * @param reactantConcentrations List of reactant concentrations
     * @param productConcentrations  Optional list of product concentrations (used in reversible reactions)
     * @return The calculated reaction rate
     */
    double calculateRate(List<ReactorComponent> reactantConcentrations,
                         Optional<List<ReactorComponent>> productConcentrations);

    String prettyPrint();
}


