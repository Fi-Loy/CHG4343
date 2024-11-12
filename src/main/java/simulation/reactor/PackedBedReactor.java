package simulation.reactor;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import tech.tablesaw.plotly.Plot;
import lombok.Getter;
import simulation.components.Species;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import simulation.reaction.Reaction;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.traces.ScatterTrace;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@EqualsAndHashCode(callSuper = true)
public class PackedBedReactor extends Reactor {
    private final double alpha;
    @NonNull private final ImmutableMap<String, Integer> speciesIndexMap;

    public PackedBedReactor(
            Reaction reaction,
            List<Species> speciesList,
            double Wf,
            double alpha,
            String mode
    ) {
        super(reaction, speciesList, Wf, mode);
        this.alpha = alpha;
        this.initialReactorState = null;
        this.speciesIndexMap = ImmutableMap.copyOf(createSpeciesIndexMap(speciesList));
    }

    private Map<String, Integer> createSpeciesIndexMap(List<Species> speciesList) {
        Map<String, Integer> map = new HashMap<>();
        int index = 2;
        for (Species species : speciesList) {
            map.put(species.name(), index++);
        }
        return map;
    }

    @Override
    public void initialize(@NonNull JsonNode feedNode) {
        this.initialReactorState = createInitialReactorState(feedNode);
    }

