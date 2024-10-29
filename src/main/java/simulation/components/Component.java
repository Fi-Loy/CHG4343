package simulation.components;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Component {
    @NonNull private String name;           // Name of the component
    @NonNull private double cp;             // Specific heat capacity (J/mol K)
    @NonNull private double h;              // Enthalpy (J/mol)
    @NonNull private double molecularWeight; // Molecular weight (g/mol)
}