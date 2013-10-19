package NeuralNetwork;

import LinearAlgebra.Operations;


public class RBF extends Network {

	boolean customCenters = false;
	double[][] centers;
	double spread;
	int partitions = 5;
	
	int k = 15;
	double rate = 3.0;
	double spreadCoefficient = 0.3;
	double functionCoefficient = 0.6;
	
	boolean echo = true;

	Neuron defaultInputNeuron = new Neuron(FunctionType.LINEAR);
	Neuron defaultHiddenNeuron = new Neuron(FunctionType.GAUSSIAN);
	Neuron defaultOutputNeuron = new Neuron(FunctionType.LINEAR);
	Operations operations = new Operations();

	

	public RBF(int k, double rate, double spreadCoefficient, double functionCoefficient) {
		this.k = k;
		this.rate = rate;
		this.spreadCoefficient = spreadCoefficient;
		this.functionCoefficient = functionCoefficient;
	}

	public void setCenters(double[][] centers) {
		this.centers = centers;
		customCenters = true;
	}
	
	/**
	 * Finds K-mean clusters for each output.
	 * The complexity is due to the restructuring of 
	 * input-output data to be used by KMeans.
	 * 
	 * @param inputs
	 * @return
	 */
	protected double[][] calculateCentersKMeans(double[][][] inputs) {
		
		int numPatterns = inputs.length;
		int numOutputs = inputs[0][1].length;
		int numInputs = inputs[0][0].length;
		int numInputNeurons = numInputs * k;
		
		double[][] centers = new double[numOutputs * k][numInputNeurons];
		
		// examples[stream#][example#][features]
		double[][][] examples = new double[numOutputs][][];
		int[] exampleIndexes = new int[numOutputs];
		int[] outputSizes = new int[numOutputs];
		
		// find number of examples of each output
		for (int patternNum = 0; patternNum < numPatterns; patternNum++) {
			
			// find which output this example belongs to
			int outputIndex;
			for (outputIndex = 0; outputIndex < numOutputs; outputIndex++)
				if (inputs[patternNum][1][outputIndex] > 0.0)
					break;
			outputSizes[outputIndex]++;
		}
		
		// store example with respect to each output for use by kmeans
		for (int patternNum = 0; patternNum < numPatterns; patternNum++) {
			
			// find which output this example belongs to
			int outputIndex;
			for (outputIndex = 0; outputIndex < numOutputs; outputIndex++)
				if (inputs[patternNum][1][outputIndex] > 0.0)
					break;
			
			// store example for this stream
			int exampleIndex = exampleIndexes[outputIndex]++;
			if (exampleIndex == 0)
				examples[outputIndex] = new double[outputSizes[outputIndex]][];
			examples[outputIndex][exampleIndex] = inputs[patternNum][0];
			
		}
		
		
		for (int outputNum = 0; outputNum < numOutputs; outputNum++) {
				
			// run K-Means clustering
			KMeans km = new KMeans(examples[outputNum], k);
			double[][] means = km.getMeans();
			
			// find max distance between centers and increase it if they are on top of each other
			double dmax = maxDistance(means);
			spread = dmax / Math.sqrt(k); 
			
			// set centers for current output
			for (int meanNum = 0; meanNum < means.length; meanNum++) {
				int index = outputNum * k + meanNum;
				centers[index] = means[meanNum];
			}
			
		}
		
		
		
		return centers;
	}

	/**
	 * TODO
	 * 
	 * This method is only used if dealing with classification.
	 * 
	 * @param inputs
	 * @return
	 */
	protected double[][] calculateCentersMeans(double[][][] inputs) {

		double[][] centers = new double[inputs[0][1].length][inputs[0][0].length];
		int[] counts = new int[inputs[0][1].length];

		for (int i = 0; i < inputs.length; i++) {
			for (int j = 0; j < inputs[i][1].length; j++) {
				counts[j]++;
				if (inputs[i][1][j] == 1) {
					for (int k = 0; k < inputs[i][0].length; k++) {
						centers[j][k] += inputs[i][0][k];
					}
					break;
				}
			}
		}

		for (int i = 0; i < centers.length; i++) {
			for (int j = 0; j < centers[i].length; j++) {

				centers[i][j] /= counts[i];
			}
		}

		double dmax = maxDistance(centers);
		spread = dmax / Math.sqrt(centers.length);

		return centers;
	}

