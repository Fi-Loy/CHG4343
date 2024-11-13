package simulation.reactor;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import simulation.components.Species;
import simulation.odesolver.ODESystem;
import simulation.reaction.Reaction;
import tech.tablesaw.api.Table;
import util.Analyzable;
import util.Summarizable;

import java.util.List;

enum ReactorModes {
    ADIABATIC,
    ISOTHERMAL,
}

@Data
@EqualsAndHashCode
public abstract class Reactor implements ODESystem, Summarizable, Analyzable {
    @NonNull protected final Reaction reaction;
    protected ReactorState initialReactorState;
    protected final double independentVariable;
    @NonNull final protected ReactorModes mode;
    @NonNull protected final List<Species> speciesList;

    public Reactor(
            @NonNull Reaction reaction,
            @NonNull List<Species> speciesList,
            double independentVariable,
            @NonNull String mode
    ) {
        this.reaction = reaction;
        this.speciesList = speciesList;
        this.independentVariable = independentVariable;

        switch (mode.toLowerCase()) {
            case "adiabatic" -> this.mode = ReactorModes.ADIABATIC;
            case "isothermal" -> this.mode = ReactorModes.ISOTHERMAL;
            default -> throw new IllegalArgumentException("reactor mode should be `adiabatic` or `isothermal`");
        }
    }

    public abstract void initialize(@NonNull JsonNode feedNode);
    public abstract void summarize();

    public abstract double[] computeDerivatives(double x, double @NonNull [] y);

    public abstract Table processResults(double @NonNull [] @NonNull [] results);

    public abstract void plotResults(@NonNull Table table);
}
