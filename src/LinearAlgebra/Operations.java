package LinearAlgebra;

public class Operations {

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
