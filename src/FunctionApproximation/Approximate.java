package FunctionApproximation;
import java.util.Random;
import NeuralNetwork.*;

public class Approximate {

	/**
	 * Main class used solely for testing function approximation algorithms.
	 */
	public static void main(String[] args) {
		
		// create function generators
		Network nn;
		Function polyFunc = new RosenbrockFunction();
		Function linearFunc = new LinearFunction();
		Function constFunc = new ConstantFunction();
		Function func = polyFunc;
		
		// some default values for all datasets
		int size = 10000;
		double max = 1.0;
		
		// a set covering 2 - 6 inputs
		double[][][][] setDimensionSizes = new double[][][][]{
			func.getRandomSet(new double[]{max,max}, size),
			func.getRandomSet(new double[]{max,max,max}, size),
			func.getRandomSet(new double[]{max,max,max,max}, size),
			func.getRandomSet(new double[]{max,max,max,max,max}, size),
			func.getRandomSet(new double[]{max,max,max,max,max,max}, size)
		};
		
		// a set covering a variety of input ranges
		double[][][][] setInputSizes = new double[][][][]{
			func.getRandomSet(new double[]{0.1,0.1}, size),
			func.getRandomSet(new double[]{1.0,1.0}, size),
			func.getRandomSet(new double[]{10.0,10.0}, size),
			func.getRandomSet(new double[]{100.0,100.0}, size),
			func.getRandomSet(new double[]{1000.0,1000.0}, size)
		};
		
		// a set covering a variety of example sizes
		double[][][][] setExampleSizes = new double[][][][]{
			func.getRandomSet(new double[]{max,max}, 100),
			func.getRandomSet(new double[]{max,max}, 1000),
			func.getRandomSet(new double[]{max,max}, 10000),
			func.getRandomSet(new double[]{max,max}, 100000),
			func.getRandomSet(new double[]{max,max}, 1000000)
		};
		
		// a single set used mostly for parameter tuning
		double[][][][] setSingle = new double[][][][]{
				func.getRandomSet(new double[]{1.0,1.0}, 2000)
		};
		
		// choose the desired dataset
		double[][][][] sets = setInputSizes;
		
		nn = new MLP();  // creates a new MLP network
		nn.setHiddenLayers(new int[]{15}, FunctionType.TANH);
		
		nn = new RBF();  // creates a new RBF network
		
		// train and test an array of datasets
		double[][][] set, train, test;
		for (int i = 0; i < sets.length; i++) {
			
			set = sets[i];
			
			Partitioner partitionRotate = new PartitionRotate(set);
		
			// train over partitions
			int part = 0;
			double error = 0.0;
			long elapsedTime = 0;
			while (partitionRotate.nextSet()) {
				train = partitionRotate.getTrain();
				test = partitionRotate.getTest();
				long startTime = System.nanoTime();
				nn.train(train);
				long partitionTime = System.nanoTime() - startTime;
				elapsedTime += partitionTime;
				error += nn.test(test);
				part++;
				break; // TODO: remove
			}
			error /= part;
			double trainTime = (double)elapsedTime / ((double)part * 1000000000.0);
			double normalError = (Math.sqrt(error)/nn.getAverage(set));
			double normalTrainTime = 100000.0 * trainTime / (double)set.length;
			
			//nn.describe();
			System.out.print(error+"\t");
			System.out.print(normalError+"\t");
			System.out.print(trainTime+"\t");
			System.out.print(normalTrainTime+"\t");
			System.out.println("");
			
		}
		
		
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
			System.out.print("F(");
			for (int j = 0; j < set[i][0].length; j++) {
				if (j > 0)
					System.out.print(",");
				System.out.print(set[i][0][j]);
			}
			System.out.println(") = "+set[i][1][0]);
		}
	}

}
