package LinearAlgebra;

public class Matrix {
	
	private Operations ops;
	protected double[][] matrix;
	protected double[][] transpose;

	Matrix(double[][] matrix) {
		ops = new Operations();
		this.matrix = matrix;
		this.transpose = ops.transpose(matrix);
	}
	
	public double[] getRow(int i) {
		return matrix[i];
	}
	
	public double[] getColumn(int i) {
		return transpose[i];
	}

}
