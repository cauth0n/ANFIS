package FunctionApproximation;

public class ConstantFunction extends Function {
	
	/**
	 * Retrieves a point given an input.
	 */
	public double getPoint(double[] inputs) {
		return 5.0;
	}
	
	/**
	 * Returns a uniform set of input-output values.
	 * 
	 * @param ranges	The max values that each input will receive.
	 * @param steps     The number of points that each input is divided into.
	 */
	public double[][][] getUniformSet(double[] ranges, int steps) {
		return getUniformSet(new double[ranges.length], ranges, steps);
	}
	
	/**
	 * Returns a uniform set of input-output values.
	 * 
	 * @param starts    The min values that each input will receive.
	 * @param ranges	The max values that each input will receive.
	 * @param steps     The number of points that each input is divided into.
	 */
	public double[][][] getUniformSet(double[] starts, double[] ranges, int steps) {
		int points = (int)Math.pow(steps+1,ranges.length);
		double[][][] set = new double[points][2][];
		for (int r = 0; r < ranges.length; r++) {
			int index = 0;
			int superset = (int)Math.pow(steps+1, r);
			for (int sup = 0; sup < superset; sup++) {
				for(double x = starts[r]; x <= ranges[r]; x+=((ranges[r]-starts[r])/steps)) {
					x = (double)Math.round(x * 1000000) / 1000000;
					int subset = (int)Math.pow(steps+1,(ranges.length-(r+1)));
					for (int i = 0; i < subset; i++) {
						if (r == 0)
							set[index][0] = new double[ranges.length];
						set[index][0][r] = x;
						if (r == (ranges.length-1)) {
							set[index][1] = new double[1];
							set[index][1][0] = getPoint(set[index][0]);
						}
						index++;
					}
				}
			}
		}
		
		return set;
	}

}
