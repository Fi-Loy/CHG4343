package simulation.ratelaw;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

public class RateLawFactory {

    public static ReactionRateLaw createRateLaw(JsonNode reactionJson) throws IllegalArgumentException {
        String type = reactionJson.get("reactionType").asText();

        if ("power-law".equals(type)) {
            JsonNode rateConstants = reactionJson.get("rateConstants");
            double E = rateConstants.get("E").asDouble();
            double kAtT0 = rateConstants.get("kAtT0").asDouble();
            double T0 = rateConstants.get("T0").asDouble();

            double R = 8.314; // J/(mol·K)
            double k0 = kAtT0 * Math.exp(E / (R * T0));

            Map<String, Double> reactantOrders = new HashMap<>();
            for (JsonNode reactantNode : reactionJson.get("reactants")) {
                String name = reactantNode.get("name").asText();
                double order = reactantNode.get("order").asDouble();
                reactantOrders.put(name, order);
            }

            return new ArrheniusRateLaw(k0, E, reactantOrders);
        } else {
            throw new IllegalArgumentException("Unsupported rate law type: " + type);
        }
    }

}

