package NeuralNetwork;

public class MLP extends Network {

	Neuron defaultInputNeuron = new Neuron(FunctionType.LINEAR);
	Neuron defaultHiddenNeuron = new Neuron(FunctionType.TANH);
	Neuron defaultOutputNeuron = new Neuron(FunctionType.LINEAR);
	private int[] hiddenSize = new int[] {};
	private FunctionType hiddenActivation;

	double scalarCoefficient = 0.25;

	/**
	 * Constructor for the MLP network. Changes to the default Neural Network
	 * are modified here.
	 */
	public MLP() {
		// rate = 0.002 for Pen Digits
		rate = 0.002;
		// maxInputs = 4500;
	}

	/**
	 * Sets the number and size of each hidden layer as well as the function
	 * type for each neuron.
	 * 
	 * @param size
	 *            an array corresponding to the number of neurons in each hidden
	 *            layer
	 * @param ft
	 *            the function type to be used for each neuron in the hidden
	 *            layer
	 */
	public void setHiddenLayers(int[] size, FunctionType ft) {
		hiddenSize = size;
		hiddenActivation = ft;
	}

	/**
	 * Internal helper function used to construct a network structure for an
	 * MLP.
	 * 
	 * @param inputs
	 *            The inputs to be trained on (used to determine input and
	 *            output layer size).
	 */
	private void constructNetwork(double[][][] inputs) {
		Layers.clear();
		Neuron neuron = new Neuron();
		Neuron neuronHidden = new Neuron(hiddenActivation);
		inputLayer = new Layer(neuron, inputs[0][0].length, null);
		Layers.add(0, inputLayer);
		for (int i = 0; i < hiddenSize.length; i++) {
			Layers.add(new Layer(neuronHidden, hiddenSize[i], Layers.get(i)));
		}
		outputLayer = new Layer(neuron, inputs[0][1].length, Layers.get(Layers.size() - 1));
		Layers.add(outputLayer);
	}

	/**
	 * Trains the network given a set of inputs and outputs, expected in the
	 * form: inputs[example_index][0] = array of input values
	 * inputs[example_index][1] = array of output values
	 */
	public void train(double[][][] inputs) {

		constructNetwork(inputs);

		double maxOutput = 0.0;
		for (int in = 0; in < inputs.length; in++)
			for (int out = 0; out < inputs[in][1].length; out++)
				maxOutput = Math.max(Math.abs(inputs[in][1][out]), maxOutput);

		// scale activation functions based on inputs
		// The 1.0 may need to be deleted.
		// FIXME
		for (Layer layer : Layers)
			layer.scaleFunctions(1.0 * maxOutput * scalarCoefficient);

		int percent = -1;
		if (norm != null)
			norm.normalize(inputs);

		for (int in = 0; in < inputs.length && in < maxInputs; in++) {
			double[][] datapoint = inputs[in];

			// Prints out percentage done with patterns. Echo is set in Network
			if (echo == true && percent != (int) (100 * ((float) in / inputs.length))) {
				percent = (int) (100 * ((float) in / inputs.length));
				System.out.println(percent + "%");
			}

			double[] targets = datapoint[1];
			double[] results;

			maxError = Double.MAX_VALUE;

			// runs through backprop until stopping conditions are met. Stopping
			// conditions are either a max number of iterations or a small
			// error.
			for (int it = 0; maxError > stopError && it < maxIterations; it++) {
				maxError = Double.MAX_VALUE;
				results = run(datapoint[0]);
				backpropagate(targets, results);

				maxError = Math.pow((Math.abs(targets[0] - results[0])), 2);

			}
		}
	}

	/**
	 * The backprop algorithm to be used by the train function. Error is
	 * propogated from the output layer back to the input layer.
	 * 
	 * @param targets
	 *            The target values of the network.
	 * @param results
	 *            The actual results of the network.
	 */
	private void backpropagate(double[] targets, double[] results) {
		int outIndex = (Layers.size() - 1);
		Layer output = Layers.get(outIndex);
		double[][] errors = new double[Layers.size()][];
		double change = 0.0;
		maxError = 0.0;

		// update output layer
		errors[outIndex] = new double[output.size()];
		for (int j = 0; j < output.size(); j++) {
			double diff = (targets[j] - results[j]);

			// grabs errors from gradient of output function
			errors[outIndex][j] = (output.getNeuron(j).gradient() * diff);
			maxError = Math.max(maxError, Math.abs(errors[outIndex][j]));
		}

		// update all hidden layers
		for (int i = outIndex - 1; i > 0; i--) {
			Layer current = Layers.get(i);
			Layer next = Layers.get(i + 1);
			errors[i] = new double[current.size()];
			for (int j = 0; j < current.size(); j++) {
				double sum = 0.0;
				for (int k = 0; k < next.size(); k++) {
					sum += errors[i + 1][k] * next.weights[k][j];
				}
				// add bias
				if (useBias)
					sum += next.weights[next.weights.length - 1][j];
				// TODO: add bias
				errors[i][j] = current.getNeuron(j).gradient() * sum;

				// FIXME
				// I don't think maxError is being used after this -- it may be
				// useless here.
				maxError = Math.max(maxError, Math.abs(errors[i][j]));
			}
		}

		// update all weights using errors
		for (int i = outIndex; i > 0; i--) {
			Layer current = Layers.get(i);
			Layer previous = Layers.get(i - 1);
			for (int j = 0; j < current.size(); j++) {
				for (int k = 0; k < previous.size(); k++) {
					double update = (rate * previous.getNeuron(k).output * errors[i][j]);
					current.weights[j][k] += update + (current.oldWeights[j][k] * momentum);
					current.oldWeights[j][k] = update;
					change = Math.max(change, Math.abs(update));
				}
				// add bias
				if (useBias) {
					int idx = current.weights[j].length - 1;
					current.weights[j][idx] += (rate * errors[i][j]) + (current.oldWeights[j][idx] * momentum);
				}
			}
		}
	}

}
