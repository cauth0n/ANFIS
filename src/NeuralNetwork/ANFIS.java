package NeuralNetwork;

import java.util.Random;
import LinearAlgebra.Operations;

public class ANFIS extends Network {
	
	double[][][] centersList;
	double[][] spreadList;
	int rules;
	double[][][] consequentParameters;
	double consequentParametersMin = -0.3;
	double consequentParametersMax = 0.3;
	Operations ops = new Operations();
	
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
		
		int numPatterns = inputs.length;
		int numStreams = inputs[0][1].length;
		int numInputs = inputs[0][0].length;
		int numInputNeurons = numInputs * rules;
		
		double[][][] centers = new double[numStreams][numInputNeurons][1];
		spreadList = new double[numStreams][numInputNeurons];
		
		double[][] mins = new double[numStreams][numInputs];
		double[][] maxs = new double[numStreams][numInputs];
		
		for (int patternNum = 0; patternNum < numPatterns; patternNum++) {
			int streamIndex;
			for (streamIndex = 0; streamIndex < numStreams; streamIndex++)
				if (inputs[patternNum][1][streamIndex] > 0.0)
					break;
			
			// get mins and maxs of each input parameter
			for (int inputNum = 0; inputNum < numInputs; inputNum++){
				mins[streamIndex][inputNum] = Math.min(mins[streamIndex][inputNum], inputs[patternNum][0][inputNum]);
				maxs[streamIndex][inputNum] = Math.max(maxs[streamIndex][inputNum], inputs[patternNum][0][inputNum]);
			}
			
		}
		
		for (int streamNum = 0; streamNum < numStreams; streamNum++) {
			for (int inputNum = 0; inputNum < numInputs; inputNum++) {
				double min = mins[streamNum][inputNum];
				double max = maxs[streamNum][inputNum];
				double step = (max - min) / (rules + 1);
				for (int ruleNum = 0; ruleNum < rules; ruleNum++) {
					centers[streamNum][inputNum * rules + ruleNum][0] = min + step * (ruleNum + 1);
					double dmax = (max - min) - (step * 2);
					spreadList[streamNum][inputNum * rules + ruleNum] = dmax / Math.sqrt(rules); 
				}
			}
		}
		
