package NeuralNetwork;

public class Neuron {
	
	public double input;
	public double output;
	private FunctionType activation;
	public double weightChange;
	private double scale = 1.0;
	
	/**
	 * A constructor for the neuron object which sets a default function type of linear.
	 */
	Neuron() {
		activation = FunctionType.LINEAR;
	}
	
	/**
	 * A copy constructor for a neuron object.
	 * 
	 * @param neuron	The neuron to create a copy from.
	 */
	Neuron(Neuron neuron) {
		this.activation = neuron.activation;
	}
	
	/**
	 * A constructor for the neuron object which sets the activation function as specified.
	 * 
	 * @param ft	The function type to use for the new neuron.
	 */
	Neuron(FunctionType ft) {
		activation = ft;
	}
	
	/**
	 * Sets the activation function for the current neuron.
	 * 
	 * @param ft	The function type to use for the activation function.
	 */
	public void setActivationFunction(FunctionType ft) {
		activation = ft;
	}
	
	/**
	 * Runs an input through the current neurons activation function.
	 * 
	 * @param input	The input value sent to the activation function.
	 * @return		The output value returned from the activation function.
	 */
	public double fire(double input) {
		this.input = input;
		output = Float.MIN_VALUE;
		switch (activation) {
		case LOGISTIC:
			output = logisticFunction(input);
			break;
		case TANH:
			output = tanhFunction(input);
			break;
		case LINEAR:
			output = linearFunction(input);
			break;
		default:
			throw new IllegalArgumentException("Cannot pass single argument to activation function "+activation+".");
		}
		return output;
	}
	
	/**
	 * Runs an input through the current neurons activation function.
	 * 
	 * @param input	The input value sent to the activation function.
	 * @param sigma	The sigma value sent along with the input (used for radial basis functions)
	 * @return		The output value returned from the activation function.
	 */
	public double fire(double input, double sigma) {
		output = Float.MIN_VALUE;
		switch (activation) {
		case GAUSSIAN:
			output = gaussianFunction(input, sigma);
			break;
		default:
			throw new IllegalArgumentException("Cannot pass second argument to activation function "+activation+".");
		}
		
		return output;
	}
	
	/**
	 * The gradient of the selected activation function.
	 * 
	 * @return	The result of the gradient when passed the same input as the activation function.
	 */
	public double gradient() {
		double out = Float.MIN_VALUE;
		double value = input;
		
		switch (activation) {
		case LOGISTIC:
			out = logisticFunctionPrime(value);
			break;
		case TANH:
			out = tanhFunctionPrime(value);
			break;
		case LINEAR:
			out = linearFunctionPrime(value);
			break;
		default:
			throw new IllegalArgumentException("Cannot call gradient on activation function "+activation+".");
		}
		return out;
	}
	
	/**
	 * A Gaussian activation function used for radial basis.
	 * 
	 * @param input	The input to the function.
	 * @param sigma	The scale of the Gaussian curve.
	 * @return		The value returned by the function.
	 */
	double gaussianFunction(double input, double sigma) {
		return Math.exp(-Math.pow(input, 2) / (2 * Math.pow(sigma, 2)));
	}
	
	/**
	 * A logistic sigmoidal activation function.
	 * 
	 * @param input	The input to the function.
	 * @return		The value returned by the function.
	 */
	double logisticFunction(double input) {
		return 1/(1 + Math.pow(Math.E,-input));
	}
	
	/**
	 * The derivative to the logistic function.
	 * 
	 * @param input	The input to the function.
	 * @return		The value returned by the function.
	 */
	double logisticFunctionPrime(double input) {
		return (logisticFunction(input)/(1-logisticFunction(input)));
	}
	
	/**
	 * A hyperbolic tangent sigmoidal activation function.
	 * 
	 * @param input	The input to the function.
	 * @return		The value returned by the function.
	 */
	double tanhFunction(double input) {
		input /= scale;
		return ((Math.pow(Math.E, input) - Math.pow(Math.E, -input)) / (Math.pow(Math.E, input) + Math.pow(Math.E, -input)));
	}
	
	/**
	 * The derivative to the hyperbolic tangent function.
	 * 
	 * @param input	The input to the function.
	 * @return		The value returned by the function.
	 */
	double tanhFunctionPrime(double input) {
		return (1.0 - Math.pow(tanhFunction(input), 2)) / scale;
	}
	
	/**
	 * A linear activation function.
	 * 
	 * @param input	The input to the function.
	 * @return		The value returned by the function.
	 */
	double linearFunction(double input) {
		return scale*input;
	}
	
	/**
	 * The derivative to the linear function.
	 * 
	 * @param input	The input to the function.
	 * @return		The value returned by the function.
	 */
	double linearFunctionPrime(double input) {
		return scale;
	}
	
	/**
	 * Scales any sigmoidal activation function or changes the slope if the function is linear.
	 * 
	 * @param scale	Larger values stretch sigmoidal functions to larger width, decimal values compress.
	 */
	public void setScale(double scale) {
		this.scale = scale;
	}
	
	/**
	 * Retrieves the activation function type for the current neuron.
	 * 
	 * @return	The activation function type from the FunctionType enumeration.
	 */
	public FunctionType getFunctionType() {
		return this.activation;
	}

}