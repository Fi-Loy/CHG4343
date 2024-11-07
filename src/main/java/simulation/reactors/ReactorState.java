package simulation.reactors;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

@Data
@AllArgsConstructor
public class ReactorState {
    private double temperature;
    private double pressure;
    private double specificPressure;
    private double volumetricFlowRate;
    private Map<String, Double> molarFlows;
    private Map<String, Double> concentrations;

    public ReactorState clone() {
        Map<String, Double> molarFlowsClone = new HashMap<>(this.molarFlows);
        Map<String, Double> concentrationsClone = new HashMap<>(this.concentrations);

        return new ReactorState(this.temperature, this.pressure, this.specificPressure, this.volumetricFlowRate, molarFlowsClone, concentrationsClone);
    }

    public double getTotalMolarFlow(){
        double total = 0;
        for (Map.Entry<String, Double> flow : molarFlows.entrySet()){
            total += flow.getValue();
        }
        return total;
    }

    public double[] toArray() {
        int size = 2 + molarFlows.size();
        double[] array = new double[size];
        int index = 0;

        array[index++] = this.temperature;
        array[index++] = this.specificPressure;

        List<String> molarFlowKeys = new ArrayList<>(molarFlows.keySet());
        Collections.sort(molarFlowKeys);

        for (String key : molarFlowKeys) {
            array[index++] = molarFlows.get(key);
        }

        return array;
    }


}

