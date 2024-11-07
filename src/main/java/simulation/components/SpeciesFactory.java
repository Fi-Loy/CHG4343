package simulation.components;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;

public class SpeciesFactory {
    public static List<Species> createSpecies(JsonNode speciesNode) {
        List<Species> speciesList = new ArrayList<>();
        for (JsonNode spNode : speciesNode) {
            String name = spNode.get("name").asText();
            double cp = spNode.get("cp").asDouble();
            Species sp = new Species(name, cp);
            speciesList.add(sp);
        }
        return speciesList;
    }
}
