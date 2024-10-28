package simulation;

import lombok.Data;

@Data
public class ReactorComponent {
    private Component component;
    private double stoichiometry;
    private final double initialConcentration;
    private double concentration;

    public ReactorComponent(Component component, double stoichiometry, double initialConcentration){
        this.component = component;
        this.stoichiometry = stoichiometry;
        this.initialConcentration = initialConcentration;
        this.concentration = initialConcentration;
    }
}


