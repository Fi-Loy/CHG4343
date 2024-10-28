package simulation;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.Map;

public class RateLawFactory {

    public static ReactionRateLaw createRateLaw(JsonNode reactionJson) throws IllegalArgumentException {
        String type = reactionJson.get("reactionType").asText();

        if ("power-law".equals(type)) {
            double rateConstant = reactionJson.get("rateConstants").get("forward").asDouble();

            // Create a map for reactant orders
            Map<String, Double> reactantOrders = new HashMap<>();
            for (JsonNode reactantNode : reactionJson.get("reactants")) {
                String name = reactantNode.get("name").asText();
                double order = reactantNode.get("order").asDouble();
                reactantOrders.put(name, order);
            }

            return new PowerRateLaw(rateConstant, reactantOrders);
        } else {
            throw new IllegalArgumentException("Unsupported rate law type: " + type);
        }
    }
}
