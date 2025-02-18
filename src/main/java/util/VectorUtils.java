package util;

import lombok.NonNull;

public class VectorUtils {

    // Add the elements of 2 vectors
    public static double[] addVectors(double @NonNull [] a, double @NonNull [] b) {
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] + b[i];
        }
        return result;
    }

    // Scalar multiplication of a vector
    public static double[] multiplyVector(double @NonNull [] a, double scalar) {
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] * scalar;
        }
        return result;
    }
}
