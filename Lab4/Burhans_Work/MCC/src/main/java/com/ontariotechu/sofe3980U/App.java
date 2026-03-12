package com.ontariotechu.sofe3980U;

import java.io.FileReader; 
import java.util.List;
import com.opencsv.*;

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
        }
        catch(Exception e) {
            System.out.println( "Error reading the CSV file" );
            return;
        }
        
        double totalCE = 0;
        // 5x5 matrix to store counts: row = predicted (y^), column = actual (y)
        int[][] confusionMatrix = new int[5][5];
        int n = allData.size();

        for (String[] row : allData) { 
            // True class is in the first column (1, 2, 3, 4, or 5)
            int y_true = Integer.parseInt(row[0]); 
            
            float[] y_predicted_probs = new float[5];
            int predictedClass = 0;
            float maxProb = -1.0f;

            // Loop through the 5 probability columns
            for(int i = 0; i < 5; i++) {
                y_predicted_probs[i] = Float.parseFloat(row[i+1]);
                
                // Find Argmax: Which class has the highest probability?
                if (y_predicted_probs[i] > maxProb) {
                    maxProb = y_predicted_probs[i];
                    predictedClass = i + 1; // Convert index 0-4 to class 1-5
                }
            }

            // 1. Cross Entropy Calculation
            // We take the log of the probability the model gave to the CORRECT class
            totalCE += -Math.log(y_predicted_probs[y_true - 1] + 1e-10);

            // 2. Confusion Matrix Update
            // confusionMatrix[row][col] where row is predicted and col is actual
            confusionMatrix[predictedClass - 1][y_true - 1]++;
        } 

        // --- Output Section ---
        System.out.printf("CE =%.7f\n", (totalCE / n));
        System.out.println("Confusion matrix");
        System.out.println("                y=1      y=2     y=3     y=4     y=5");
        
        for (int i = 0; i < 5; i++) {
            System.out.printf("        y^=%d    ", (i + 1));
            for (int j = 0; j < 5; j++) {
                System.out.print(confusionMatrix[i][j] + "\t");
            }
            System.out.println();
        }
    }
}