package CharacterRecognition;

import java.io.FileNotFoundException;

/**
 * TODO -- Some pre-processing will HAVE to be done here to limit our
 * input data. Otherwise, we are dealing with HUGE amounts of data.
 * 
 */
public class ParserSemeion extends Parser {

	private String delim = " ";

	/**
	 * 
	 * Constructor. Only call is to super.
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 */
	public ParserSemeion(String file) throws FileNotFoundException {
		super(file);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * Parses simeion data. This data has 256 features, and one
	 * target. The target is represented as an array, with a '1'
	 * corresponding to a target of the index the 1 is in.
	 * 
	 * 
	 * @see CharacterRecognition.Parser#parseLine(java.lang.String)
	 */
	protected double[][] parseLine(String line) {
		double[][] data = new double[2][];
		String[] elements = line.split(delim);

		// read inputs. Inputs are every value except for the last 10.
		// This means there are a TON of input features. 256 to be
		// exact.
		data[0] = new double[elements.length - 10];
		for (int i = 0; i < elements.length - 10; i++)
			data[0][i] = Double.valueOf(elements[i]);

		// read output. Output is represented in an array from 0 -->
		// 9, where a '1' in the index means the target is the index's
		// number.
		for (int i = 0; i < 10; i++) {
			if (elements[elements.length - 10 + i].length() == 1) {
				if (elements[elements.length - 10 + i].charAt(0) == '1') {
					data[1] = new double[] { (double) i };
					break;
				} else if (i == 9) {
					System.out.println("No class found in output.");
				}
			} else {
				System.out.println("Output must be a single character.");
			}
		}
		return data;
	}

}
