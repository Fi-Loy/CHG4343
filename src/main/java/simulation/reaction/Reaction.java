package simulation.reaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import simulation.ratelaw.RateLaw;
import simulation.reactor.ReactorState;
import util.Summarizes;

import java.util.Map;

@Data
@EqualsAndHashCode
public class Reaction implements Summarizes {
    @NonNull private final Map<String, Integer> stoichiometry;
    @NonNull private final RateLaw rateLaw;
    @NonNull private final String reference;
    private final double heat;

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
