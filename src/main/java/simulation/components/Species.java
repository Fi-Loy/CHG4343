package simulation.components;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class Species {
    @NonNull private String name;
    private double cp;

}
