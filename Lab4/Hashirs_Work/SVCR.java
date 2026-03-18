package com.ontariotechu.sofe3980U;


import java.io.FileReader; 
import java.util.List;
import java.lang.Math;
import com.opencsv.*;

/**
 * Evaluate Single Variable Continuous Regression
 *
 */
public class App 
{
	public static void main( String[] args )
    {
		double[] mses = {0, 0, 0};
		double[] maes = new double[3];
		double[] mares = new double[3];
		double epsilon = 0.00000001;
		int i, n;

		String filePath="model_1.csv";
		FileReader filereader;
		List<String[]> allData;
		try{
			filereader = new FileReader(filePath); 
			CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build(); 
			allData = csvReader.readAll();
		}
		catch(Exception e){
			System.out.println( "Error reading the CSV file" );
			return;
		}
		
		i = 0;
		n = allData.size();

		for (String[] row : allData) { 
			float y_true=Float.parseFloat(row[0]);
			float y_predicted=Float.parseFloat(row[1]);
			
			mses[0] += Math.pow(y_true - y_predicted, 2);
			maes[0] += Math.abs(y_true - y_predicted);
			mares[0] += (Math.abs(y_true - y_predicted)) / (Math.abs(y_true) + epsilon);
			i++;
		}

		mses[0] /= n;
		maes[0] /= n;
		mares[0] /= n;

		int mse_min = 1;
		int mae_min = 1;
		int mare_min = 1;

		System.out.println("The MSE for model 1 is " + mses[0]);
		System.out.println("The MAE for model 1 is " + maes[0]);
		System.out.println("The MARE for model 1 is " + mares[0]);

		System.out.println();

		filePath="model_2.csv";
		try{
			filereader = new FileReader(filePath); 
			CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build(); 
			allData = csvReader.readAll();
		}
		catch(Exception e){
			System.out.println( "Error reading the CSV file" );
			return;
		}
		
		i = 0;
		n = allData.size();

		for (String[] row : allData) { 
			float y_true=Float.parseFloat(row[0]);
			float y_predicted=Float.parseFloat(row[1]);
			
			mses[1] += Math.pow(y_true - y_predicted, 2);
			maes[1] += Math.abs(y_true - y_predicted);
			mares[1] += (Math.abs(y_true - y_predicted)) / (Math.abs(y_true) + epsilon);
			i++;
		}

		mses[1] /= n;
		maes[1] /= n;
		mares[1] /= n;

		if (mses[1] < mses[0])
			mse_min = 2;
		if (maes[1] < maes[0])
			mae_min = 2;
		if (mares[1] < mares[0])
			mare_min = 2;

		System.out.println("The MSE for model 2 is " + mses[1]);
		System.out.println("The MAE for model 2 is " + maes[1]);
		System.out.println("The MARE for model 2 is " + mares[1]);

		System.out.println();

		filePath="model_3.csv";
		try{
			filereader = new FileReader(filePath); 
			CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build(); 
			allData = csvReader.readAll();
		}
		catch(Exception e){
			System.out.println( "Error reading the CSV file" );
			return;
		}
		
		i = 0;
		n = allData.size();

		for (String[] row : allData) { 
			float y_true=Float.parseFloat(row[0]);
			float y_predicted=Float.parseFloat(row[1]);
			
			mses[2] += Math.pow(y_true - y_predicted, 2);
			maes[2] += Math.abs(y_true - y_predicted);
			mares[2] += (Math.abs(y_true - y_predicted)) / (Math.abs(y_true) + epsilon);
			i++;
		}

		mses[2] /= n;
		maes[2] /= n;
		mares[2] /= n;

		if (mses[2] < mses[1] || mses[2] < mses[0])
			mse_min = 3;
		if (maes[2] < maes[1] || maes[2] < maes[0])
			mae_min = 3;
		if (mares[2] < mares[1] || mares[2] < mares[0])
			mare_min = 3;

		System.out.println("The MSE for model 3 is " + mses[2]);
		System.out.println("The MAE for model 3 is " + maes[2]);
		System.out.println("The MARE for model 3 is " + mares[2]);

		System.out.println();

		System.out.println("According to MSE, The best model is model_" + mse_min + ".csv");
		System.out.println("According to MAE, The best model is model_" + mae_min + ".csv");
		System.out.println("According to MARE, The best model is model_" + mare_min + ".csv");
    }
}