	/**
	 * Calculates the centers by evenly partitioning the input space.
	 * 
	 * We take the min and max values from the input data and set that as our
	 * state space.
	 * 
	 * This method is only used if dealing with function approximation.
	 * 
	 * @param inputs
	 *            The set of input-output examples.
	 * @return An array of center vectors.
	 */
	protected double[][] calculateCentersUniform(double[][][] inputs) {
		double[] mins = new double[inputs[0][0].length];
		double[] maxs = new double[inputs[0][0].length];
		for (int i = 0; i < inputs.length; i++) {
			for (int j = 0; j < inputs[0][0].length; j++) {
				if (i == 0) {
					maxs[j] = inputs[i][0][j];
					mins[j] = inputs[i][0][j];
				} else {
					maxs[j] = Math.max(maxs[j], inputs[i][0][j]);
					mins[j] = Math.min(mins[j], inputs[i][0][j]);
				}
			}
		}
		double[][] centers = getUniformSet(mins, maxs, partitions - 1);
		double dmax = operations.euclidean(centers[0], centers[centers.length - 1]);
		spread = dmax / Math.sqrt(centers.length);

		return centers;
	}

	/**
	 * Helper function for the calculateCentersUniform function. Evenly splits
	 * up input spaces based on min and max values given a specific number of
	 * steps.
	 * 
	 * Only used in function approx.
	 * 
	 * @param starts
	 *            The minimum values of the input spaces.
	 * @param ranges
	 *            The maximum values of the input spaces.
	 * @param steps
	 *            The number of partitions to split each input space into.
	 * @return An array of center vectors.
	 */
	public double[][] getUniformSet(double[] starts, double[] ranges, int steps) {
		if (starts.length != ranges.length)
			throw new IllegalArgumentException("Number of starting values and ending values must match.");
		int points = (int) Math.pow(steps + 1, ranges.length);
		System.out.println(points);
		double[][] set = new double[points][];
		for (int r = 0; r < ranges.length; r++) {
			int index = 0;
			int superset = (int) Math.pow(steps + 1, r);
			for (int sup = 0; sup < superset; sup++) {
				double x = starts[r];
				for (int s = 0; s <= steps; s++) {
					x = (double) Math.round(x * 1000000) / 1000000;
					int subset = (int) Math.pow(steps + 1, (ranges.length - (r + 1)));
					for (int i = 0; i < subset; i++) {
						if (r == 0)
							set[index] = new double[ranges.length];
						set[index][r] = x;
						index++;
					}
					x += ((ranges[r] - starts[r]) / steps);
				}
			}
		}

		return set;
	}

	/**
	 * Internal helper function used to construct a network structure for an
	 * RBF.
	 * 
	 * @param inputs
	 *            The inputs to be trained on (used to determine input and
	 *            output layer size).
	 */
	private void constructNetwork(double[][][] inputs) {
		// Removes any garbage from network
		Layers.clear();

		// Adds our input neuron layer.
		inputLayer = new Layer(defaultInputNeuron, inputs[0][0].length, null);
		Layers.add(0, inputLayer);

		// Adds our hidden neuron layer, with connections coming from the input
		// layer.
		Layers.add(new Layer(defaultHiddenNeuron, centers.length, inputLayer));

		// Adds the output layer, with connections coming from the previous
		// layer.
		outputLayer = new Layer(defaultOutputNeuron, inputs[0][1].length, Layers.get(Layers.size() - 1));
		Layers.add(outputLayer);
	}

