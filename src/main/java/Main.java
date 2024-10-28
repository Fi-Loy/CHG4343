import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import simulation.Component;
import simulation.PowerRateLaw;
import simulation.RateLawFactory;
import simulation.Reaction;
import simulation.ReactorComponent;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("irreversible_powerlaw.json");
            JsonNode rootNode = objectMapper.readTree(inputStream);

            JsonNode componentsJson = rootNode.get("components");
            Map<String, Component> components = objectMapper.convertValue(componentsJson,
                    objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Component.class));

            List<ReactorComponent> reactants = new ArrayList<>();
            JsonNode reactantsJson = rootNode.get("reaction").get("reactants");
            for (JsonNode reactantNode : reactantsJson) {
                String name = reactantNode.get("name").asText();
                double stoichiometry = reactantNode.get("stoichiometry").asDouble();
                double initialConcentration = rootNode.get("reactorConditions").get("feedConcentrations").get(name).asDouble();
                reactants.add(new ReactorComponent(components.get(name), stoichiometry, initialConcentration));
            }

            List<ReactorComponent> products = new ArrayList<>();
            JsonNode productsJson = rootNode.get("reaction").get("products");
            for (JsonNode productNode : productsJson) {
                String name = productNode.get("name").asText();
                double stoichiometry = productNode.get("stoichiometry").asDouble();
                double initialConcentration = rootNode.get("reactorConditions").get("feedConcentrations").get(name).asDouble();
                products.add(new ReactorComponent(components.get(name), stoichiometry, initialConcentration));
            }

            JsonNode reactionJson = rootNode.get("reaction");
            PowerRateLaw rateLaw = (PowerRateLaw) RateLawFactory.createRateLaw(reactionJson);

            Reaction reaction = new Reaction(reactants, products, rateLaw);

            System.out.println(reaction.prettyPrint());

            double reactionRate = reaction.calculateReactionRate();
            System.out.println("\nCalculated Reaction Rate: " + reactionRate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
