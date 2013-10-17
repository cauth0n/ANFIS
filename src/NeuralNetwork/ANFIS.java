package NeuralNetwork;

import java.util.Random;

import LinearAlgebra.Matrix;
import LinearAlgebra.Operations;

public class ANFIS extends Network {
	
	Random r = new Random(11235);
	double[][][] centersList;
	double[][] spreadList;
	int rules = 10;
	double gamma = 11235;
	Operations ops = new Operations();
	Matrix A;
	Matrix[] SList;
	Matrix[] XList;
	Matrix[] BList;
	
	Neuron defaultLayer1Neuron = new Neuron(FunctionType.GAUSSIAN);
	
	Layer[] Layer1List;
	
	/**
	 * Constructor for the RBF network. 
	 * Changes to the default Neural Network are modified here.
	 */
	public ANFIS() {
		
		
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
					if (min == max)
						max = min + 1;
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
		/*
		Layers.add(Layer1List[0]);
		Neuron linearNeuron = new Neuron(FunctionType.LINEAR);
		Layers.add(new Layer(linearNeuron, rules, null));
		Layers.add(new Layer(linearNeuron, rules, null));
		Layers.add(new Layer(linearNeuron, rules, null));
		Layers.add(new Layer(linearNeuron, inputs[0][1].length, null));
		*/
		
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
		buildMatrices(data);
		
		// LSE (offline)
		for (int exampleNum = 0; exampleNum < data.length && exampleNum < maxInputs; exampleNum++) {
			updateSList(exampleNum);
			updateXList(exampleNum);
			
		}
		//SList[3].printMatrix();
		//SList[4].printMatrix();;
		
	}
	
	
	//TODO -- comment
	private void updateSList(int exampleNum) {
		
		Matrix ARow = A.getRow(exampleNum);
		Matrix ARowTranspose = ARow.getTranspose();
		
		//The for loop here had XList.length as the stopping condition. IS this right now?
		for (int streamNum = 0; streamNum < SList.length; streamNum++) {
			
			Matrix P1 = SList[streamNum].multiply(ARowTranspose);
			Matrix P2 = P1.multiply(ARow);
			Matrix P3 = P2.multiply(SList[streamNum]);
			
			Matrix Q1 = ARow.multiply(SList[streamNum]);
			Matrix Q2 = Q1.multiply(ARowTranspose);
			
			double denominator = 1 + Q2.getScalar();
			
			Matrix combinedFraction = P3.scalarDivide(denominator);
			Matrix newS = SList[streamNum].subtract(combinedFraction);
			
			SList[streamNum] = newS;
			
		}
		
	}
	
	
	//TODO -- comment
	private void updateXList(int exampleNum) {
			
		Matrix ARow = A.getRow(exampleNum);
		Matrix ARowTranpose = ARow.getTranspose();
		

		for (int streamNum = 0; streamNum < XList.length; streamNum++) {
			
			for (int pathNum = 0; pathNum < XList[streamNum].width(); pathNum++) {
			
				Matrix P1 = ARow.multiply(XList[streamNum].getColumn(pathNum));
				
				double rhs = BList[streamNum].getRow(exampleNum).getScalar() - P1.getScalar();
				
				Matrix Q1 = SList[streamNum].multiply(ARowTranpose);
				Matrix Q2 = Q1.scalarMultiply(rhs);
				
				Matrix newX = XList[streamNum].getColumn(pathNum).add(Q2);
				
				XList[streamNum].setColumn(pathNum, newX);
				
			}
				
		}
			
	}
	
	private void buildMatrices(double[][][] data) {
		buildA(data);
		buildBList(data);
		buildSList(data[0][1].length);
		buildXList(data[0][1].length);
	}
	
	private void buildA(double[][][] data) {
		double[][] inputs = new double[data.length][data[0][0].length + 1];
		for (int exampleNum = 0; exampleNum < inputs.length; exampleNum++) {
			for (int featureNum = 0; featureNum < inputs[exampleNum].length - 1; featureNum++) {
				inputs[exampleNum][featureNum] = data[exampleNum][0][featureNum];
			}
			inputs[exampleNum][inputs[exampleNum].length - 1] = 1;
		}
		A = new Matrix(inputs);
	}
	
