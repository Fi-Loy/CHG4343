package simulation.components;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public class Species {
    @NonNull private final String name;
    private final double cp;
}
