import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import simulation.components.Species;
import simulation.components.SpeciesFactory;
import simulation.odesolver.ODESolver;
import simulation.odesolver.ODESystem;
import simulation.odesolver.RK4Solver;
import simulation.reaction.Reaction;
import simulation.reaction.ReactionFactory;
import simulation.reactor.Reactor;
import simulation.reactor.ReactorFactory;
import tech.tablesaw.api.Table;

import java.io.InputStream;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        var objectMapper = new ObjectMapper();

        try {
            InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("irreversible_powerlaw.json");
            JsonNode rootNode = objectMapper.readTree(inputStream);

            List<Species> speciesList = SpeciesFactory.createSpecies(rootNode.get("species"));
            Reaction reaction = ReactionFactory.createReaction(rootNode.get("reaction"));
            Reactor reactor = ReactorFactory.createReactor(rootNode.get("reactor"), reaction, speciesList);
            reactor.initialize(rootNode.get("feed"));

            reactor.summarize();

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
