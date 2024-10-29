package simulation.reaction;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import simulation.components.Component;
import simulation.components.ReactorComponent;
import simulation.ratelaw.RateLawFactory;
import simulation.ratelaw.ReactionRateLaw;
import simulation.reaction.Reaction;

import java.util.*;

public class ReactionFactory {

    public static Reaction createReaction(JsonNode rootNode) {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode componentsJson = rootNode.get("components");
        Map<String, Component> components = objectMapper.convertValue(componentsJson,
                objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Component.class));

        List<ReactorComponent> reactants = parseComponents(rootNode, components, "reactants");

        List<ReactorComponent> products = parseComponents(rootNode, components, "products");

        JsonNode reactionJson = rootNode.get("reaction");
        ReactionRateLaw rateLaw = RateLawFactory.createRateLaw(reactionJson);
        Reaction rxn = new Reaction(reactants, products, rateLaw);
        System.out.println(rxn.prettyPrint());
        return rxn;
    }

    private static List<ReactorComponent> parseComponents(JsonNode rootNode, Map<String, Component> componentsMap, String type) {
        List<ReactorComponent> components = new ArrayList<>();
        JsonNode componentsJson = rootNode.get("reaction").get(type);

        JsonNode reactorConditions = rootNode.get("reactorConditions");
        JsonNode feedMoleFractions = reactorConditions.get("feedMoleFractions");
        double P0 = reactorConditions.get("pressure").asDouble();
        double T0 = reactorConditions.get("temperature").asDouble();
        double v0 = reactorConditions.get("v0").asDouble();

        double R = 8.314; // J/(mol·K)
        double F_T0 = (P0 * v0) / (R * T0);

        for (JsonNode componentNode : componentsJson) {
            String name = componentNode.get("name").asText();
            double stoichiometry = componentNode.get("stoichiometry").asDouble();

            double moleFraction = feedMoleFractions.has(name) ? feedMoleFractions.get(name).asDouble() : 0.0;
            double initialMolarFlowRate = moleFraction * F_T0;

            components.add(new ReactorComponent(componentsMap.get(name), stoichiometry, initialMolarFlowRate));
        }
        return components;
    }

}
