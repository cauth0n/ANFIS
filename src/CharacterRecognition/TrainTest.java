package CharacterRecognition;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import NeuralNetwork.*;

public class TrainTest {

	/**
	 * Main class used solely for testing function approximation algorithms.
	 */
	public static void main(String[] args) {
		
		Parser parserLetterRecognition = null;
		Parser parserOpticalDigits = null;
		Parser parserOpticalDigitsTest = null;
		Parser parserPenDigits = null;
		Parser parserPenDigitsTest = null;
		Parser parserSemeion = null;
		
		try {
			parserLetterRecognition = new ParserLetterRecognition("data/letter-recognition.data");
			parserOpticalDigits = new ParserOpticalDigits("data/optdigits.tra");
			parserOpticalDigitsTest = new ParserOpticalDigits("data/optdigits.tes");
			parserOpticalDigits.getData();
			parserOpticalDigits.appendData(parserOpticalDigitsTest.getData());
			parserPenDigits = new ParserPenDigits("data/pendigits.tra");
			parserPenDigitsTest = new ParserPenDigits("data/pendigits.tes");
			parserPenDigits.getData();
			parserPenDigits.appendData(parserPenDigitsTest.getData());
			parserSemeion = new ParserSemeion("data/semeion.data");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		double[][][] trainData;
		double[][][] testData;
		int[] categories;
		Partitioner partitioner;
		
		int rules = 2;
		double gamma = 100;
		boolean useKMeans = true;
		
		// RBF: 10% (rate = 3.0; spread *= 0.35;)
		// RBF: 4% DIDN'T LEARN (KMeans)
		// MLP: 57% (rate = 0.001; functionScale = 0.25; {100, 50}
		// ANFIS: 55%
//		partitioner = new PartitionRotate(parserLetterRecognition.getData());
//		trainData = partitioner.getTrain();
//		testData = partitioner.getTest();
//		categories = parserLetterRecognition.getCategories();
		


		// ANFIS: 92% (rules = 2), gamma = 100, k means = true
		// ANFIS: 92% (rules = 2), gamma = 100, k means = false
		partitioner = new PartitionRotate(parserOpticalDigits.getData());
		trainData = partitioner.getTrain();
		testData = partitioner.getTest();
		categories = parserOpticalDigits.getCategories();
		


		// ANFIS: 59% (Uniform Centers)
		// ANFIS: 59% (KMeans)
//		partitioner = new PartitionRotate(parserPenDigits.getData());
//		trainData = partitioner.getTrain();
//		testData = partitioner.getTest();
//		categories = parserPenDigits.getCategories();
		
		
//		int[] network = new int[]{1000};
//		double functionScale = 0.25;
//		double rate = 0.0005;
		

		
		// RBF: 22% (rate = 3.0; spread *= 0.35;)
		// RBF: 10% DIDN"T LEARN (KMeans)
		// MLP: 13% (rate = 0.002; functionScale = 0.25; {1000}  )
		// MLP: 18% (PREPROCESSED, rate = 0.0005; functionScale = 0.25; {1000}  )
		// ANFIS: 88% (PREPROCESSED, rules = 2)
//		Preprocessor pp = new PreprocessorCompress(parserSemeion.getData());
//		partitioner = new PartitionRotate(pp.getProcessed());
//		trainData = partitioner.getTrain();
//		testData = partitioner.getTest();
//		categories = parserSemeion.getCategories();
		
		// disable
		//trainData = new double[1][2][1];
		//testData = new double[1][2][1];
		
		// create function generators
		Network nn;
		
		//nn = new MLP(functionScale, rate);  // creates a new MLP network
		//nn.setHiddenLayers(network, FunctionType.TANH);
		
		//nn = new RBF();  // creates a new RBF network
		
		nn = new ANFIS(rules, gamma, useKMeans);
		
		double[][][] train;
		double[][][] test;
		
		int part = 0;
        double error = 0.0;
        long elapsedTime = 0;
        while (partitioner.nextSet()) {
                train = partitioner.getTrain();
                test = partitioner.getTest();
                long startTime = System.nanoTime();
                nn.train(train);
                long partitionTime = System.nanoTime() - startTime;
                elapsedTime += partitionTime;
                double partitionError = nn.test(test);
                error += partitionError;
                System.out.println("Fold: " + part);
                System.out.println("Performance: " + partitionError);
                System.out.println("Run time: "  + partitionTime / 1000000000.0);
                System.out.println();
                part++;
        }
        error /= part;
        double trainTime = (elapsedTime / part);
        System.out.println("*****************************************************************");
        System.out.println("Error: " + error);
        System.out.println("Run time: " + trainTime / 1000000000.0);
        
        
		nn.printConfusionMatrix(categories);
		
	}
	
	/**
	 * A utility used to shuffle a data set's values.
	 * This is used for data sets that are generated systematically.
	 * 
	 * @param array
	 */
	static void shuffleArray(double[][][] array) {
	    Random rnd = new Random(11235);
	    for (int i = array.length - 1; i > 0; i--) {
	      int index = rnd.nextInt(i + 1);
	      // Simple swap
	      double[][] a = array[index];
	      array[index] = array[i];
	      array[i] = a;
	    }
	}
	
	/**
	 * Utility function used to print a data set to standard out.
	 * 
	 * @param set	The set to be printed to stdout.
	 */
	public static void printSet(double[][][] set) {
		for (int i = 0; i < set.length; i++) {
			System.out.print("{");
			for (int j = 0; j < set[i][0].length; j++) {
				if (j > 0)
					System.out.print(",");
				System.out.print(set[i][0][j]);
			}
			System.out.print("} -> {");
			for (int j = 0; j < set[i][1].length; j++) {
				if (j > 0)
					System.out.print(",");
				System.out.print(set[i][1][j]);
			}
			System.out.println("}");
		}
	}

}
