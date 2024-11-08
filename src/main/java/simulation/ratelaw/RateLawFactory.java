package simulation.ratelaw;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class RateLawFactory {
    public static RateLaw createRateLaw(@NonNull JsonNode rateLawNode) {
        String type = rateLawNode.get("type").asText();
        return switch (type.toLowerCase()) {
            case "arrhenius" -> createArrheniusRateLaw(rateLawNode);
            default -> throw new IllegalArgumentException("Unsupported rate law type: " + type);
        };
    }

    private static RateLaw createArrheniusRateLaw(@NonNull JsonNode rateLawNode) {
        JsonNode params = rateLawNode.get("parameters");
        double k0 = params.get("k0").asDouble();
        double E = params.get("E").asDouble();
        double T0 = params.get("T0").asDouble();

        Map<String, Double> orders = new HashMap<>();
        JsonNode ordersNode = rateLawNode.get("orders");
        Iterator<String> fieldNames = ordersNode.fieldNames();
        while (fieldNames.hasNext()) {
            String name = fieldNames.next();
            double order = ordersNode.get(name).asDouble();
            orders.put(name, order);
        }

        return new ArrheniusRateLaw(k0, E, T0, orders);
    }
}
