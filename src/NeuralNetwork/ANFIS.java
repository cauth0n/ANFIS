package NeuralNetwork;

import java.util.Random;

public class ANFIS extends Network {
	
	double[][][] centersList;
	double[][] spreadList;
	int rules;
	double[][][] consequentParameters;
	double consequentParametersMin = -0.3;
	double consequentParametersMax = 0.3;
	
	
	//Neuron defaultInputNeuron = new Neuron(FunctionType.LINEAR);
	//Neuron defaultHiddenNeuron = new Neuron(FunctionType.GAUSSIAN);
	//Neuron defaultOutputNeuron = new Neuron(FunctionType.LINEAR);
	
	Neuron defaultLayer1Neuron = new Neuron(FunctionType.GAUSSIAN);
	
	Layer[] Layer1List;
	
	/**
	 * Constructor for the RBF network. 
	 * Changes to the default Neural Network are modified here.
	 */
	public ANFIS() {
		rate = 3.0;   // ???
		rules = 2;
	}
	
	/**
	 * Internal helper function used to initialize parameters to small random numbers.
	 */
	private void initializeParams(double[][][] params) {
		Random r = new Random(11235);
		for (int i = 0; i < params.length; i++) {
			for (int j = 0; j < params[i].length; j++)
				for (int k = 0; k < params[i][j].length; k++)
					params[i][j][k] = consequentParametersMin + (consequentParametersMax - consequentParametersMin) * r.nextDouble();
		}
	}
	
