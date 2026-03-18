package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.*;

/**
 * Evaluate Single Variable Multi-Class Classification
 * Metrics: Cross-Entropy (CE), Confusion Matrix
 */
public class App
{
    public static void main( String[] args )
    {
        String filePath = "model.csv";
        FileReader filereader;
        List<String[]> allData;
        try {
            filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            allData = csvReader.readAll();
        } catch (Exception e) {
            System.out.println("Error reading the CSV file");
            return;
        }

        int numClasses = 5;
        double eps = 1e-15;  // clip to avoid log(0)
        double ceSum = 0.0;
        int n = 0;

        // confusion[predicted][actual] — rows=y^, cols=y  (matches output format)
        int[][] confusion = new int[numClasses][numClasses];

        for (String[] row : allData) {
            // y_true is 1-indexed (1..5)
            int y_true = Integer.parseInt(row[0].trim());

            float[] probs = new float[numClasses];
            for (int i = 0; i < numClasses; i++) {
                probs[i] = Float.parseFloat(row[i + 1].trim());
            }

            // Cross-Entropy: -log2(probability of the true class)
            double p = Math.max(eps, probs[y_true - 1]);
            ceSum += -Math.log(p) / Math.log(2);

            // Predicted class = argmax of probabilities (0-indexed internally)
            int predClass = 0;
            for (int i = 1; i < numClasses; i++) {
                if (probs[i] > probs[predClass]) predClass = i;
            }

            // rows = predicted, cols = actual
            confusion[predClass][y_true - 1]++;
            n++;
        }

        double ce = ceSum / n;
        System.out.printf("CE =%s%n", (float) ce);

        // Print confusion matrix
        System.out.println("Confusion matrix");
        System.out.print("\t\t");
        for (int i = 1; i <= numClasses; i++) {
            System.out.printf("y=%-5d\t", i);
        }
        System.out.println();
        for (int pred = 0; pred < numClasses; pred++) {
            System.out.printf("\ty^=%d\t", pred + 1);
            for (int actual = 0; actual < numClasses; actual++) {
                System.out.printf("%-5d\t\t", confusion[pred][actual]);
            }
            System.out.println();
        }
    }
}