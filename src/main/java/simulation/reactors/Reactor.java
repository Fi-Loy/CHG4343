package simulation.reactors;

import java.util.List;

public interface Reactor {
    void initialize();
    void runSimulation();

    String prettyPrint();

    void printResults();

    List<double[]> getResults();
}
