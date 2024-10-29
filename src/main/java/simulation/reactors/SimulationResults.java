package simulation.reactors;

import java.util.List;

public class SimulationResults {

    private final List<double[]> data;

    public SimulationResults(List<double[]> data) {
        this.data = data;
    }

    public List<double[]> getData() {
        return data;
    }
}
