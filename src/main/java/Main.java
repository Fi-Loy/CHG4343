import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import simulation.components.Species;
import simulation.components.SpeciesFactory;
import simulation.odesolver.ODESolver;
import simulation.odesolver.ODESystem;
import simulation.odesolver.RK4Solver;
import simulation.reaction.Reaction;
import simulation.reaction.ReactionFactory;
import simulation.reactors.Reactor;
import simulation.reactors.ReactorFactory;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.numbers.NumberColumnFormatter;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

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
            double[][] results = solver.solve((ODESystem) reactor, reactor.getInitialReactorState().toArray(), 0.0, reactor.getIndependentVariable(), 0.05);
            Table resultTable = reactor.processResults(results);
            reactor.plotResults(resultTable);
            resultTable.write().csv("src/main/resources/testoutput.csv");

            System.out.println(resultTable.print());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
