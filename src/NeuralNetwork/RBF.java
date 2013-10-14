package NeuralNetwork;

public class RBF extends Network {
	
	boolean customCenters = false;
	double[][] centers;
	double spread;
	int partitions = 5;
	
	Neuron defaultInputNeuron = new Neuron(FunctionType.LINEAR);
	Neuron defaultHiddenNeuron = new Neuron(FunctionType.GAUSSIAN);
	Neuron defaultOutputNeuron = new Neuron(FunctionType.LINEAR);
	
	/**
	 * Constructor for the RBF network. 
	 * Changes to the default Neural Network are modified here.
	 */
	public RBF() {
		rate = 3.0;   // ???
	}
	
	public void setCenters(double[][] centers) {
		this.centers = centers;
		customCenters = true;
	}
	
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
		spread *= 0.35;
		
		return centers;
	}
	
	private double maxDistance(double[][] centers) {
		double dmax = 0.0;
		for (int i = 0; i < centers.length - 1; i++) {
			for (int j = i + 1; j < centers.length; j++) {
				dmax = Math.max(dmax, euclidean(centers[i], centers[j]));
			}
		}
		return dmax;
	}
	
	/**
	 * Calculates the centers by evenly partitioning the input space.
	 * 
	 * @param inputs	The set of input-output examples.
	 * @return			An array of center vectors.
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
		double dmax = euclidean(centers[0], centers[centers.length - 1]);
		spread = dmax / Math.sqrt(centers.length);
		
		return centers;
	}
	
	/**
	 * Helper function for the calculateCenters function.
	 * Evenly splits up input spaces based on min and max values given a specific number of steps.
	 * 
	 * @param starts	The minimum values of the input spaces.
	 * @param ranges	The maximum values of the input spaces.
	 * @param steps		The number of partitions to split each input space into.
	 * @return			An array of center vectors.
	 */
	public double[][] getUniformSet(double[] starts, double[] ranges, int steps) {
		if (starts.length != ranges.length)
			throw new IllegalArgumentException("Number of starting values and ending values must match.");
		int points = (int)Math.pow(steps+1,ranges.length);
		System.out.println(points);
		double[][] set = new double[points][];
		for (int r = 0; r < ranges.length; r++) {
			int index = 0;
			int superset = (int)Math.pow(steps+1, r);
			for (int sup = 0; sup < superset; sup++) {
				double x = starts[r];
				for(int s = 0; s <= steps; s++) {
					x = (double)Math.round(x * 1000000) / 1000000;
					int subset = (int)Math.pow(steps+1,(ranges.length-(r+1)));
					for (int i = 0; i < subset; i++) {
						if (r == 0)
							set[index] = new double[ranges.length];
						set[index][r] = x;
						index++;
					}
					x+=((ranges[r]-starts[r])/steps);
				}
			}
		}
		
		return set;
	}
	
	/**
	 * Internal helper function used to construct a network structure for an RBF.
	 * 
	 * @param inputs	The inputs to be trained on (used to determine input and output layer size).
	 */
	private void constructNetwork(double[][][] inputs) {
		Layers.clear();
		inputLayer = new Layer(defaultInputNeuron, inputs[0][0].length, null);
		Layers.add(0, inputLayer);
		Layers.add(new Layer(defaultHiddenNeuron, centers.length, inputLayer));
		outputLayer = new Layer(defaultOutputNeuron, inputs[0][1].length, Layers.get(Layers.size() - 1));
		Layers.add(outputLayer);
	}
	
	/**
	 * Trains the network given a set of inputs and outputs, expected in the form:
	 * inputs[example_index][0] = array of input values
	 * inputs[example_index][1] = array of output values
	 */
	public void train(double[][][] inputs) {
		if (!customCenters) {
			if (classify)
				centers = calculateCentersMeans(inputs);
			else
				centers = calculateCentersUniform(inputs);
		}
		constructNetwork(inputs);
		
		double maxOutput = 0.0;
		for (int in = 0; in < inputs.length; in++)
			for (int out = 0; out < inputs[in][1].length; out++)
				maxOutput = Math.max(Math.abs(inputs[in][1][out]), maxOutput);
		
		// scale activation functions based on inputs
		for (Layer layer : Layers)
			layer.scaleFunctions(maxOutput/4);
		
		int percent = -1;
		if (norm != null)
			norm.normalize(inputs);
		
		for (int in = 0; in < inputs.length && in < maxInputs; in++) {
			double[][] datapoint = inputs[in];
			
			if (echo == true && percent != (int) (100*((float)in/inputs.length))) {
				percent = (int) (100*((float)in/inputs.length));
				System.out.println(percent+"%");
			}
			
			double[] targets = datapoint[1];
			double[] results;
			
			maxError = Double.MAX_VALUE;
			int it;
			for (it = 0; maxError > stopError && it < maxIterations; it++) {
				results = run(datapoint[0]);
				backpropagate(targets, results);
				
				maxError = Math.pow((Math.abs(targets[0] - results[0])), 2);
				
			}
		}
	}
	
	/**
	 * A simplified version of the backprop algorithm to be used by the train function.
	 * Error is propogated from the output layer back to the input layer.
	 * 
	 * @param targets	The target values of the network.
	 * @param results	The actual results of the network.
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
		for (int j = 0; j < output.size(); j++) {
			double diff = (targets[j] - results[j]);
			maxError = Math.max(maxError, Math.pow(diff, 2));
			errors[outIndex][j] = (output.getNeuron(j).gradient() * diff);
			for (int k = 0; k < hidden.size(); k++) {
				double update = (rate * hidden.getNeuron(k).output * errors[outIndex][j]);
				output.weights[j][k] += update;
				output.oldWeights[j][k] = update;
				change = Math.max(change, Math.abs(update));
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
		
		double[] inVal;
		double[] hiddenVal;
		double[] outVal;
		
		inVal = Layers.get(0).run(inputs);

		Layer hidden = Layers.get(1);
		hiddenVal = new double[hidden.size()];
		for (int i = 0; i < hidden.size(); i++) {
			hiddenVal[i] = hidden.getNeuron(i).fire(norm(inVal, centers[i]), spread);
		}
		
		outVal = Layers.get(2).run(hiddenVal);
		
		return outVal;
	}
	
	/**
	 * The default norm of two vectors (euclidean). 
	 * 
	 * @param X	The x vector.
	 * @param Y	The y vector.
	 * @return	The normed vector.
	 */
	protected double norm(double[] X, double[] Y) {
		return euclidean(X, Y);
	}
	
	/**
	 * The manhattan norm of two vectors. 
	 * 
	 * @param X	The x vector.
	 * @param Y	The y vector.
	 * @return	The normed vector.
	 */
	protected double manhattan(double[] X, double[] Y) {
		return LPNorm(X, Y, 1);
	}
	
	/**
	 * The euclidean norm of two vectors.
	 * 
	 * @param X	The x vector.
	 * @param Y	The y vector.
	 * @return	The normed vector.
	 */
	protected double euclidean(double[] X, double[] Y) {
		return LPNorm(X, Y, 2);
	}
	
	/**
	 * The max coordinant norm of two vectors.
	 * 
	 * @param X	The x vector.
	 * @param Y	The y vector.
	 * @return	The normed vector.
	 */
	protected double maxCoordDist(double[] X, double[] Y) {
		double max = 0.0;
		for (double x : X)
			for (double y : Y)
				max = Math.max(max, Math.abs(x - y));
		return max;
	}
	
	/**
	 * A p-norm of two vectors.
	 * 
	 * @param X	The x vector.
	 * @param Y	The y vector.
	 * @param p The p value of the norm to be taken.
	 * @return	The normed vector.
	 */
	protected double LPNorm(double[] X, double[] Y, int p) {
		double sum = 0.0;
		for (int i = 0; i < X.length; i++)
			sum += Math.pow((X[i] - Y[i]), p);
		sum = Math.pow(sum, 1.0/p);
		return sum;
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
