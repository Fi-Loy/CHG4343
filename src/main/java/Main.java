import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import simulation.reaction.Reaction;
import simulation.reaction.ReactionFactory;
import simulation.components.ReactorComponent;
import simulation.reactors.PackedBedReactor;
import simulation.reactors.Reactor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("irreversible_powerlaw.json");
            JsonNode rootNode = objectMapper.readTree(inputStream);

            // Create reaction using ReactionFactory
            Reaction reaction = ReactionFactory.createReaction(rootNode);

            // Get reactor conditions
// Get reactor conditions
            JsonNode reactorConditions = rootNode.get("reactorConditions");
            double W0 = reactorConditions.get("W0").asDouble();
            double Wf = reactorConditions.get("Wf").asDouble();
            double h = reactorConditions.get("h").asDouble();
            double T0 = reactorConditions.get("temperature").asDouble();
            double P0 = reactorConditions.get("pressure").asDouble();
            double deltaHr = reactorConditions.get("deltaHr").asDouble();
            double Cp = reactorConditions.get("Cp").asDouble();
            double alpha = reactorConditions.get("alpha").asDouble();
            double v0 = reactorConditions.get("v0").asDouble(); // Volumetric flow rate

// Calculate total molar flow rate F_T0
            double R = 8.314; // J/(mol·K)
            double F_T0 = (P0 * v0) / (R * T0);

// Collect all components
            List<ReactorComponent> components = new ArrayList<>();
            components.addAll(reaction.getReactants());
            components.addAll(reaction.getProducts());

// Create reactor
            Reactor reactor = new PackedBedReactor(
                    reaction,
                    components,
                    W0,
                    Wf,
                    h,
                    T0,
                    P0,
                    F_T0,
                    v0,
                    deltaHr,
                    Cp,
                    alpha
            );
            System.out.println(reactor.prettyPrint());


            // Initialize and run simulation
            reactor.initialize();
            reactor.runSimulation();

            // Print results
            reactor.printResults();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
