package simulation.components;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableList;
import lombok.NonNull;

public class SpeciesFactory {
    public static ImmutableList<Species> createSpecies(@NonNull JsonNode speciesNode) {
        ImmutableList.Builder<Species> speciesListBuilder = ImmutableList.builder();
        for (JsonNode spNode : speciesNode) {
            String name = spNode.get("name").asText();
            double cp = spNode.get("cp").asDouble();
            Species sp = new Species(name, cp);
            speciesListBuilder.add(sp);
        }
        return speciesListBuilder.build();
    }
}
