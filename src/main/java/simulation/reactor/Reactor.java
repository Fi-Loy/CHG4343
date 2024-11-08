package simulation.reactor;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.NonNull;
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
    @NonNull protected Reaction reaction;
    protected ReactorState initialReactorState;
    protected double independentVariable;
    @NonNull protected ReactorModes mode;
    @NonNull protected List<Species> speciesList;
    public Reactor(@NonNull Reaction reaction, @NonNull List<Species> speciesList, double independentVariable, @NonNull String mode) {
        this.reaction = reaction;
        this.speciesList = speciesList;
        this.independentVariable = independentVariable;

        switch (mode.toLowerCase()) {
            case "adiabatic" -> this.mode = ReactorModes.ADIABATIC;
            case "isothermal" -> this.mode = ReactorModes.ISOTHERMAL;
            default -> throw new IllegalArgumentException("reactor mode should be `adiabtic` or `isothermal`");
        }
    }

    public abstract void initialize(@NonNull JsonNode feedNode);
    public abstract void summarize();

    public abstract double[] computeDerivatives(double x, double @NonNull [] y);

    public abstract Table processResults(double @NonNull [] @NonNull [] results);

    public abstract void plotResults(@NonNull Table table);
}
