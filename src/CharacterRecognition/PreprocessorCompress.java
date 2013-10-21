package CharacterRecognition;

/**
 * This preprocessor takes features in the form of pixels and
 * merges physically close pixels within an image together in
 * order to reduce the number of features.
 *
 */
public class PreprocessorCompress extends Preprocessor {
	
	// width/height of the new image
	int width = 8;	// compress to 8x8 image (64 features)
	
	/**
	 * Immediately preprocess data once constructed.
	 * 
	 * @param data	The data to preprocess in the standard form used by this package
	 */
	public PreprocessorCompress(double[][][] data) {
		super(data);
		process();
	}
	
	/**
	 * Compresses pixels that are close to each other into
	 * a single pixel value. No averaging is done, simply a
	 * straight summation of the combined pixels.
	 */
	public void process() {
		
		// initialize some variables
		int inputsize = data[0][0].length;
		int rowsize = (int) Math.sqrt(inputsize);
		double compression = rowsize / width;
		
		// loop through all the examples in the data
		for (int exampleNum = 0; exampleNum < processed.length; exampleNum++) {
			
			// initialize arrays to correct (smaller) values
			processed[exampleNum][0] = new double[width * width];
			processed[exampleNum][1] = data[exampleNum][1];
			
			
			// loop through each feature in the current example
			for (int featureNum = 0; featureNum < inputsize; featureNum++) {
				
				// get the current row that this feature represents
				int row = (int) (featureNum / rowsize);
				int col = featureNum - row * rowsize;
				
				// map to the new feature location
				// note: there are less new feature locations than old feature locations, thus compression
				int newrow = (int) (row / compression);
				int newcol = (int) (col / compression);
				
				// add the current feature to the correct new feature location
				int index = (newrow * width + newcol);
				processed[exampleNum][0][index] += data[exampleNum][0][featureNum];
			}
			
		}
		
	}

}
