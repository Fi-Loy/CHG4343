package simulation.ratelaw;

import lombok.NonNull;
import simulation.reactor.ReactorState;
import util.Summarizes;

public abstract class RateLaw implements Summarizes {
    abstract public double calculateRate(@NonNull ReactorState state);
    abstract public void summarize();
}
