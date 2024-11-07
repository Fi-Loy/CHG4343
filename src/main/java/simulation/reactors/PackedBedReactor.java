package simulation.reactors;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import simulation.components.Species;
import simulation.odesolver.EulerSolver;
import simulation.odesolver.ODESolver;
import simulation.odesolver.RK4Solver;
import simulation.reaction.Reaction;

import java.util.*;
@Getter
public class PackedBedReactor extends Reactor {
    private final double alpha;
    private final String mode; //"adiabatic" or "isothermal"
    private final Map<String, Integer> speciesIndexMap;

    public PackedBedReactor(Reaction reaction, List<Species> speciesList, double Wf, double alpha, String mode) {
        super(reaction, speciesList, Wf);
        this.alpha = alpha;
        this.mode = mode;
        this.reactorState = null;
        this.initialReactorState = null;

        this.speciesIndexMap = new HashMap<>();
        int index = 2;
        for (Species species : speciesList) {
            this.speciesIndexMap.put(species.getName(), index++);
        }
    }

    @Override
    public void initialize(JsonNode feedNode) {
        double R = 0.08206;
        double T = feedNode.get("T").asDouble();
        double P = feedNode.get("P").asDouble();
        double V = feedNode.get("V").asDouble();
        double p = 1;

        JsonNode compNode = feedNode.get("composition");

        Map<String, Double> molarFlows = new HashMap<>();

        double totalMolarRatio = 0.0;
        Iterator<String> fieldNames = compNode.fieldNames();
        while (fieldNames.hasNext()) {
            String name = fieldNames.next();
            double ratio = compNode.get(name).asDouble();
            double molarFlow = ratio * ((P * V) / (T * R));
            molarFlows.put(name, molarFlow);
            totalMolarRatio += ratio;
        }

        if(Double.compare(1.0, totalMolarRatio) != 0){
            throw new IllegalArgumentException("Molar Ratios should add up to 0");
        }

        Map<String, Double> concentrations = new HashMap<>();
        for (Map.Entry<String, Double> flow : molarFlows.entrySet()){
            concentrations.put(flow.getKey(), flow.getValue()/V);
        }
//        molarFlows.put("T", totalMolarFlow); //total flow

        this.reactorState = new ReactorState(T, P, 1, V, molarFlows, concentrations);
        this.initialReactorState = this.reactorState.clone();
        System.out.println(java.util.Arrays.toString(this.initialReactorState.toArray()));

    }


    @Override
    public double[] computeDerivatives(double x, double[] y) {
        double R = 0.08206;

        double temperature = y[0];
        double specificPressure = y[1];
        double initialPressure = this.initialReactorState.getPressure();
        double pressure = specificPressure * initialPressure;

        Map<String, Double> molarFlows = new HashMap<>();
        for (Species species : speciesList) {
            String speciesName = species.getName();
            int speciesIndex = speciesIndexMap.get(speciesName);
            molarFlows.put(speciesName, y[speciesIndex]);
        }

        double totalMolarFlow = molarFlows.values().stream().mapToDouble(Double::doubleValue).sum();

        double volume = (totalMolarFlow * R * temperature) / pressure;

        Map<String, Double> concentrations = new HashMap<>();
        for (Map.Entry<String, Double> flow : molarFlows.entrySet()) {
            concentrations.put(flow.getKey(), flow.getValue() / volume);
        }

        ReactorState currentState = new ReactorState(temperature, pressure, specificPressure, volume, molarFlows, concentrations);
        this.reactorState = currentState;

        double[] dydW = new double[y.length];
        Arrays.fill(dydW, 0.0);

        double reactionRate = reaction.calculateRate(currentState);
        String referenceSpecie = reaction.getReference();
        int referenceStoichiometry = reaction.getStoichiometry().get(referenceSpecie);

        for (Map.Entry<String, Integer> entry : reaction.getStoichiometry().entrySet()) {
            String species = entry.getKey();
            int stoichiometry = entry.getValue();

            int speciesIndex = speciesIndexMap.get(species);
            dydW[speciesIndex] += reactionRate * stoichiometry / referenceStoichiometry;
        }

        double initialTemperature = this.initialReactorState.getTemperature();
        double initialTotalFlow = this.initialReactorState.getTotalMolarFlow();
        dydW[1] = -(alpha / (2 * pressure)) * (totalMolarFlow / initialTotalFlow) * (temperature / initialTemperature);

        double totalCp = 0;
        for (Species species : speciesList) {
            String speciesName = species.getName();
            double speciesCp = species.getCp();

            Double molarFlow = currentState.getMolarFlows().get(speciesName);

            if (molarFlow != null) {
                totalCp += molarFlow * speciesCp;
            }
        }
        dydW[0] = (reactionRate * reaction.getHeat()) / totalCp;

        return dydW;
    }

    public void test(){
        var arr = this.initialReactorState.toArray();
        ODESolver solver = new EulerSolver();
        solver.solve(this, arr, 0, 2, 0.05);
    }



    @Override
    public void summarize() {
        StringBuilder output = new StringBuilder();

        output.append("Packed Bed Reactor Summary\n");
        output.append("--------------------------\n");
        output.append(String.format("Mode: %-10s\n", mode));
        output.append(String.format("Alpha: %-10.4f\n", alpha));
        output.append(String.format("Final Weight (Wf): %-10.2f\n\n", independentVariable));

        output.append("Reaction Details:\n");
        System.out.println(output.toString());

        reaction.summarize();

    }
}
