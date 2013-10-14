package NeuralNetwork;
import java.util.ArrayList;
import java.util.Random;

public class Layer {
	
	private ArrayList<Neuron> Neurons = new ArrayList<Neuron>();
	public Layer parent;
	public double[] output;
	public double[][] weights;  //* weights[toNeuron][fromNeuron] */
	public double[][] oldWeights;
	public boolean useBias = false;
	double weightMin = -0.3;
	double weightMax = 0.3;

	/**
	 * Construct a Layer structure with a specific type and number of neurons.
	 * 
	 * @param neuron		The neuron to be used for the entire layer (copied).
	 * @param numNeurons	The number of neurons to be contained in the layer.
	 * @param parent		The parent layer (null if this is the input layer).
	 */
	Layer(Neuron neuron, int numNeurons, Layer parent) {
		for (int i = 0; i < numNeurons; i++)
			Neurons.add(new Neuron(neuron));
		this.output = new double[this.size()];
		this.parent = parent;
		initializeWeights();
	}
	
	/**
	 * Internal helper function used to initialize weights to small random numbers.
	 */
	private void initializeWeights() {
		Random r = new Random(11235);
		if (parent != null) {
			weights = new double[this.size()][parent.size() + 1];
			oldWeights = new double[this.size()][parent.size() + 1];
			for (int i = 0; i < weights.length; i++) {
				for (int j = 0; j < weights[i].length; j++) {
					weights[i][j] = weightMin + (weightMax - weightMin) * r.nextDouble();
					oldWeights[i][j] = 0.0;
				}
			}
		}
	}
	
	/**
	 * Scales any sigmoidal activation functions or changes the slope if the function is linear.
	 * 
	 * @param scale	Larger values stretch sigmoidal functions to larger width, decimal values compress.
	 */
	public void scaleFunctions(double scale) {
		for (Neuron neuron : Neurons)
			neuron.setScale(scale);
	}
	
	/**
	 * Sets the parent layer of the current layer.
	 * 
	 * @param layer	The layer whose outputs are being received as inputs.
	 */
	public void setParent(Layer layer) {
		this.parent = layer;
		initializeWeights();
	}
	
	/**
	 * Sets whether or not the bias should be used.
	 * 
	 * @param useBias	Boolean value of whether or not to use the bias.
	 */
	public void setUseBias(boolean useBias) {
		this.useBias = useBias;
	}
	
	/**
	 * Runs the inputs through the current layer and returns the outputs.
	 * 
	 * @param inputs	The input values to be passed to each neuron's activation function.
	 * @return			The output values returned by each neuron.
	 */
	public double[] run(double inputs[]) {
		for (int i = 0; i < this.size(); i++) {
			double sum = 0.0;
			if (parent == null) {
				sum = inputs[i];
			} else {
				for (int j = 0; j < inputs.length; j++) {
					sum += weights[i][j] * inputs[j];
				}
			}
			// add bias
			if (useBias && parent != null)
				sum += weights[i][weights[i].length - 1];
			output[i] = Neurons.get(i).fire(sum);
		}
		return output;
	}
	
	/**
	 * Retrieves a specific neuron from 
	 * 
	 * @param i	The index of the neuron to return.
	 * @return	The neuron object requested.
	 */
	public Neuron getNeuron(int i) {
		return Neurons.get(i);
	}
	
	/**
	 * Returns the size of the current layer.
	 * 
	 * @return	The number of neurons contained in the current layer.
	 */
	public int size() {
		return Neurons.size();
	}
	
	/**
	 * Sets all neurons in the current layer to use a specific activation function.
	 * 
	 * @param ft	The activation function type chosen from the FunctionType enumeration.
	 */
	public void setActivationFunction(FunctionType ft) {
		for (Neuron neuron : Neurons)
			neuron.setActivationFunction(ft);
	}
	
}
