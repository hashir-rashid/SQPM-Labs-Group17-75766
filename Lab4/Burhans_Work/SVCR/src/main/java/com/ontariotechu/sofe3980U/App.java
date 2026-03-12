package com.ontariotechu.sofe3980U;

import java.io.FileReader; 
import java.util.List;
import com.opencsv.*;

public class App 
{
    public static void main( String[] args )
    {
        // Change this string to "model_2.csv" or "model_3.csv" to test the other files
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

        double mse = 0;
        double mae = 0;
        double mare = 0;
        double epsilon = 1e-10; // To avoid division by zero
        int n = allData.size();

        for (String[] row : allData) { 
            float y_true = Float.parseFloat(row[0]);
            float y_predicted = Float.parseFloat(row[1]);

            // Calculate errors for this row
            double error = y_true - y_predicted;
            
            mse += Math.pow(error, 2);
            mae += Math.abs(error);
            mare += Math.abs(error) / (Math.abs(y_true) + epsilon);
        }

        // Final Averaging
        mse = mse / n;
        mae = mae / n;
        mare = (mare / n); // For MARE as a decimal (multiply by 100 if you want percentage)

        System.out.println("for " + filePath);
        System.out.println("        MSE =" + mse);
        System.out.println("        MAE =" + mae);
        System.out.println("        MARE =" + mare);
    }
}