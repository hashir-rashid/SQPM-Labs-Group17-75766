package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class App {
    public static void main(String[] args) {

        String filePath = "model.csv";

        FileReader filereader;
        List<String[]> allData;

        try {
            filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            allData = csvReader.readAll();
        } catch (Exception e) {
            System.out.println("Error reading file");
            return;
        }

        int numClasses = 5;
        int[][] confusion = new int[numClasses][numClasses];

        double ce = 0;

        for (String[] row : allData) {

            int y_true = Integer.parseInt(row[0]) - 1;

            double maxProb = -1;
            int y_pred = -1;

            // find predicted class (argmax)
            for (int i = 1; i <= numClasses; i++) {
                double prob = Double.parseDouble(row[i]);

                if (prob > maxProb) {
                    maxProb = prob;
                    y_pred = i - 1;
                }
            }

            // cross entropy
            double probTrue = Double.parseDouble(row[y_true + 1]);
            ce += Math.log(probTrue);

            // confusion matrix
            confusion[y_pred][y_true]++;
        }

        ce = -ce / allData.size();

        System.out.println("CE = " + ce);

        System.out.println("Confusion Matrix:");

        System.out.print("      ");
        for (int i = 1; i <= numClasses; i++) {
            System.out.print("y=" + i + "   ");
        }
        System.out.println();

        for (int i = 0; i < numClasses; i++) {
            System.out.print("y^=" + (i + 1) + "  ");
            for (int j = 0; j < numClasses; j++) {
                System.out.print(confusion[i][j] + "   ");
            }
            System.out.println();
        }
    }
}