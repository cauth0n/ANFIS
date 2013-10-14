package FunctionApproximation;

public abstract class Partitioner {
	
	public double[][][] train;
	public double[][][] test;
	public double[][][] validate;
	protected double[][][] set;
	
	/**
	 * Creates a partitioner for a particular set of data.
	 * 
	 * @param set	The dataset to partition.
	 */
	Partitioner(double[][][] set) {
		this.set = set;
		train = null;
		test = null;
		validate = null;
	}
	
	/**
	 * 
	 * @return	The train data for the set
	 */
	public double[][][] getTrain() {
		return train;
	}
	
	/**
	 * 
	 * @return	The test data for the set
	 */
	public double[][][] getTest() {
		return test;
	}
	
	/**
	 * 
	 * @return	The validation data for the set
	 */
	public double[][][] getValidate() {
		return validate;
	}
	
	/**
	 * Repartitions the data such that a new test set is chosen.
	 * 
	 * @return	True if another partition has been created, False if partitioner is finished.
	 */
	abstract boolean nextSet();
	
}
