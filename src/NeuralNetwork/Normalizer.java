package NeuralNetwork;

public class Normalizer {
	
	protected double[][][] set;
	protected double minVal, maxVal;
	
	/**
	 * Normalizes a set's inputs to a specific range.
	 * 
	 * @param set	The set of input-output examples.
	 */
	public void normalize(double[][][] set) {
		normalize(set, true);
	}
	
	/**
	 * Normalizes a set's inputs and possibly outputs to a specific range.
	 * 
	 * @param set		The set of input-output examples.
	 * @param output	Whether or not the outputs should be normalized.
	 */
	public void normalize(double[][][] set, boolean output) {
		minVal = 0.0;
		maxVal = 0.0;
		// find max and min vals
		for (double[][] input : set) {
			for (int j = 0; j < input[1].length; j++) {
				maxVal = Math.max(maxVal, input[1][j]);
				minVal = Math.min(minVal, input[1][j]);
			}
		}
		// normalize
		for (int i = 0; i < set.length; i++) {
			// inputs
			for (int j = 0; j < set[i][0].length; j++)
				set[i][0][j] = normalize(set[i][0][j]);
			// outputs
			if (output)
				for (int j = 0; j < set[i][1].length; j++)
					set[i][1][j] = normalize(set[i][1][j]);
		}
	}
	
	/**
	 * Normalizes a specific value to the range -5.0 to 5.0
	 * 
	 * @param input	The set of input-output examples.
	 * @return		The normalized output value.
	 */
	public double normalize(double input) {
		return (((input - minVal) / (maxVal - minVal) - 0.5 ) * 10);
	}
	
	/**
	 * Reverses the normalization process.
	 * 
	 * @param input	The set of input-output examples.
	 * @return		The denormalized result.
	 */
	public double denormalize(double input) {
		return (input / 2 + 0.5) * (maxVal - minVal) + minVal;
	}
	
}