		return centers;
	}
	
	/**
	 * Internal helper function used to construct a network structure for an RBF.
	 * 
	 * @param inputs	The inputs to be trained on (used to determine input and output layer size).
	 */
	private void constructNetwork(double[][][] inputs) {
		
		Layers.clear();
		
		// List of Layer1 objects corresponding to the different streams or outputs
		Layer1List = new Layer[inputs[0][1].length];
		for (int i = 0; i < Layer1List.length; i++)
			Layer1List[i] = new Layer(defaultLayer1Neuron, inputs[0][0].length * rules, null);
		
		
		// Network is traversed in run code, so Layers is not actually used
		// The following is strictly for displaying the network structure in the "describe" function
		Layers.add(Layer1List[0]);
		Neuron linearNeuron = new Neuron(FunctionType.LINEAR);
		Layers.add(new Layer(linearNeuron, rules, null));
		Layers.add(new Layer(linearNeuron, rules, null));
		Layers.add(new Layer(linearNeuron, rules, null));
		Layers.add(new Layer(linearNeuron, inputs[0][1].length, null));
		
		// initialize all consequent parameters for each stream to small decimal values
		consequentParameters = new double[inputs[0][1].length][rules][inputs[0][0].length + 1];
		initializeParams(consequentParameters);
		
	}
	
	/**
	 * Trains the network given a set of inputs and outputs, expected in the form:
	 * inputs[example_index][0] = array of input values
	 * inputs[example_index][1] = array of output values
	 */
	public void train(double[][][] data) {
		
		// first think, calculate the centers by evenly partitioning
		// the range of each input with respect to outputs
		centersList = calculateCentersRanges(data);
		
		// construct the network, using the dimension of inputs and outputs
		// to determine input layer size and number of streams
		constructNetwork(data);
		
		// loop through the input/output patterns and train on each
		int percent = -1;
		for (int in = 0; in < data.length && in < maxInputs; in++) {
			
			double[][] datapoint = data[in];
			
			// if echo flag is set to true, print the percentage load bar
			if (echo == true && percent != (int) (100*((float)in/data.length))) {
				percent = (int) (100*((float)in/data.length));
				System.out.println(percent+"%");
			}
			
			// get target value for current example and initialize results
			double[] targets = datapoint[1];
			double[] results = new double[targets.length];
			
			// train on current example until error is small enough or max iterations exceeded
			maxError = Double.MAX_VALUE;
			for (int it = 0; maxError > stopError && it < maxIterations; it++) {
				
				results = run(datapoint[0]);  // run the inputs through the network and retrieve the results
				backpropagate(targets, results);  // backpropogate to train premise params
				maxError = Math.pow((Math.abs(targets[0] - results[0])), 2);  // calculate the max squared error
				
			}
			
		}
	}
	
	/**
	 * Backward propogate error from output to premise parameters for each stream.
	 * 
	 * @param targets	The target values of the network.
	 * @param results	The actual results of the network.
	 */
	private void backpropagate(double[] targets, double[] results) {
		
		
		
	}
	
	/**
	 * Runs the inputs through the network and returns the outputs.
	 * 
	 * @param inputs	The input values to be passed to the input layer.
	 * @return			The output values returned by the output layer.
	 */
	public double[] run(double inputs[]) {
		
		// validate input dimensions
		if (inputs.length * rules != Layers.get(0).size())
			throw new IllegalArgumentException("Inputs must match input layer size." +
											   "Expected " + Layers.get(0).size() / rules + ", found " + inputs.length + ".");
		
		// run input through each stream of network and retrieve a results vector
		double[] outputs = new double[Layer1List.length];
		for (int i = 0; i < Layer1List.length; i++)
			outputs[i] = runSingleOutput(inputs, Layer1List[i], centersList[i], spreadList[i], consequentParameters[i]);
		
		return outputs;
	}
	
	/**
	 * Runs an input through a single stream of the network, resulting in a scalar output.
	 * 
	 * @param inputs
	 * @param Layer1
	 * @param centers
	 * @param spread
	 * @param consequentParams
	 * @return	The scalar output of the stream, bounded by range [0,1]
	 */
	private double runSingleOutput(double[] inputs, Layer Layer1, double[][] centers, double[] spread, double[][] consequentParams) {
		
		// LAYER 1: Gaussian activation functions
		double[] inputLayer1 = new double[inputs.length * rules];
		// pass input to each connected node in layer 1
		for (int i = 0; i < inputs.length; i++)
			for (int j = 0; j < rules; j++)
				inputLayer1[i * rules + j] = inputs[i];
		// run input on each activation function in layer 1 to retrieve output vector
		double[] outputLayer1 = new double[inputLayer1.length];
		for (int i = 0; i < Layer1.size(); i++) {
			outputLayer1[i] = Layer1.getNeuron(i).fire(ops.norm(new double[]{inputLayer1[i]}, centers[i]), spread[i]);
		}

		// LAYER 2: create product of rules
		double[] outputLayer2 = new double[rules];
		for (int i = 0; i < outputLayer1.length; i++) {
			int index = i % rules;
			if (index == 0)
				outputLayer2[index] = outputLayer1[i];	// initialize each product to first input's neurons
			else
				outputLayer2[index] *= outputLayer1[i];  // multiply each product by corresponding neurons for the rest of the inputs
		}
		double[] inputLayer3 = outputLayer2;
		
		// LAYER 3: normalize inputs
		double[] outputLayer3 = new double[rules];
		double layer3Sum = 0.0;
		// sum up all inputs to layer 3
		for (int i = 0; i < inputLayer3.length; i++)
			layer3Sum += inputLayer3[i];
		// divide each input by the sum, thus normalizing
		for (int i = 0; i < inputLayer3.length; i++)
			outputLayer3[i] = inputLayer3[i] / layer3Sum;
		
		// LAYER 4: linear function of inputs
		double[] outputLayer4 = new double[rules];
		for (int i = 0; i < outputLayer4.length; i++) {
			
			// construct linear eqn in the form: (p_i*x + q_i*y + r_i)
			double ruleSum = 0.0;
			for (int j = 0; j < inputs.length; j++) {
				ruleSum += consequentParams[i][j] * inputs[j];
			}
			ruleSum += consequentParams[i][inputs.length]; // bias, or something similar
			
			// w_i * ruleSum (weight of rule)
			// w_i * (p_i*x + q_i*y + r_i)
			outputLayer4[i] = outputLayer3[i] * ruleSum;
			
		}
		
		// LAYER 5: sum the inputs
		double out = 0.0;
		for (int i = 0; i < outputLayer4.length; i++) {
			out += outputLayer4[i];
		}
		
		return out;
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
