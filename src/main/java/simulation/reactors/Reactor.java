package simulation.reactors;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import simulation.components.Species;
import simulation.odesolver.ODESystem;
import simulation.reaction.Reaction;
import tech.tablesaw.api.Table;
import util.Summarizes;

import java.util.List;

enum ReactorModes {
    ADIABATIC,
    ISOTHERMAL,
}

@Data
public abstract class Reactor implements ODESystem, Summarizes {
    protected Reaction reaction;
    protected ReactorState initialReactorState;
    protected double independentVariable;
    protected ReactorModes mode;
    protected List<Species> speciesList;
    public Reactor(Reaction reaction, List<Species> speciesList, double independentVariable, String mode) {
        this.reaction = reaction;
        this.speciesList = speciesList;
        this.independentVariable = independentVariable;

        switch (mode.toLowerCase()) {
            case "adiabatic" -> this.mode = ReactorModes.ADIABATIC;
            case "isothermal" -> this.mode = ReactorModes.ISOTHERMAL;
            default -> throw new IllegalArgumentException("reactor mode should be `adiabtic` or `isothermal`");
        }
    }

    public abstract void initialize(JsonNode feedNode);
    public abstract void summarize();

    public abstract double[] computeDerivatives(double x, double[] y);

    public abstract Table processResults(double[][] results);

    public abstract void plotResults(Table table);
}
