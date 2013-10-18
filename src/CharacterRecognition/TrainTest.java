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
		Parser parserOpticalDigitsTrain = null;
		Parser parserOpticalDigitsTest = null;
		Parser parserPenDigitsTrain = null;
		Parser parserPenDigitsTest = null;
		Parser parserSemeion = null;
		
		try {
			parserLetterRecognition = new ParserLetterRecognition("data/letter-recognition.data");
			parserOpticalDigitsTrain = new ParserOpticalDigits("data/optdigits.tra");
			parserOpticalDigitsTest = new ParserOpticalDigits("data/optdigits.tes");
			parserPenDigitsTrain = new ParserPenDigits("data/pendigits.tra");
			parserPenDigitsTest = new ParserPenDigits("data/pendigits.tes");
			parserSemeion = new ParserSemeion("data/semeion.data");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		double[][][] trainData;
		double[][][] testData;
		int[] categories;
		Partitioner partitioner;
		
		// RBF:  10% (rate = 3.0; spread *= 0.35;)
		// MLP: 42% (rate = 0.002; {64}
		// MLP: 64% 163s (rate = 0.002; {1000} scaleFunctions(0.8*...) )
		// ANFIS: 55%
//		partitioner = new PartitionOnce(parserLetterRecognition.getData());
//		trainData = partitioner.getTrain();
//		testData = partitioner.getTest();
//		categories = parserLetterRecognition.getCategories();
		
		// RBF: 74% (rate = 3.0; spread *= 0.5;)
		// MLP: 91% 22s (rate = 0.002; {100}
		// ANFIS: 92% (rules = 10)
//		trainData = parserOpticalDigitsTrain.getData();
//		testData = parserOpticalDigitsTest.getData();
//		categories = parserOpticalDigitsTest.getCategories();
		
		// RBF: 66% (rate = 3.0; spread *= 0.4;)
		// MLP: 78% (rate = 0.002; {64}
		// MLP: 87% 75s (rate = 0.002; {1000} )
		// MLP: 91% 93s (rate = 0.002; {1000} scaleFunctions(0.8*...) )
		// ANFIS: 53%
//		trainData = parserPenDigitsTrain.getData();
//		testData = parserPenDigitsTest.getData();
//		categories = parserPenDigitsTest.getCategories();
		
		// RBF: 22% (rate = 3.0; spread *= 0.35;)
		// MLP: 19% (rate = 0.002; {1000} scaleFunctions(0.8*...) )
		//ANFIS: 83% (rules = 10)
//		partitioner = new PartitionOnce(parserSemeion.getData());
//		trainData = partitioner.getTrain();
//		testData = partitioner.getTest();
//		categories = parserSemeion.getCategories();
		
		Preprocessor pp = new PreprocessorCompress(parserSemeion.getData());
		double[][][] testset = pp.getProcessed();
		
		
		// disable
		//trainData = new double[1][2][1];
		//testData = new double[1][2][1];
		
		// create function generators
		Network nn;
		
		//nn = new MLP();  // creates a new MLP network
		//nn.setHiddenLayers(new int[]{100}, FunctionType.TANH);
		
		//nn = new RBF();  // creates a new RBF network
		
		nn = new ANFIS();
		
		/*
		long startTime = System.nanoTime();
		nn.train(trainData);
		nn.describe();
		long totalTime = System.nanoTime() - startTime;
		double trainTime = (double)totalTime / 1000000000.0;
		double correct = nn.test(testData);
		
		System.out.print(correct+"\t");
		System.out.print(trainTime+"\t");
		System.out.println("\n");
		nn.printConfusionMatrix(categories);
		*/
		
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
