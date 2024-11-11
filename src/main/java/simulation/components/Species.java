package simulation.components;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

public record Species(
        @NonNull String name,
        double cp
) {

}
