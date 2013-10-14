package FunctionApproximation;

import java.util.Random;

public abstract class Function {
	
	public abstract double getPoint(double[] inputs);
	public abstract double[][][] getUniformSet(double[] starts, double[] ranges, int steps);
	public abstract double[][][] getUniformSet(double[] ranges, int steps);
	protected Random rand = new Random(11235);
	
	/**
	 * Returns a random set of input-output values.
	 * 
	 * @param ranges	The max values that each input may receive.
	 * @param steps     The total number of points that will be returned.
	 */
	public double[][][] getRandomSet(double[] ranges, int points) {
		return getRandomSet(new double[ranges.length], ranges, points);
	}
	
	/**
	 * Returns a random set of input-output values.
	 * 
	 * @param starts    The min values that each input may receive.
	 * @param ranges	The max values that each input may receive.
	 * @param steps     The total number of points that will be returned.
	 */
	public double[][][] getRandomSet(double[] starts, double[] ranges, int points) {
		if (starts.length != ranges.length)
			throw new IllegalArgumentException("Number of starting values and ending values must match.");
		double[][][] set = new double[points][2][];
		for (int i = 0; i < set.length; i++) {
			set[i][0] = new double[starts.length];
			set[i][1] = new double[1];
			for (int j = 0; j < starts.length; j++) {
				set[i][0][j] = starts[j] + (ranges[j] - starts[j]) * rand.nextDouble();
			}
			set[i][1][0] = getPoint(set[i][0]);
		}
		return set;
	}
}