    private ReactorState createInitialReactorState(JsonNode feedNode) {
        double R = 0.08206;
        double T = feedNode.get("T").asDouble();
        double P = feedNode.get("P").asDouble();
        double V = feedNode.get("V").asDouble();

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

        if (Double.compare(1.0, totalMolarRatio) != 0) {
            throw new IllegalArgumentException("Molar Ratios should add up to 1");
        }

        Map<String, Double> concentrations = molarFlows.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() / V));

        return new ReactorState(
                T, P, 1, V,
                ImmutableMap.copyOf(molarFlows),
                ImmutableMap.copyOf(concentrations)
        );
    }

    @Override
    public double[] computeDerivatives(double x, double @NonNull [] y) {
        if (this.initialReactorState == null) {
            throw new NullPointerException("Reactor not initialized before attempting to solve.");
        }

        double R = 0.08206;
        double temperature = y[0];
        double specificPressure = y[1];
        double initialPressure = this.initialReactorState.pressure();
        double pressure = specificPressure * initialPressure;

        Map<String, Double> molarFlows = speciesList.stream()
                .collect(Collectors.toMap(Species::name, species -> y[speciesIndexMap.get(species.name())]));
        molarFlows = ImmutableMap.copyOf(molarFlows);

        double totalMolarFlow = molarFlows.values().stream().mapToDouble(Double::doubleValue).sum();
        double volume = (totalMolarFlow * R * temperature) / pressure;

        Map<String, Double> concentrations = molarFlows.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() / volume));
        concentrations = ImmutableMap.copyOf(concentrations);

        ReactorState currentState = new ReactorState(
                temperature, pressure, specificPressure, volume, molarFlows, concentrations
        );

        double[] dydW = new double[y.length];
        Arrays.fill(dydW, 0.0);

        double reactionRate = reaction.calculateRate(currentState);
        String referenceSpecie = reaction.reference();
        int referenceStoichiometry = reaction.stoichiometry().get(referenceSpecie);

        for (Map.Entry<String, Integer> entry : reaction.stoichiometry().entrySet()) {
            String species = entry.getKey();
            int stoichiometry = entry.getValue();
            int speciesIndex = speciesIndexMap.get(species);
            dydW[speciesIndex] += reactionRate * stoichiometry / referenceStoichiometry;
        }

        double initialTemperature = this.initialReactorState.temperature();
        double initialTotalFlow = this.initialReactorState.getTotalMolarFlow();
        dydW[1] = -(alpha / (2 * pressure)) * (totalMolarFlow / initialTotalFlow) * (temperature / initialTemperature);

        double totalCp = 0;
        for (Species species : speciesList) {
            String speciesName = species.name();
            double speciesCp = species.cp();
            Double molarFlow = currentState.molarFlows().get(speciesName);

            if (molarFlow != null) {
                totalCp += molarFlow * speciesCp;
            }
        }

        double dTdW = 0;
        if (mode == ReactorModes.ADIABATIC) {
            dTdW = (reactionRate * reaction.heat()) / totalCp;
        }
        dydW[0] = dTdW;

        return dydW;
    }

    @Override
    public Table processResults(double @NonNull [] @NonNull [] results) {
        double R = 0.08206;
        double initialPressure = this.initialReactorState.pressure();

        List<String> columnNames = new ArrayList<>(List.of("Catalyst Weight", "T", "sp", "P", "V"));
        List<String> speciesColumns = speciesList.stream()
                .flatMap(species -> Stream.of("F_" + species.name(), "C_" + species.name()))
                .toList();
        columnNames = List.copyOf(Stream.concat(columnNames.stream(), speciesColumns.stream()).toList());


        Table resultTable = Table.create("Packed Bed Reactor Results");
        for (String columnName : columnNames) {
            resultTable.addColumns(DoubleColumn.create(columnName));
        }

        int numSteps = results.length;
        double deltaW = this.independentVariable / (numSteps - 1);
        double catalystWeight = 0;

        for (double[] row : results) {
            List<Double> rowData = new ArrayList<>();
            rowData.add(catalystWeight);

            double temperature = row[0];
            double specificPressure = row[1];
            rowData.add(temperature);
            rowData.add(specificPressure);

            double pressure = specificPressure * initialPressure;
            rowData.add(pressure);

            double totalMolarFlow = Arrays.stream(row, 2, row.length).sum();
            double volume = (totalMolarFlow * R * temperature) / pressure;
            rowData.add(volume);

            for (int i = 2; i < row.length; i++) {
                rowData.add(row[i]);
            }
            for (int i = 2; i < row.length; i++) {
                double concentration = row[i] / volume;
                rowData.add(concentration);
            }

            for (int i = 0; i < rowData.size(); i++) {
                resultTable.doubleColumn(i).append(rowData.get(i));
            }

            catalystWeight += deltaW;
        }

        return resultTable;
    }
    @Override
    public void plotResults(@NonNull Table table) {
        var x = table.nCol("Catalyst Weight");

        var concentrationColumn = Table.create("Concentration Table");
        for(var colName : table.columnNames()){
            if(!colName.startsWith("C_")){
                continue;
            }
            concentrationColumn.addColumns(table.nCol(colName));
        }

        var concentrationTraces = new ScatterTrace[concentrationColumn.columnCount()];
        for(int i = 0;  i < concentrationColumn.columnCount(); i++){
            concentrationTraces[i] = (
                    ScatterTrace
                            .builder(x, concentrationColumn.nCol(i))
                            .mode(ScatterTrace.Mode.LINE)
                            .name(concentrationColumn.nCol(i).name())
                            .build()
            );
        }

        Layout concentrationLayout = (
                Layout
                        .builder()
                        .title("Specie Concentrations Versus Catalyst Weight in Packed Bed Reactor")
                        .build()
                );
        Plot.show(new Figure(concentrationLayout, concentrationTraces));

        var flowColumn = Table.create("Concentration Table");
        for(var colName : table.columnNames()){
            if(!colName.startsWith("F_")){
                continue;
            }
            flowColumn.addColumns(table.nCol(colName));
        }

        var traces = new ScatterTrace[flowColumn.columnCount()];
        for(int i = 0;  i < flowColumn.columnCount(); i++){
            traces[i] = (ScatterTrace.builder(x, flowColumn.nCol(i)).mode(ScatterTrace.Mode.LINE).name(flowColumn.nCol(i).name()).build());
        }

        Layout flowLayout =
                Layout.builder().title("Specie Flows Versus Catalyst Weight in Packed Bed Reactor").build();
        Plot.show(new Figure(flowLayout, traces));

        var y = table.nCol("T");
        var y2 = table.nCol("P");

        Layout layout =
                Layout.builder()
                        .title("Temperature and Pressure versus Catalyst Weight in a Packed Bed Reactor")
                        .xAxis(Axis.builder().title("Catalyst Weight").build())
                        .yAxis(Axis.builder().title("Temperature [K]").build())
                        .yAxis2(
                                Axis.builder()
                                        .title("Pressure [atm]")
                                        .side(Axis.Side.right)
                                        .overlaying(ScatterTrace.YAxis.Y)
                                        .build())
                        .build();

        ScatterTrace trace =
                ScatterTrace.builder(x, y)
                        .name("Temperature [K]")
                        .marker(Marker.builder().opacity(.7).color("#9A0EEA").build())
                        .mode(ScatterTrace.Mode.LINE)
                        .build();

        ScatterTrace trace2 =
                ScatterTrace.builder(x, y2)
                        .yAxis(ScatterTrace.YAxis.Y2)
                        .name("Pressure [atm]")
                        .marker(Marker.builder().opacity(.7).color("#008080").build())
                        .mode(ScatterTrace.Mode.LINE)
                        .build();

        Figure figure = new Figure(layout, trace2, trace);
        Plot.show(figure);
    }


    @Override
    public void summarize() {
        StringBuilder output = new StringBuilder();

        output.append("Packed Bed Reactor Summary\n");
        output.append("--------------------------\n");
        output.append(String.format("Mode: %-10s\n", this.mode));
        output.append(String.format("Alpha: %-10.4f\n", alpha));
        output.append(String.format("Final Weight (Wf): %-10.2f\n", independentVariable));

        System.out.println(output.toString());

        reaction.summarize();

    }
}
