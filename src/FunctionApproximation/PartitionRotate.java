package FunctionApproximation;

public class PartitionRotate extends Partitioner {
	
	private int rotations = 10;
	private int rotation = 0;
	
	/**
	 * Constructs a rotating hold-one-out partitioner.
	 * 
	 * @param set	The dataset to be partitioned.
	 */
	PartitionRotate(double[][][] set) {
		super(set);
	}
	
	/**
	 * Rotates the data so that the next partition in line is held out for testing.
	 * 
	 * @return 	True if a new test set was assigned, False if all partitions have been tested.
	 */
	public boolean nextSet() {
		rotation++;
		if (rotation > rotations)
			return false;
		split();
		return true;
	}
	
	/**
	 * Helper function used internally by the partitioner to 
	 * split datasets based on current rotation value.
	 */
	private void split() {
		int size = (int) (set.length * (1.0 / rotations));
		int splitMax = rotation * size;
		int splitMin = (rotation - 1) * size;
		int trainIt = 0;
		int testIt = 0;
		train = new double[set.length - size][][];
		test  = new double[size][][];
		validate = null;
		for (int i = 0; i < set.length; i++) {
			if (i >= splitMin && i < splitMax)
				test[testIt++] = set[i];
			else
				train[trainIt++] = set[i];
		}
	}

}
