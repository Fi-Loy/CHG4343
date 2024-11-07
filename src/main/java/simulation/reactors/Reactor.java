package simulation.reactors;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import simulation.components.Species;
import simulation.odesolver.ODESystem;
import simulation.reaction.Reaction;
import util.Summarizes;

import java.util.List;
import java.util.Map;

@Data
public abstract class Reactor implements ODESystem, Summarizes {
    protected Reaction reaction;
    protected ReactorState reactorState;
    protected ReactorState initialReactorState;
    protected double independentVariable;

    protected List<Species> speciesList;
    public Reactor(Reaction reaction, List<Species> speciesList, double independentVariable) {
        this.reaction = reaction;
        this.speciesList = speciesList;
        this.independentVariable = independentVariable;
    }

    public abstract void initialize(JsonNode feedNode);
    public abstract void summarize();

    public abstract double[] computeDerivatives(double x, double[] y);

}
