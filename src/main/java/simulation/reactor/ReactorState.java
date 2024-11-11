package simulation.reactor;

import com.google.common.collect.ImmutableMap;
import lombok.*;

import java.util.*;

@Getter
@EqualsAndHashCode
public class ReactorState {
    private final double temperature;
    private final double pressure;
    private final double specificPressure;
    private final double volumetricFlowRate;
    @NonNull private final Map<String, Double> molarFlows;
    @NonNull private final Map<String, Double> concentrations;

    public ReactorState(
            double temperature,
            double pressure,
            double specificPressure,
            double volumetricFlowRate,
            @NonNull Map<String, Double> molarFlows,
            @NonNull Map<String, Double> concentrations
    ) {
        if (molarFlows.isEmpty() || concentrations.isEmpty()) {
            throw new IllegalArgumentException("Molar flows and concentrations cannot be empty.");
        }
        this.temperature = temperature;
        this.pressure = pressure;
        this.specificPressure = specificPressure;
        this.volumetricFlowRate = volumetricFlowRate;
        this.molarFlows = ImmutableMap.copyOf(new HashMap<>(molarFlows));
        this.concentrations = ImmutableMap.copyOf(new HashMap<>(concentrations));
    }

    public double getTotalMolarFlow() {
      return molarFlows.values().stream().mapToDouble(Double::doubleValue).sum();
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

