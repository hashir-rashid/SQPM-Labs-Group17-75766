package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

/**
 * Evaluate Single Variable Continuous Regression
 */
public class App
{
    public static void main(String[] args)
    {
        String[] files = {"model_1.csv", "model_2.csv", "model_3.csv"};

        double bestMSE = Double.MAX_VALUE;
        double bestMAE = Double.MAX_VALUE;
        double bestMARE = Double.MAX_VALUE;

        String bestMSEModel = "";
        String bestMAEModel = "";
        String bestMAREModel = "";

        for (String filePath : files)
        {
            System.out.println("For " + filePath);

            FileReader filereader;
            List<String[]> allData;

            try
            {
                filereader = new FileReader(filePath);
                CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
                allData = csvReader.readAll();
            }
            catch (Exception e)
            {
                System.out.println("Error reading the CSV file");
                continue;
            }

            double mse = 0;
            double mae = 0;
            double mare = 0;
            int n = 0;
            int count = 0;

            for (String[] row : allData)
            {
                float y_true = Float.parseFloat(row[0]);
                float y_predicted = Float.parseFloat(row[1]);

                double error = y_true - y_predicted;

                mse += error * error;
                mae += Math.abs(error);
                mare += Math.abs(error / y_true);

                n++;

                if (count < 10)
                {
                    System.out.println(y_true + "\t" + y_predicted);
                }
                count++;
            }

            mse = mse / n;
            mae = mae / n;
            mare = (mare / n) * 100;

            System.out.println("MSE = " + mse);
            System.out.println("MAE = " + mae);
            System.out.println("MARE = " + mare);
            System.out.println();

            if (mse < bestMSE)
            {
                bestMSE = mse;
                bestMSEModel = filePath;
            }

            if (mae < bestMAE)
            {
                bestMAE = mae;
                bestMAEModel = filePath;
            }

            if (mare < bestMARE)
            {
                bestMARE = mare;
                bestMAREModel = filePath;
            }
        }

        System.out.println("According to MSE, the best model is " + bestMSEModel);
        System.out.println("According to MAE, the best model is " + bestMAEModel);
        System.out.println("According to MARE, the best model is " + bestMAREModel);
    }
}