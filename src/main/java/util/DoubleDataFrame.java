package util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoubleDataFrame {
    private final Map<String, List<Double>> columns;
    private int rowCount;

    public DoubleDataFrame(String[] columnNames) {
        this.columns = new HashMap<>();
        for (String name : columnNames) {
            columns.put(name, new ArrayList<>());
        }
        this.rowCount = 0;
    }

    // Add a new row of data
    public void appendRow(double[] row) {
        if (row.length != columns.size()) {
            throw new IllegalArgumentException("Row length must match the number of columns.");
        }

        int i = 0;
        for (String columnName : columns.keySet()) {
            columns.get(columnName).add(row[i++]);
        }
        rowCount++;
    }

    // Get a row by index
    public double[] getRow(int index) {
        if (index < 0 || index >= rowCount) {
            throw new IndexOutOfBoundsException("Invalid row index.");
        }

        double[] row = new double[columns.size()];
        int i = 0;
        for (String columnName : columns.keySet()) {
            row[i++] = columns.get(columnName).get(index);
        }
        return row;
    }

    // Get a column by name
    public List<Double> getColumn(String columnName) {
        List<Double> column = columns.get(columnName);
        if (column == null) {
            throw new IllegalArgumentException("Column not found: " + columnName);
        }
        return column;
    }

    // Pretty print the DataFrame
    public void prettyPrint() {
        Map<String, Integer> columnWidths = calculateColumnWidths();

        // Print header
        StringBuilder header = new StringBuilder("|");
        for (String columnName : columns.keySet()) {
            header.append(String.format(" %-"+ columnWidths.get(columnName) +"s |", columnName));
        }
        System.out.println(header);

        // Print separator line
        printSeparatorLine(columnWidths);

        // Print rows
        for (int i = 0; i < rowCount; i++) {
            StringBuilder row = new StringBuilder("|");
            for (String columnName : columns.keySet()) {
                String formattedValue = String.format(" %"+ columnWidths.get(columnName) +".2f ", columns.get(columnName).get(i));
                row.append(formattedValue).append("|");
            }
            System.out.println(row);
        }
    }

    // Helper method to calculate maximum width of each column
    private Map<String, Integer> calculateColumnWidths() {
        Map<String, Integer> columnWidths = new HashMap<>();
        for (String columnName : columns.keySet()) {
            int maxWidth = columnName.length(); // Start with the length of the column name
            for (Double value : columns.get(columnName)) {
                maxWidth = Math.max(maxWidth, String.format("%.2f", value).length());
            }
            columnWidths.put(columnName, maxWidth + 2); // Add padding for each side
        }
        return columnWidths;
    }

    public void toCSV(String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            // Write header
            writer.write(String.join(",", columns.keySet()) + "\n");

            // Write rows
            for (int i = 0; i < rowCount; i++) {
                List<String> row = new ArrayList<>();
                for (String columnName : columns.keySet()) {
                    row.add(String.format("%.2f", columns.get(columnName).get(i)));
                }
                writer.write(String.join(",", row) + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing to CSV: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Helper method to print separator line
    private void printSeparatorLine(Map<String, Integer> columnWidths) {
        StringBuilder separator = new StringBuilder("+");
        for (String columnName : columns.keySet()) {
            separator.append("-".repeat(columnWidths.get(columnName) + 2)).append("+");
        }
        System.out.println(separator);
    }
}

