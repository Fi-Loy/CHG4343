package simulation.reactor;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.NonNull;
import simulation.components.Species;
import simulation.reaction.Reaction;

import java.util.List;

public class ReactorFactory {
    public static Reactor createReactor(@NonNull JsonNode reactorNode, @NonNull Reaction reaction, @NonNull List<Species> speciesList) {
        String type = reactorNode.get("type").asText();

        switch (type.toLowerCase()) {
            case "packed-bed" -> {
                double Wf = reactorNode.get("Wf").asDouble();
                double alpha = reactorNode.get("alpha").asDouble();
                String mode = reactorNode.get("mode").asText("adiabatic");
                return new PackedBedReactor(reaction, speciesList, Wf, alpha, mode);
            }
            default -> throw new IllegalArgumentException("Unsupported reactor type: " + type);
        }
    }
}