	protected double[][][] calculateCentersRanges(double[][][] inputs) {
		
		double[][][] centers = new double[inputs[0][1].length][inputs[0][0].length * rules][1];
		spreadList = new double[inputs[1][0].length][inputs[0][0].length * rules];
		
		double[] mins = new double[inputs[0][0].length];
		double[] maxs = new double[inputs[0][0].length];
		
		// TODO: different mins and maxs should be calculated for each class (input stream)
		
		// get mins and maxs of each input parameter
		for (int i = 0; i < inputs.length; i++) {
			for (int j = 0; j < inputs[i][0].length; j++) {
				mins[j] = Math.min(mins[j], inputs[i][0][j]);
				maxs[j] = Math.max(maxs[j], inputs[i][0][j]);
			}
		}
		
		// construct centers by uniformly splitting min-max range of each input into *rules* number of pieces
		// could be done using k-means as well
		int layer1Index = 0;
		for (int i = 0; i < inputs[0][0].length; i++) {
			double size = maxs[i] - mins[i];
			double stepSize = size / (rules * 2);
			double dmax = stepSize * 2;
			for (int j = 1; j < (rules * 2); j+=2) {
				// duplicate centers for each network stream
				for (int k = 0; k < inputs[0][1].length; k++) {
					centers[k][layer1Index][0] = mins[i] + j * stepSize;
					spreadList[k][layer1Index++] = dmax / Math.sqrt(rules);
				}
			}
		}
		
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
	 * Internal helper function used to construct a network structure for an RBF.
	 * 
	 * @param inputs	The inputs to be trained on (used to determine input and output layer size).
	 */
	private void constructNetwork(double[][][] inputs) {
		
		Layers.clear();
		
		Layer1List = new Layer[inputs[0][1].length];
		for (int i = 0; i < Layer1List.length; i++)
			Layer1List[i] = new Layer(defaultLayer1Neuron, inputs[0][0].length * rules, null);
		
		Layers.add(Layer1List[0]);
		Neuron linearNeuron = new Neuron(FunctionType.LINEAR);
		Layers.add(new Layer(linearNeuron, rules, null));
		Layers.add(new Layer(linearNeuron, rules, null));
		Layers.add(new Layer(linearNeuron, rules, null));
		Layers.add(new Layer(linearNeuron, inputs[0][1].length, null));
		
		consequentParameters = new double[inputs[0][1].length][rules][inputs[0][0].length + 1];
		initializeParams(consequentParameters);
		
		describe();
	}
	
	/**
	 * Trains the network given a set of inputs and outputs, expected in the form:
	 * inputs[example_index][0] = array of input values
	 * inputs[example_index][1] = array of output values
	 */
	public void train(double[][][] inputs) {
		centersList = calculateCentersRanges(inputs);
		constructNetwork(inputs);
		
		double maxOutput = 0.0;
		for (int in = 0; in < inputs.length; in++)
			for (int out = 0; out < inputs[in][1].length; out++)
				maxOutput = Math.max(Math.abs(inputs[in][1][out]), maxOutput);
		
		// scale activation functions based on inputs
		//for (Layer layer : Layers)
		//	layer.scaleFunctions(maxOutput/4);
		
		int percent = -1;
		
		for (int in = 0; in < inputs.length && in < maxInputs; in++) {
			double[][] datapoint = inputs[in];
			
			if (echo == true && percent != (int) (100*((float)in/inputs.length))) {
				percent = (int) (100*((float)in/inputs.length));
				System.out.println(percent+"%");
			}
			
			double[] targets = datapoint[1];
			double[] results = new double[targets.length];
			
			maxError = Double.MAX_VALUE;
			int it;
			for (it = 0; maxError > stopError && it < maxIterations; it++) {
				results = run(datapoint[0]);
				backpropagate(targets, results);
				maxError = Math.pow((Math.abs(targets[0] - results[0])), 2);
				
			}
			
			/*
			for (int i = 0; i < targets.length; i++)
				System.out.printf("%5s",targets[i]);
			System.out.println("");
			for (int i = 0; i < results.length; i++)
				System.out.printf("%5s",results[i]);
			System.out.println("\n");
			*/
			
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
		
		// UPDATE PREMISE PARAMS WITH GRADIENT DESCENT
		
		for (int j = 0; j < results.length; j++) {
			
			double diff = (targets[j] - results[j]);
			maxError = Math.max(maxError, Math.pow(diff, 2));
			
			Layer layer = Layer1List[j];
			
			for (int k = 0; k < Layer1List[j].size(); k++) {
				double update = -1 * rate * Layer1List[j].getNeuron(k).output * diff;
				spreadList[j][k] += update;
			}
			
		}
		
		/*
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
		*/
	}
	
	/**
	 * Runs the inputs through the network and returns the outputs.
	 * 
	 * @param inputs	The input values to be passed to the input layer.
	 * @return			The output values returned by the output layer.
	 */
	public double[] run(double inputs[]) {
		if (inputs.length * rules != Layers.get(0).size())
			throw new IllegalArgumentException("Inputs must match input layer size." +
											   "Expected " + Layers.get(0).size() / rules + ", found " + inputs.length + ".");
		
		double[] outputs = new double[Layer1List.length];
		for (int i = 0; i < Layer1List.length; i++)
			outputs[i] = runSingleOutput(inputs, Layer1List[i], centersList[i], spreadList[i], consequentParameters[i]);
		
		return outputs;
	}
	
	private double runSingleOutput(double[] inputs, Layer Layer1, double[][] centers, double[] spread, double[][] consequentParams) {
		
		// feed inputs to layer 1
		double[] inputLayer1 = new double[inputs.length * rules];
		for (int i = 0; i < inputs.length; i++)
			for (int j = 0; j < rules; j++)
				inputLayer1[i * rules + j] = inputs[i];
		double[] outputLayer1 = new double[inputLayer1.length];
		for (int i = 0; i < Layer1.size(); i++) {
			outputLayer1[i] = Layer1.getNeuron(i).fire(norm(new double[]{inputLayer1[i]}, centers[i]), spread[i]);
			System.out.print(inputLayer1[i]+"=>"+outputLayer1[i]+" ");
		}
		System.out.println("");

		// create product of rules at layer 2
		double[] outputLayer2 = new double[rules];
		for (int i = 0; i < outputLayer1.length; i++) {
			int index = i % rules;
			if (index == 0)
				outputLayer2[index] = outputLayer1[i];
			else
				outputLayer2[index] *= outputLayer1[i];
		}
		double[] inputLayer3 = outputLayer2;
		
		// normalize inputs at layer 3
		double[] outputLayer3 = new double[rules];
		double layer3Sum = 0.0;
		for (int i = 0; i < inputLayer3.length; i++)
			layer3Sum += inputLayer3[i];
		for (int i = 0; i < inputLayer3.length; i++)
			outputLayer3[i] = inputLayer3[i] / layer3Sum;
		
		// feed normalized results to layer 4
		double[] outputLayer4 = new double[rules];
		for (int i = 0; i < outputLayer4.length; i++) {
			// (p_i*x + q_i*y + r_i)
			double ruleSum = 0.0;
			for (int j = 0; j < inputs.length; j++) {
				ruleSum += consequentParams[i][j] * inputs[j];
			}
			ruleSum += consequentParams[i][inputs.length]; // bias, or something similar
			// w_i * ruleSum (weight of rule)
			outputLayer4[i] = outputLayer3[i] * ruleSum;
		}
		
		// sum the inputs in layer 5
		double out = 0.0;
		for (int i = 0; i < outputLayer4.length; i++) {
			out += outputLayer4[i];
		}
		
		return out;
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