	private void buildSList(int size) {
		SList = new Matrix[size];
		for (int i = 0; i < SList.length; i++)
			SList[i] = new Matrix(ops.getIdentity(A.width())).scalarMultiply(gamma);
	}
	
	private void buildXList(int size) {
		XList = new Matrix[size];
		for (int streamNum = 0; streamNum < XList.length; streamNum++) {
			XList[streamNum] = new Matrix(A.width(), rules, 0);
		}
	}
	
	private void buildBList(double[][][] data) {
		double[][] outputs = new double[data[0][1].length][data.length];
		BList = new Matrix[outputs.length];
		for (int streamNum = 0; streamNum < outputs.length; streamNum++) {
			for (int exampleNum = 0; exampleNum < outputs[streamNum].length; exampleNum++) {
				outputs[streamNum][exampleNum] = data[exampleNum][1][streamNum];
			}
		}
		for (int streamNum = 0; streamNum < outputs.length; streamNum++)
			BList[streamNum] = new Matrix(outputs[streamNum]);
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
		if (inputs.length * rules != Layer1List[0].size())
			throw new IllegalArgumentException("Inputs must match input layer size." +
											   "Expected " + Layers.get(0).size() / rules + ", found " + inputs.length + ".");
		
		// run input through each stream of network and retrieve a results vector
		double[] outputs = new double[Layer1List.length];
		
		
		for (int streamNum = 0; streamNum < Layer1List.length; streamNum++)
			outputs[streamNum] = runSingleOutput(inputs, Layer1List[streamNum], centersList[streamNum], spreadList[streamNum], 
												XList[streamNum].getTranspose().toPrimitive(), streamNum);
		
		//Above this line.
		//System.out.println(outputs[5]);
		
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
	private double runSingleOutput(double[] inputs, Layer Layer1, double[][] centers, double[] spread, double[][] consequentParams, int streamNum) {
		

		
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
			
			if (streamNum == 4 && Double.isNaN(outputLayer1[i])){
				System.out.println();
			}
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
		for (int pathNum = 0; pathNum < outputLayer4.length; pathNum++) {
			
			// construct linear eqn in the form: (p_i*x + q_i*y + r_i)
			double ruleSum = 0.0;
			for (int j = 0; j < inputs.length; j++) {
				ruleSum += consequentParams[pathNum][j] * inputs[j];
			}
			ruleSum += consequentParams[pathNum][inputs.length]; // bias, or something similar
			
			// w_i * ruleSum (weight of rule)
			// w_i * (p_i*x + q_i*y + r_i)
			outputLayer4[pathNum] = outputLayer3[pathNum] * ruleSum;
			
		}
		
		// LAYER 5: sum the inputs
		double out = 0.0;
		for (int i = 0; i < outputLayer4.length; i++) {
			out += outputLayer4[i];
		}
		return out;
	}
	
	public void describe() {
		System.out.print("NETWORK {");
		System.out.print(Layer1List[0].size()+":"+Layer1List[0].getNeuron(0).getFunctionType()+",");
		System.out.print(rules+":Product,");
		System.out.print(rules+":Normalization,");
		System.out.print(rules+":Linear,");
		System.out.print(Layer1List.length+":Summation");
		System.out.println("}:");
	}
	
	/**
	 * Enforces pattern by disallowing activation functions to be set for ANFIS networks.
	 */
	public void setActivationFunctions(FunctionType[] fts) {
		throw new IllegalArgumentException("Cannot specify activation functions for ANFIS networks.");
	}
	
	/**
	 * Enforces pattern by disallowing hidden layers to be set for ANFIS networks.
	 */
	public void setHiddenLayers(int[] size, FunctionType ft) {
		throw new IllegalArgumentException("Cannot specify hidden layers for ANFIS networks.");
	}

}
