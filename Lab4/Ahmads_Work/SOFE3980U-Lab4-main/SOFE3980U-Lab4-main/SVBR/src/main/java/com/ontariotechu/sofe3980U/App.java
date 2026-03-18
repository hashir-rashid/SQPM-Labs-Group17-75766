package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class App {
    public static void main(String[] args) {

        String[] files = {"model_1.csv", "model_2.csv", "model_3.csv"};

        double bestBCE = Double.MAX_VALUE;
        double bestAcc = 0;
        double bestPrec = 0;
        double bestRecall = 0;
        double bestF1 = 0;

        String bestBCEModel = "";
        String bestAccModel = "";
        String bestPrecModel = "";
        String bestRecallModel = "";
        String bestF1Model = "";

        for (String filePath : files) {

            System.out.println("For " + filePath);

            FileReader filereader;
            List<String[]> allData;

            try {
                filereader = new FileReader(filePath);
                CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
                allData = csvReader.readAll();
            } catch (Exception e) {
                System.out.println("Error reading file");
                continue;
            }

            double bce = 0;
            int TP = 0, FP = 0, TN = 0, FN = 0;

            for (String[] row : allData) {

                int y_true = Integer.parseInt(row[0]);
                double y_pred = Double.parseDouble(row[1]);

                // BCE
                if (y_true == 1)
                    bce += Math.log(y_pred);
                else
                    bce += Math.log(1 - y_pred);

                // threshold = 0.5
                int y_hat = (y_pred >= 0.5) ? 1 : 0;

                // confusion matrix
                if (y_true == 1 && y_hat == 1) TP++;
                else if (y_true == 0 && y_hat == 1) FP++;
                else if (y_true == 0 && y_hat == 0) TN++;
                else if (y_true == 1 && y_hat == 0) FN++;
            }

            bce = -bce / allData.size();

            double accuracy = (double)(TP + TN) / (TP + TN + FP + FN);
            double precision = (double)TP / (TP + FP);
            double recall = (double)TP / (TP + FN);
            double f1 = 2 * (precision * recall) / (precision + recall);

            System.out.println("BCE = " + bce);
            System.out.println("Confusion Matrix:");
            System.out.println("TP=" + TP + " FP=" + FP);
            System.out.println("FN=" + FN + " TN=" + TN);

            System.out.println("Accuracy = " + accuracy);
            System.out.println("Precision = " + precision);
            System.out.println("Recall = " + recall);
            System.out.println("F1 score = " + f1);
            System.out.println();

            // best tracking
            if (bce < bestBCE) {
                bestBCE = bce;
                bestBCEModel = filePath;
            }
            if (accuracy > bestAcc) {
                bestAcc = accuracy;
                bestAccModel = filePath;
            }
            if (precision > bestPrec) {
                bestPrec = precision;
                bestPrecModel = filePath;
            }
            if (recall > bestRecall) {
                bestRecall = recall;
                bestRecallModel = filePath;
            }
            if (f1 > bestF1) {
                bestF1 = f1;
                bestF1Model = filePath;
            }
        }

        System.out.println("Best models:");
        System.out.println("According to BCE: " + bestBCEModel);
        System.out.println("According to Accuracy: " + bestAccModel);
        System.out.println("According to Precision: " + bestPrecModel);
        System.out.println("According to Recall: " + bestRecallModel);
        System.out.println("According to F1 score: " + bestF1Model);
    }
}