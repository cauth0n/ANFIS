package NeuralNetwork;
import java.util.ArrayList;

import LinearAlgebra.Operations;

public abstract class Network {

	public ArrayList<Layer> Layers = new ArrayList<Layer>();
	protected double rate = 0.01;
	protected double momentum = 0.5;
	double weightMin = -0.3;
	double weightMax = 0.3;
	double maxError;
	double stopError = 0.001;
	boolean echo = true;
	protected int maxIterations = 5000;
	protected Layer inputLayer, outputLayer;
	protected int maxInputs = 100000;
	protected boolean classify = true;
	protected int[][] confusionMatrix;
	protected Operations ops = new Operations();
	
	/**
	 * Sets the momentum of the weights updates.
	 * 
	 * @param momentum	The factor to be multiplied by the old weight update
	 */
	public void setMomentum(double momentum) {
		if (momentum > 1.0 || momentum < 0.0)
			throw new IllegalArgumentException("Momentum must be a decimal value between 0.0 and 1.0.");
		this.momentum = momentum;
	}
	
	public abstract void setHiddenLayers(int[] size, FunctionType ft);
	
	/**
	 * Describes the network in terms of layer size and activation function of the first neuron in each layer.
	 */
	public void describe() {
		System.out.print("NETWORK {");
		for (int i = 0; i < Layers.size(); i++) {
			if (i > 0)
				System.out.print(",");
			System.out.print(Layers.get(i).size()+":"+Layers.get(i).getNeuron(0).getFunctionType());
		}
		System.out.println("}:");
	}
	
	public abstract void train(double[][][] inputs);
	
	/**
	 * Returns the average output from a set of training examples.
	 * 
	 * @param set	A set of input-output examples to be trained on.
	 * @return		The average output.
	 */
	public double getAverage(double[][][] set) {
		double avg = 0.0;
		int n = 0;
		for (double[][] input : set) {
			for (int j = 0; j < input[1].length; j++) {
				avg += input[1][j];
				n++;
			}
		}
		return (avg / n);
	}
	
	/**
	 * Tests a network's ability to approximate outputs.
	 * 
	 * @param inputs	The test group from the input-output examples.
	 * @return			The mean squared error of the set.
	 */
	public double test(double[][][] inputs) {
		
		if (classify)
			confusionMatrix = new int[inputs[0][1].length][inputs[0][1].length];
		
		double totalError = 0.0;
		int correct = 0;
		for (double[][] datapoint : inputs) {
			
			double[] targets = datapoint[1];
			double[] results = run(datapoint[0]);
			
			/*
			for(double result : results)
				System.out.print(round(result,2)+",");
			System.out.println("");
			for(double target : targets)
				System.out.print(target+",");
			System.out.println("\n");
			*/
			
			// calculate error
			double error = 0.0;
			for (int i = 0; i < results.length; i++)	
				error += Math.pow((targets[i] - results[i]), 2);
			error /= results.length;
			totalError += error;
			
			// check if classification is correct
			int targetIndex = 0;
			int resultIndex = 0;
			double maxTarget = Double.MIN_VALUE;
			double maxResult = Double.MIN_VALUE;
			for (int i = 0; i < results.length; i++) {
				if (targets[i] > maxTarget)
					targetIndex = i;
				if (results[i] > maxResult)
					resultIndex = i;
				maxTarget = Math.max(maxTarget, targets[i]);
				maxResult = Math.max(maxResult, results[i]);
			}
			confusionMatrix[targetIndex][resultIndex] += 1;
			if (targetIndex == resultIndex)
				correct++;
		}
		
		System.out.println(correct + " / " + inputs.length);
		
		if (classify)
			return ((double)correct / inputs.length);
		else
			return (totalError / inputs.length);
		
	}
	
	/**
	 * Helper function to find the max distance between all centers.
	 * 
	 * @param centers
	 * @return largest distance.
	 */
	protected double maxDistance(double[][] centers) {
		double dmax = 0.0;
		for (int i = 0; i < centers.length - 1; i++) {
			for (int j = i + 1; j < centers.length; j++) {
				dmax = Math.max(dmax, ops.euclidean(centers[i], centers[j]));
			}
		}
		return dmax;
	}
	
	public void printConfusionMatrix(int[] classes) {
		
		// allow for ascii code or digits based on index of array
		for (int i = 0; i < classes.length; i++)
			if (classes[i] < 10)
				classes[i] += 48;
		
		if (confusionMatrix != null) {
			int padding = 5;
			System.out.print("   | ");
			for (int i = 0; i < classes.length; i++)
				System.out.printf("%"+padding+"c", (char) classes[i]);
			System.out.println("");
			for (int i = 0; i < (classes.length + 1) * padding; i++)
				System.out.print("-");
			System.out.println("");
			for (int i = 0; i < confusionMatrix.length; i++) {
				System.out.printf(" %c | ", (char) classes[i]);
				for (int j = 0; j < confusionMatrix.length; j++) {
					System.out.printf("%"+padding+"d",confusionMatrix[i][j]);
				}
				System.out.println("");
			}
		}
	}
	
	/**
	 * Runs the inputs through the network and returns the outputs.
	 * 
	 * @param inputs	The input values to be passed to the input layer.
	 * @return			The output values returned by the output layer.
	 */
	public double[] run(double inputs[]) {
		if (inputs.length != Layers.get(0).size())
			throw new IllegalArgumentException("Inputs must match input layer size." +
											   "Expected " + Layers.get(0).size() + ", found " + inputs.length + ".");
		double[] out = Layers.get(0).run(inputs);
		for (int i = 1; i < Layers.size(); i ++) {
			out = Layers.get(i).run(out);
		}
		
		return out;
	}
	
	/**
	 * Sets the activation functions for each layer in the network.
	 * Activations may be set on a neuron by neuron basis, but entire layers are more common.
	 * 
	 * @param fts	An array of function types for each layer.
	 */
	public void setActivationFunctions(FunctionType[] fts) {
		if (fts.length != Layers.size())
			throw new IllegalArgumentException("Must specify same number of function types as layers ("+Layers.size()+").");
		for (int i = 0; i < fts.length; i++)
			Layers.get(i).setActivationFunction(fts[i]);
	}
	
	private double round(double value, int decimal) {
		return (double)Math.round(value * Math.pow(10, decimal)) / Math.pow(10, decimal);
	}

}
