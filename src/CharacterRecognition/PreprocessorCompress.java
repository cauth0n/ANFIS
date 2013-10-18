package CharacterRecognition;

public class PreprocessorCompress extends Preprocessor {
	
	int width = 8;	// compress to 8x8 image (64 features)
	
	public PreprocessorCompress(double[][][] data) {
		super(data);
		process();
	}
	
	public void process() {
		
		int inputsize = data[0][0].length;
		int rowsize = (int) Math.sqrt(inputsize);
		double compression = rowsize / width;
		
		for (int exampleNum = 0; exampleNum < processed.length; exampleNum++) {
			processed[exampleNum][0] = new double[width * width];
			processed[exampleNum][1] = data[exampleNum][1];
			
			for (int featureNum = 0; featureNum < inputsize; featureNum++) {
				int row = (int) (featureNum / rowsize);
				int col = featureNum - row * rowsize;
				
				int newrow = (int) (row / compression);
				int newcol = (int) (col / compression);
				
				int index = (newrow * width + newcol);
				processed[exampleNum][0][index] += data[exampleNum][0][featureNum];
			}
			
		}
		
	}

}
