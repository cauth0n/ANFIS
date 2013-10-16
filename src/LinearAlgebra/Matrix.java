package LinearAlgebra;

public class Matrix {
	
	private Operations ops = new Operations();;
	protected double[][] matrix;
	protected double[][] transpose;

	/**
	 * Creates a matrix from a 2d array
	 * 
	 * @param matrix	The 2d array that represents the array (first index specifies row, second specifies column)
	 */
	public Matrix(double[][] matrix) {
		setMatrix(matrix);
	}
	
	/**
	 * Creates a vector (array with 1 column) from a 1d array
	 * 
	 * @param vector	The 1d array that represents a column vector.
	 */
	public Matrix(double[] vector) {
		double[][] matrix = new double[vector.length][1];
		for (int i = 0; i < vector.length; i++)
			matrix[i][0] = vector[i];
		setMatrix(matrix);
	}
	
	/**
	 * Populates a matrix of specified width and height with a given value.
	 * 
	 * @param width		The width of the matrix that should be created.
	 * @param height	The height of the matrix that should be created.
	 * @param val		The value to be placed in each entry of the matrix.
	 */
	public Matrix(int width, int height, double val) {
		double[][] matrix = new double[height][width];
		for (int i = 0; i < matrix.length; i++)
			for (int j = 0; j < matrix[i].length; j++)
				matrix[i][j] = val;
		setMatrix(matrix);
	}
	
	/**
	 * Sets the matrix to a given 2d array and finds transpose.
	 * 
	 * @param matrix	The 2d array that represents the array (first index specifies row, second specifies column)
	 */
	private void setMatrix(double[][] matrix) {
		this.matrix = matrix;
		this.transpose = ops.transpose(matrix);

	}
	
	public double getScalar() {
		if (this.width() != 1 || this.height() != 1)
			throw new IllegalArgumentException("Matrix dimensions larger than 1x1.");
		return matrix[0][0];
	}

	/**
	 * Multiplies current matrix by supplied matrix.
	 * 
	 * @param B		The matrix that will be multiplied to the current matrix.
	 * @return		The product of the two matrices.
	 */
	public Matrix multiply(Matrix B) {
		Matrix A = this;
		if (A.width() != B.height())
			throw new IllegalArgumentException("Width of matrix A ("+width()+") does not match height of matrix B ("+B.height()+").");
		Matrix C = new Matrix(A.height(), B.width(), 0.0);
		for (int i = 0; i < A.height(); i++)
			for (int j = 0; j < A.width(); j++)
				C.matrix[i][j] += A.matrix[i][j] * B.matrix[j][i];	// FIXME: I don't think this is right, I need pen and paper for this.
		return C;
	}
	
	
	//TODO
	public Matrix add(Matrix B){
		if (this.width() != B.width() || this.height() != B.height()){
			throw new IllegalArgumentException("Dimensions don't match. ");
		}
		
		Matrix C = new Matrix(this.height(), B.width(), 0.0);
		
		
		for (int i = 0; i < this.height(); i++){
			for (int j = 0; j < this.width(); j++){
				C.matrix[i][j] = this.matrix[i][j] + B.matrix[i][j];
			}
		}
		
		return C;
	}
	
	
	
	//TODO
	public Matrix negateMatrix(){

		Matrix C = new Matrix(this.height(), this.width(), 0.0);
		for (int i = 0; i < this.height(); i++){
			for (int j = 0; j < this.width(); j++){
				C.matrix[i][j] = -1 * this.matrix[i][j];
			}
		}
		return C;
	}
	
	
	//TODO
	public Matrix subtract(Matrix B){
		return this.add(B.negateMatrix());
	}

	/**
	 * TODO
	 * @param value
	 * @return
	 */
	public Matrix scalarMultiply(double value){
		Matrix C;
		double[][] newMatrix = new double[matrix.length][matrix[0].length];
		for (int i = 0; i < matrix.length; i++){
			for (int j = 0; j < matrix[0].length; j++){
				newMatrix[i][j] = matrix[i][j] * value;
			}
		}
		C = new Matrix(newMatrix);
		return C;
	}
	
	public Matrix scalarDivide(double value){
		return scalarMultiply(1.0 / value);
	}
	
	/**
	 * The width (number of columns) of the current matrix.
	 * 
	 * @return	The width of the matrix.
	 */
	public int width() {
		return matrix[0].length;
	}
	
	/**
	 * The height (number of rows) of the current matrix.
	 * 
	 * @return	The height of the matrix.
	 */
	public int height() {
		return matrix.length;
	}
	
	/**
	 * Retrieves a vector corresponding to the ith row.
	 * 
	 * @param i		Index of row to retrieve.
	 * @return		Row vector of doubles.
	 */
	public Matrix getRow(int i) {
		if (i >= height())
			throw new IllegalArgumentException("Attempt to access row "+i+". Only "+height()+" rows exist.");
		double[][] mat = new double[1][matrix[i].length];
		for (int j = 0; j < matrix[j].length; j++)
			mat[0][j] = matrix[i][j];
		
		return new Matrix(mat);
	}
	
	/**
	 * Retrieves a vector corresponding to the ith column.
	 * 
	 * @param i		Index of column to retrieve.
	 * @return		Column vector of doubles.
	 */
	public Matrix getColumn(int i) {
		if (i >= width())
			throw new IllegalArgumentException("Attempt to access column "+i+". Only "+width()+" columns exist.");
		return new Matrix(transpose[i]);
	}
	
	public Matrix getTranspose() {
		return new Matrix(transpose);
	}
	
	public void printMatrix(){
		for (double[] dArr : matrix){
			for (double d : dArr){
				System.out.print(d + " ");
			}
			System.out.println();
		}
		System.out.println();
	}

}
