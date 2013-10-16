package LinearAlgebra;

public class Operations {
	
	public double[][] getIdentity(int size) {
		double[][] ident = new double[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (i == j)
					ident[i][j] = 1.0;
				else
					ident[i][j] = 0.0;
			}
		}
		return ident;
	}
	
	/**
	 * Transposes an MxN matrix.
	 * 
	 * @param matrix	The matrix to be transposed.
	 * @return			The new, transposed matrix.
	 */
	public double[][] transpose(double[][] matrix) {
		int m = matrix.length;
		int n = matrix[0].length;
		double[][] transposed = new double[n][m];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < m; j++)
				transposed[i][j] = matrix[j][i];
		return matrix;
	}
	
	public double[] getRow(double[][] matrix, int i) {
		return matrix[i];
	}
	
	public double[] getColumn(double[][] matrix, int i) {
		return transpose(matrix)[i];	// inefficient, don't care for now. not even sure if it will be used.
	}
	
	/**
	 * Returns the maximum distance between a set of vectors.
	 * 
	 * @param vectors	An array of vectors.
	 * @return			The maximum distance between two vectors in the set.
	 */
	public double maxDistance(double[][] vectors) {
		// note: should use an enum to select norm function?
		double dmax = 0.0;
		for (int i = 0; i < vectors.length - 1; i++) {
			for (int j = i + 1; j < vectors.length; j++) {
				dmax = Math.max(dmax, euclidean(vectors[i], vectors[j]));
			}
		}
		return dmax;
	}

	/**
	 * The default norm of two vectors (euclidean). 
	 * 
	 * @param X	The x vector.
	 * @param Y	The y vector.
	 * @return	The normed vector.
	 */
	public double norm(double[] X, double[] Y) {
		return euclidean(X, Y);
	}
	
	/**
	 * The manhattan norm of two vectors. 
	 * 
	 * @param X	The x vector.
	 * @param Y	The y vector.
	 * @return	The normed vector.
	 */
	public double manhattan(double[] X, double[] Y) {
		return LPNorm(X, Y, 1);
	}
	
	/**
	 * The euclidean norm of two vectors.
	 * 
	 * @param X	The x vector.
	 * @param Y	The y vector.
	 * @return	The normed vector.
	 */
	public double euclidean(double[] X, double[] Y) {
		return LPNorm(X, Y, 2);
	}
	
	/**
	 * The max coordinant norm of two vectors.
	 * 
	 * @param X	The x vector.
	 * @param Y	The y vector.
	 * @return	The normed vector.
	 */
	public double maxCoordDist(double[] X, double[] Y) {
		double max = 0.0;
		for (double x : X)
			for (double y : Y)
				max = Math.max(max, Math.abs(x - y));
		return max;
	}
	
	/**
	 * A p-norm of two vectors.
	 * 
	 * @param X	The x vector.
	 * @param Y	The y vector.
	 * @param p The p value of the norm to be taken.
	 * @return	The normed vector.
	 */
	protected double LPNorm(double[] X, double[] Y, int p) {
		double sum = 0.0;
		for (int i = 0; i < X.length; i++)
			sum += Math.pow((X[i] - Y[i]), p);
		sum = Math.pow(sum, 1.0/p);
		return sum;
	}

}