	/**
	 * Trains the network given a set of inputs and outputs, expected in the
	 * form: inputs[example_index][0] = array of input values
	 * inputs[example_index][1] = array of output values
	 */
	public void train(double[][][] inputs) {
		
		if (!customCenters) {
			// classify is set in the Network class
			if (classify)
				centers = calculateCentersKMeans(inputs);
			else
				// is function approx.
				centers = calculateCentersUniform(inputs);
		}
		spread *= spreadCoefficient;
		constructNetwork(inputs);

		double maxOutput = 0.0;
		for (int in = 0; in < inputs.length; in++)
			for (int out = 0; out < inputs[in][1].length; out++)
				maxOutput = Math.max(Math.abs(inputs[in][1][out]), maxOutput);

		// scale activation functions based on inputs
		for (Layer layer : Layers)
			layer.scaleFunctions(maxOutput * functionCoefficient);

		int percent = -1;

		for (int in = 0; in < inputs.length && in < maxInputs; in++) {
			double[][] datapoint = inputs[in];

			// prints out the % done with the the training. Echo is set in
			// NEtwork.
			if (echo == true && percent != (int) (100 * ((float) in / inputs.length))) {
				percent = (int) (100 * ((float) in / inputs.length));
				System.out.println(percent + "%");
			}

			double[] targets = datapoint[1];
			double[] results;

			maxError = Double.MAX_VALUE;
			int it;
			// Stopping condition for RBF. Based on maxError primarily, but if a
			// certain number of iterations is complete, we stop.
			for (it = 0; maxError > stopError && it < maxIterations; it++) {
				results = run(datapoint[0]);
				backpropagate(targets, results);

				maxError = Math.pow((Math.abs(targets[0] - results[0])), 2);

			}
		}
	}

	/**
	 * A simplified version of the backprop algorithm to be used by the train
	 * function. Error is propogated from the output layer back to the input
	 * layer.
	 * 
	 * @param targets
	 *            The target values of the network.
	 * @param results
	 *            The actual results of the network.
	 */
	private void backpropagate(double[] targets, double[] results) {
		int outIndex = (Layers.size() - 1);
		Layer output = Layers.get(outIndex);
		Layer hidden = Layers.get(outIndex - 1);
		double[][] errors = new double[Layers.size()][];
		double change = 0.0;
		maxError = 0.0;

		// update output layer
		errors[outIndex] = new double[output.size()];

		// Loop through output layer to find errors.
		for (int j = 0; j < output.size(); j++) {
			double diff = (targets[j] - results[j]);

			// squared error to exit the loop. I don't think we need this line,
			// but it doesn't matter.
			// FIXME
			maxError = Math.max(maxError, Math.pow(diff, 2));

			// indexes the error as the gradient * the difference.
			errors[outIndex][j] = (output.getNeuron(j).gradient() * diff);
			for (int k = 0; k < hidden.size(); k++) {
				double update = (rate * hidden.getNeuron(k).output * errors[outIndex][j]);

				// weight update.
				output.weights[j][k] += update;

				// FIXME
				// This line is not doing anything
				output.oldWeights[j][k] = update;

				// FIXME
				// This line is not doing anything as far as I can tell.
				change = Math.max(change, Math.abs(update));
			}
		}

	}

	/**
	 * Runs the inputs through the network and returns the outputs.
	 * 
	 * Our feedforward part.
	 * 
	 * @param inputs
	 *            The input values to be passed to the input layer.
	 * @return The output values returned by the output layer.
	 */
	public double[] run(double inputs[]) {
		if (inputs.length != Layers.get(0).size())
			throw new IllegalArgumentException("Inputs must match input layer size." + "Expected " + Layers.get(0).size() + ", found " + inputs.length + ".");

		double[] inVal;
		double[] hiddenVal;
		double[] outVal;

		// Run the Inputs through the Layer run method. @NeuralNetwork.Layer
		inVal = Layers.get(0).run(inputs);

		Layer hidden = Layers.get(1);
		hiddenVal = new double[hidden.size()];

		// Loops through the hidden layer, firing neurons with all calculated
		// information.

		// fire() is a function in @NeuralNetwork.Layer
		for (int i = 0; i < hidden.size(); i++) {
			hiddenVal[i] = hidden.getNeuron(i).fire(operations.norm(inVal, centers[i]), spread);
		}

		// Finally, run through output neurons and return last value.
		outVal = Layers.get(2).run(hiddenVal);

		return outVal;
	}

	/**
	 * Enforces pattern by disallowing activation functions to be set for RBFs.
	 */
	public void setActivationFunctions(FunctionType[] fts) {
		throw new IllegalArgumentException("Cannot specify activation functions for RBFs.");
	}

	/**
	 * Enforces pattern by disallowing hidden layers to be set for RBFs.
	 */
	public void setHiddenLayers(int[] size, FunctionType ft) {
		throw new IllegalArgumentException("Cannot specify hidden layers for RBFs.");
	}

}
