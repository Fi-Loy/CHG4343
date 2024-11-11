package simulation.reaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import simulation.ratelaw.RateLaw;
import simulation.reactor.ReactorState;
import util.Summarizable;

import java.util.Map;

public record Reaction(
        @NonNull Map<String, Integer> stoichiometry,
        @NonNull RateLaw rateLaw,
        @NonNull String reference,
        double heat
) implements Summarizable {
    public double calculateRate(@NonNull ReactorState state) {
        return rateLaw.calculateRate(state);
    }

    public void summarize(){
        StringBuilder output = new StringBuilder();

        output.append("Reaction Summary\n");
        output.append("----------------\n");

        output.append("Stoichiometry:\n");
        output.append(String.format("%-10s | %-10s\n", "Species", "Coefficient"));
        output.append("-----------|------------\n");
        for (Map.Entry<String, Integer> entry : stoichiometry.entrySet()) {
            output.append(String.format("%-10s | %-10d\n", entry.getKey(), entry.getValue()));
        }

        System.out.println(output);
        rateLaw.summarize();
    }

}
