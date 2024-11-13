package simulation.reaction;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import lombok.NonNull;
import simulation.ratelaw.RateLaw;
import simulation.ratelaw.RateLawFactory;

import java.util.Iterator;

public class ReactionFactory {
    // TODO unit tests for things like no reactants, no products, no reaction, ect
    public static Reaction createReaction(@NonNull JsonNode reactionNode) {
        ImmutableMap.Builder<String, Integer> stoichiometryBuilder = ImmutableMap.builder();
        JsonNode stoichNode = reactionNode.get("stoichiometry");
        if (stoichNode == null || !stoichNode.fieldNames().hasNext()) {
            throw new IllegalArgumentException("Reaction must have at least one component in stoichiometry.");
        }

        Iterator<String> fieldNames = stoichNode.fieldNames();
        String first = null;
        while (fieldNames.hasNext()) {
            String name = fieldNames.next();
            // TODO not good, better validation
            if (first == null) {
                first = name;
            }
            int coeff = stoichNode.get(name).asInt();
            stoichiometryBuilder.put(name, coeff);
        }

        if (first == null) {
            throw new NullPointerException("Reaction has no components.");
        }

        ImmutableMap<String, Integer> stoichiometry = stoichiometryBuilder.build();
        double heat = reactionNode.get("heat").asDouble();
        RateLaw rateLaw = RateLawFactory.createRateLaw(reactionNode.get("rateLaw"));

        return new Reaction(stoichiometry, rateLaw, first, heat);
    }
}
