package CharacterRecognition;

public class PartitionOnce extends Partitioner {
	
	double testSize = 0.25;
	
	/**
	 * Constructs a rotating hold-one-out partitioner.
	 * 
	 * @param set	The dataset to be partitioned.
	 */
	PartitionOnce(double[][][] set) {
		super(set);
		split();
	}
	
	/**
	 * Rotates the data so that the next partition in line is held out for testing.
	 * 
	 * @return 	True if a new test set was assigned, False if all partitions have been tested.
	 */
	public boolean nextSet() {
		return false;
	}
	
	/**
	 * Helper function used internally by the partitioner to 
	 * split datasets based on current rotation value.
	 */
	private void split() {
		int split = (int) (set.length * testSize);
		int trainIt = 0;
		int testIt = 0;
		train = new double[set.length - split][][];
		test  = new double[split][][];
		validate = null;
		for (int i = 0; i < set.length; i++) {
			if (i >= split)
				train[trainIt++] = set[i];
			else
				test[testIt++] = set[i];
		}
	}

}
