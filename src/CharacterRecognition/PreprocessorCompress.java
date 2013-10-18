package CharacterRecognition;

public class PreprocessorCompress extends Preprocessor {
	
	int width = 8;	// compress to 8x8 image (64 features)
	
	public PreprocessorCompress(double[][][] data) {
		super(data);
	}
	
	public double[][][] getProcessed() {
		int inputsize = data[0][0].length;
		int rowsize = (int) Math.sqrt(inputsize);
		double compression = rowsize / width;
		
		double[][] compressed = new double[width][width];
		
		for (int exampleNum = 0; exampleNum < processed.length; exampleNum++) {
			processed[exampleNum][0] = new double[width * width];
			processed[exampleNum][1] = new double[data[exampleNum][1].length];
			
			for (int featureNum = 0; featureNum < inputsize; featureNum++) {
				int row = (int) (featureNum / rowsize);
				int col = featureNum - row * rowsize;
				
				int newrow = (int) (row / compression);
				int newcol = (int) (col / compression);
				
				compressed[newrow][newcol] += data[exampleNum][0][featureNum];
				int index = (newrow * width + newcol);
				processed[exampleNum][0][index] += data[exampleNum][0][featureNum];
				
				//System.out.println("("+(row+1)+","+(col+1)+")");
			}
			
			for (int i = 0; i < compressed.length; i++) {
				for (int j = 0; j < compressed[i].length; j++) {
					System.out.print((int)compressed[i][j]+" ");
				}
				System.out.println();
			}
			for (int i = 0; i < processed[exampleNum][0].length; i++) {
				System.out.print((int)processed[exampleNum][0][i]+" ");
			}
			
			break;
		}
		
		return processed;
	}

}
