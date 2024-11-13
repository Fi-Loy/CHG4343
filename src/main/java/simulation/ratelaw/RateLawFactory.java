package simulation.ratelaw;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.NonNull;
import com.google.common.collect.ImmutableMap;

import java.util.Iterator;

public class RateLawFactory {
    public static RateLaw createRateLaw(@NonNull JsonNode rateLawNode) {
        String type = rateLawNode.get("type").asText();
        return switch (type.toLowerCase()) {
            case "arrhenius" -> createArrheniusRateLaw(rateLawNode);
            default -> throw new IllegalArgumentException("Unsupported rate law type: " + type);
        };
    }

    // build ArrheniusRateLaw from JSON
    // TODO all validation to all fields
    // TODO write unit tests for creation
    private static RateLaw createArrheniusRateLaw(@NonNull JsonNode rateLawNode) {
        JsonNode params = rateLawNode.get("parameters");
        if (params == null || !params.has("k0") || !params.has("E") || !params.has("T0")) {
            throw new IllegalArgumentException("Arrhenius parameters (k0, E, T0) are required.");
        }

        double k0 = params.get("k0").asDouble();
        double E = params.get("E").asDouble();
        double T0 = params.get("T0").asDouble();

        JsonNode ordersNode = rateLawNode.get("orders");
        if (ordersNode == null) {
            throw new IllegalArgumentException("Reaction orders must be provided.");
        }

        ImmutableMap.Builder<String, Double> ordersBuilder = ImmutableMap.builder();
        Iterator<String> fieldNames = ordersNode.fieldNames();
        while (fieldNames.hasNext()) {
            String name = fieldNames.next();
            double order = ordersNode.get(name).asDouble();
            ordersBuilder.put(name, order);
        }
        ImmutableMap<String, Double> orders = ordersBuilder.build();

        return new ArrheniusRateLaw(k0, E, T0, orders);
    }
}