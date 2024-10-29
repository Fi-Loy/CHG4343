import util.DoubleDataFrame;

public class MainTest {

    public static void main(String[] args) {
        DoubleDataFrame df = new DoubleDataFrame(new String[]{"A", "B", "C"});

        df.appendRow(new double[]{1.0, 2.5, 3.0});
        df.appendRow(new double[]{4.0, 5.12, 6.33});
        df.appendRow(new double[]{7.25, 8.0, 9.42});

        System.out.println("DataFrame Contents:");
        df.prettyPrint();

        String fileName = "src/main/resources/output.csv";
        df.toCSV(fileName);
        System.out.println("DataFrame has been saved to " + fileName);
    }
}
