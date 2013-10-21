package CharacterRecognition;

/**
 * Alters the incoming data in some way.
 *
 */
public abstract class Preprocessor {
	
	// stores the original and processed data structures.
	protected double[][][] data;
	protected double[][][] processed;
	
	/**
	 * Stores the original data and sets up a generic array for the processed data to use.
	 * 
	 * @param data	The original data that will be processed.
	 */
	public Preprocessor(double[][][] data) {
		this.data = data;
		this.processed = new double[data.length][2][];
	}
	
	/**
	 * Retrieves the processed data.
	 * 
	 * @return	The processed data.
	 */
	public double[][][] getProcessed() {
		return processed;
	}

}
