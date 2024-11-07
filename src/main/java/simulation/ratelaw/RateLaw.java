package simulation.ratelaw;

import java.util.List;

import simulation.components.Species;
import simulation.reactors.ReactorState;
import util.Summarizes;

public abstract class RateLaw implements Summarizes {
    abstract public double calculateRate(ReactorState state);

    abstract public void summarize();
}
