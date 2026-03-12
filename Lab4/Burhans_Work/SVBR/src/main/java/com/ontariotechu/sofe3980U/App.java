package com.ontariotechu.sofe3980U;

import java.io.FileReader; 
import java.util.List;
import java.util.ArrayList;
import com.opencsv.*;

public class App 
{
    public static void main( String[] args )
    {
        // Change to model_2.csv or model_3.csv for subsequent tests
        String filePath = "model_3.csv"; 
        FileReader filereader;
        List<String[]> allData;
        try {
            filereader = new FileReader(filePath); 
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build(); 
            allData = csvReader.readAll();
        }
        catch(Exception e) {
            System.out.println( "Error reading the CSV file" );
            return;
        }

        double bce = 0;
        int tp = 0, fp = 0, tn = 0, fn = 0;
        double threshold = 0.5;
        int n = allData.size();

        for (String[] row : allData) { 
            int y_true = Integer.parseInt(row[0]);
            float y_predicted = Float.parseFloat(row[1]);

            // 1. Binary Cross-Entropy (BCE)
            // We add a tiny epsilon (1e-10) to avoid log(0) which is undefined
            bce += (y_true * Math.log(y_predicted + 1e-10) + (1 - y_true) * Math.log(1 - y_predicted + 1e-10));

            // 2. Confusion Matrix Logic
            int y_pred_binary = (y_predicted >= threshold) ? 1 : 0;
            if (y_true == 1 && y_pred_binary == 1) tp++;
            else if (y_true == 0 && y_pred_binary == 1) fp++;
            else if (y_true == 0 && y_pred_binary == 0) tn++;
            else if (y_true == 1 && y_pred_binary == 0) fn++;
        }

        // Final Calculations
        double finalBCE = -bce / n;
        double accuracy = (double)(tp + tn) / n;
        double precision = (double)tp / (tp + fp);
        double recall = (double)tp / (tp + fn);
        double f1 = 2 * (precision * recall) / (precision + recall);
        
        // AUC-ROC Calculation (Simplified version using the lab's procedure)
        double auc = calculateAUC(allData);

        // Output formatting to match your lab requirements
        System.out.println("for " + filePath);
        System.out.println("        BCE =" + finalBCE);
        System.out.println("        Confusion matrix");
        System.out.println("                        y=1      y=0");
        System.out.println("                y^=1    " + tp + "    " + fp);
        System.out.println("                y^=0    " + fn + "     " + tn);
        System.out.println("        Accuracy =" + accuracy);
        System.out.println("        Precision =" + precision);
        System.out.println("        Recall =" + recall);
        System.out.println("        f1 score =" + f1);
        System.out.println("        auc roc =" + auc);
    }

    // Helper method for AUC calculation
    public static double calculateAUC(List<String[]> data) {
        double auc = 0;
        double[] x = new double[101];
        double[] y = new double[101];
        int n_pos = 0;
        int n_neg = 0;

        for (String[] row : data) {
            if (Integer.parseInt(row[0]) == 1) n_pos++;
            else n_neg++;
        }

        for (int i = 0; i <= 100; i++) {
            double th = i / 100.0;
            int tp = 0, fp = 0;
            for (String[] row : data) {
                int y_true = Integer.parseInt(row[0]);
                float y_pred = Float.parseFloat(row[1]);
                if (y_true == 1 && y_pred >= th) tp++;
                if (y_true == 0 && y_pred >= th) fp++;
            }
            y[i] = (double) tp / n_pos; // TPR
            x[i] = (double) fp / n_neg; // FPR
        }

        for (int i = 1; i <= 100; i++) {
            auc += (y[i-1] + y[i]) * Math.abs(x[i-1] - x[i]) / 2.0;
        }
        return auc;
    }
}