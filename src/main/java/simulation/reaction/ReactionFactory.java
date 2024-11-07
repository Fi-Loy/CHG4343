package simulation.reaction;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import simulation.ratelaw.RateLaw;
import simulation.ratelaw.RateLawFactory;

public class ReactionFactory {
    public static Reaction createReaction(JsonNode reactionNode) {
        Map<String, Integer> stoichiometry = new HashMap<>();
        JsonNode stoichNode = reactionNode.get("stoichiometry");
        Iterator<String> fieldNames = stoichNode.fieldNames();
        String first = null;
        while (fieldNames.hasNext()) {
            String name = fieldNames.next();
            if (first==null){
                first = name;
            }
            int coeff = stoichNode.get(name).asInt();
            stoichiometry.put(name, coeff);
        }
        double heat = reactionNode.get("heat").asDouble();
        RateLaw rateLaw = RateLawFactory.createRateLaw(reactionNode.get("rateLaw"));

        return new Reaction(stoichiometry, rateLaw, first, heat);
    }
}
