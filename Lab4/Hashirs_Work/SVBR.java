package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import com.opencsv.*;

/**
 * Evaluate Single Variable Binary Classification Models
 * Metrics: BCE, Confusion Matrix, Accuracy, Precision, Recall, F1 Score, AUC-ROC
 */
public class App
{
    // ---------------------------------------------------------------
    // Reads a CSV file and returns two parallel arrays:
    //   result[0] = int[]   of y_true  (0 or 1)
    //   result[1] = float[] of y_predicted (probability in [0,1])
    // ---------------------------------------------------------------
    private static Object[] readCSV(String filePath) {
        try {
            FileReader filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            List<String[]> allData = csvReader.readAll();

            int n = allData.size();
            int[]   y_true      = new int[n];
            float[] y_predicted = new float[n];

            for (int i = 0; i < n; i++) {
                y_true[i]      = Integer.parseInt(allData.get(i)[0].trim());
                y_predicted[i] = Float.parseFloat(allData.get(i)[1].trim());
            }
            return new Object[]{y_true, y_predicted};

        } catch (Exception e) {
            System.out.println("Error reading the CSV file: " + filePath);
            return null;
        }
    }

    // ---------------------------------------------------------------
    // Binary Cross-Entropy Loss
    //   BCE = -1/N * sum( y*log(p) + (1-y)*log(1-p) )
    // ---------------------------------------------------------------
    private static double calcBCE(int[] y_true, float[] y_predicted) {
        double eps = 1e-15;   // clip to avoid log(0)
        double sum = 0.0;
        int n = y_true.length;
        for (int i = 0; i < n; i++) {
            double p = Math.max(eps, Math.min(1 - eps, y_predicted[i]));
            sum += y_true[i] * Math.log(p) + (1 - y_true[i]) * Math.log(1 - p);
        }
        return -sum / n;
    }

    // ---------------------------------------------------------------
    // Confusion matrix at threshold = 0.5
    //   returns int[4] = { TP, FP, FN, TN }
    // ---------------------------------------------------------------
    private static int[] calcConfusionMatrix(int[] y_true, float[] y_predicted) {
        int TP = 0, FP = 0, FN = 0, TN = 0;
        for (int i = 0; i < y_true.length; i++) {
            int pred = (y_predicted[i] >= 0.5f) ? 1 : 0;
            if      (pred == 1 && y_true[i] == 1) TP++;
            else if (pred == 1 && y_true[i] == 0) FP++;
            else if (pred == 0 && y_true[i] == 1) FN++;
            else                                   TN++;
        }
        return new int[]{TP, FP, FN, TN};
    }

    // ---------------------------------------------------------------
    // AUC-ROC via the trapezoidal rule over all unique thresholds
    // ---------------------------------------------------------------
    private static double calcAUCROC(int[] y_true, float[] y_predicted) {
        int n = y_true.length;

        // Collect all unique thresholds (plus sentinel 0 and 1)
        List<Float> thresholds = new ArrayList<>();
        thresholds.add(0.0f);
        thresholds.add(1.0f);
        for (float v : y_predicted) thresholds.add(v);
        thresholds.sort(null);

        int totalPos = 0, totalNeg = 0;
        for (int v : y_true) { if (v == 1) totalPos++; else totalNeg++; }
        if (totalPos == 0 || totalNeg == 0) return 0.0;

        // Build (FPR, TPR) points
        List<double[]> points = new ArrayList<>();
        for (float thresh : thresholds) {
            int TP = 0, FP = 0;
            for (int i = 0; i < n; i++) {
                if (y_predicted[i] >= thresh) {
                    if (y_true[i] == 1) TP++; else FP++;
                }
            }
            double fpr = (double) FP / totalNeg;
            double tpr = (double) TP / totalPos;
            points.add(new double[]{fpr, tpr});
        }

        // Sort by FPR ascending, then TPR ascending
        points.sort((a, b) -> {
            if (a[0] != b[0]) return Double.compare(a[0], b[0]);
            return Double.compare(a[1], b[1]);
        });

        // Trapezoidal integration
        double auc = 0.0;
        for (int i = 1; i < points.size(); i++) {
            double dx = points.get(i)[0] - points.get(i - 1)[0];
            double avgY = (points.get(i)[1] + points.get(i - 1)[1]) / 2.0;
            auc += dx * avgY;
        }
        return Math.abs(auc);
    }

    // ---------------------------------------------------------------
    // Evaluate and print one model; returns a double[6] of all metrics
    //   [0]=BCE  [1]=Accuracy  [2]=Precision  [3]=Recall  [4]=F1  [5]=AUC
    // ---------------------------------------------------------------
    private static double[] evaluateModel(String filePath) {
        System.out.println("for " + filePath);

        Object[] data = readCSV(filePath);
        if (data == null) return null;

        int[]   y_true      = (int[])   data[0];
        float[] y_predicted = (float[]) data[1];

        // BCE
        double bce = calcBCE(y_true, y_predicted);
        System.out.printf("\tBCE =%s%n", (float) bce);

        // Confusion matrix
        int[] cm = calcConfusionMatrix(y_true, y_predicted);
        int TP = cm[0], FP = cm[1], FN = cm[2], TN = cm[3];
        System.out.println("\tConfusion matrix");
        System.out.println("\t\t\t\ty=1      y=0");
        System.out.printf ("\t\t\ty^=1\t%d\t%d%n", TP, FP);
        System.out.printf ("\t\t\ty^=0\t%d\t%d%n", FN, TN);

        // Derived metrics
        double accuracy  = (double)(TP + TN) / (TP + FP + FN + TN);
        double precision = (TP + FP) == 0 ? 0.0 : (double) TP / (TP + FP);
        double recall    = (TP + FN) == 0 ? 0.0 : (double) TP / (TP + FN);
        double f1        = (precision + recall) == 0 ? 0.0
                           : 2 * precision * recall / (precision + recall);
        double auc       = calcAUCROC(y_true, y_predicted);

        System.out.printf("\tAccuracy =%s%n",  (float) accuracy);
        System.out.printf("\tPrecision =%s%n", (float) precision);
        System.out.printf("\tRecall =%s%n",    (float) recall);
        System.out.printf("\tf1 score =%s%n",  (float) f1);
        System.out.printf("\tauc roc =%s%n",   (float) auc);

        return new double[]{bce, accuracy, precision, recall, f1, auc};
    }

    // ---------------------------------------------------------------
    // Main
    // ---------------------------------------------------------------
    public static void main(String[] args)
    {
        String[] files   = {"model_1.csv", "model_2.csv", "model_3.csv"};
        String[] metrics = {"BCE", "Accuracy", "Precision", "Recall", "F1 score", "AUC ROC"};
        // For BCE lower is better; for all others higher is better
        boolean[] lowerIsBetter = {true, false, false, false, false, false};

        double[][] results = new double[files.length][];
        for (int i = 0; i < files.length; i++) {
            results[i] = evaluateModel(files[i]);
        }

        // Per-metric best model
        int numMetrics = metrics.length;
        for (int m = 0; m < numMetrics; m++) {
            int bestIdx = 0;
            for (int i = 1; i < files.length; i++) {
                if (results[i] == null) continue;
                boolean better = lowerIsBetter[m]
                    ? results[i][m] < results[bestIdx][m]
                    : results[i][m] > results[bestIdx][m];
                if (better) bestIdx = i;
            }
            System.out.println("According to " + metrics[m] + ", The best model is " + files[bestIdx]);
        }
    }
}