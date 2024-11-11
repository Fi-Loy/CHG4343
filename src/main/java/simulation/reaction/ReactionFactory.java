package simulation.reaction;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import lombok.NonNull;
import simulation.ratelaw.RateLaw;
import simulation.ratelaw.RateLawFactory;

public class ReactionFactory {
    public static Reaction createReaction(@NonNull JsonNode reactionNode) {
        Map<String, Integer> stoichiometry = new HashMap<>();
        JsonNode stoichNode = reactionNode.get("stoichiometry");
        Iterator<String> fieldNames = stoichNode.fieldNames();
        String first = null;
        while (fieldNames.hasNext()) {
            String name = fieldNames.next();
            // TODO not good, fails in the case that a product is put first
            if (first==null){
                first = name;
            }
            int coeff = stoichNode.get(name).asInt();
            stoichiometry.put(name, coeff);
        }
        double heat = reactionNode.get("heat").asDouble();
        RateLaw rateLaw = RateLawFactory.createRateLaw(reactionNode.get("rateLaw"));

        if (first == null){
            throw new NullPointerException("Reaction has no components");
        }
        return new Reaction(stoichiometry, rateLaw, first, heat);
    }
}
