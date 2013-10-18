package CharacterRecognition;

public abstract class Preprocessor {
	
	protected double[][][] data;
	protected double[][][] processed;
	
	public Preprocessor(double[][][] data) {
		this.data = data;
		this.processed = new double[data.length][2][];
	}
	
	public double[][][] getProcessed() {
		return processed;
	}

}
