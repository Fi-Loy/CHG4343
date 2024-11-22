package simulation.reaction;

import com.fasterxml.jackson.databind.JsonNode;

import java.security.KeyStore.Entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

import lombok.NonNull;
import simulation.ratelaw.RateLaw;
import simulation.ratelaw.RateLawFactory;

public class ReactionFactory {
    public static Reaction createReaction(@NonNull JsonNode reactionNode) {
        Map<String, Integer> stoichiometry = new HashMap<>();
        JsonNode stoichNode = reactionNode.get("reaction").get("stoichiometry");//temp fix, means all reaction objects have to have the same name: "reaction"
        Iterator<String> fieldNames = stoichNode.fieldNames();
        String reference = null;
        while (fieldNames.hasNext()) {
            String name = fieldNames.next();
            // TODO not good, fails in the case that a product is put first
            //the above issue should be fixed with the explicit "ref" designation in the JSON and this revised if statement
            if (name.equals("ref")){
                reference = stoichNode.get(name).asText();
            }
            else{
                int coeff = stoichNode.get(name).asInt();
                stoichiometry.put(name, coeff);
            }
            
        }
        double heat = reactionNode.get("reaction").get("heat").asDouble();//same issue here, see line 19 comment
        RateLaw rateLaw = RateLawFactory.createRateLaw(reactionNode.get("reaction").get("rateLaw"));//see above

        if (reference == null){
            throw new NullPointerException("Reaction has no components");
        }
        return new Reaction(stoichiometry, rateLaw, reference, heat);
    }

    public static List<Reaction> createReactionsList(@NonNull JsonNode reactionListNode){
        List<Reaction> reactionsList = new ArrayList<>();
        for(JsonNode reactionNode :reactionListNode){
            reactionsList.add(createReaction(reactionNode));
        }
        return reactionsList;
    }
}
