package simulation.components;

import lombok.NonNull;

public record Species(
        @NonNull String name,
        double cp
) {

}
