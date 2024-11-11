package simulation.ratelaw;

import lombok.NonNull;
import simulation.reactor.ReactorState;
import util.Summarizable;

public interface RateLaw extends Summarizable {
    double calculateRate(@NonNull ReactorState state);
}
