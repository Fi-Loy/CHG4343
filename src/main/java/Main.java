import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import simulation.species.Species;
import simulation.species.SpeciesFactory;
import simulation.odesolver.ODESolver;
import simulation.odesolver.RK4Solver;
import simulation.reaction.Reaction;
import simulation.reaction.ReactionFactory;
import simulation.reactor.Reactor;
import simulation.reactor.ReactorFactory;
import tech.tablesaw.api.Table;

import java.io.InputStream;

public class Main {
    public static void main(String[] args) {
        var objectMapper = new ObjectMapper();

        try {
            // Read input
            InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("irreversible_powerlaw.json");
            JsonNode rootNode = objectMapper.readTree(inputStream);

            // Create necessary objects
            ImmutableList<Species> speciesList = SpeciesFactory.createSpecies(rootNode.get("species"));
            Reaction reaction = ReactionFactory.createReaction(rootNode.get("reaction"));
            Reactor reactor = ReactorFactory.createReactor(rootNode.get("reactor"), reaction, speciesList);
            reactor.initialize(rootNode.get("feed"));

            reactor.summarize();

            // Solve and process
            ODESolver solver = new RK4Solver();
            double[][] results = solver.solve(reactor, reactor.getInitialReactorState().toArray(), 0.0, reactor.getIndependentVariable(), 0.05);
            Table resultTable = reactor.processResults(results);
            reactor.plotResults(resultTable);
            resultTable.write().csv("src/main/resources/testoutput.csv");

            System.out.println(resultTable.print());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
