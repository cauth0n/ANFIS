package CharacterRecognition;

import java.io.FileNotFoundException;

/**
 * Parses letter recognition data -- Data is of form [<target>, <f1>,
 * <f2>, ... <f16>] where <fn> is the nth feature. There are 16
 * features and 1 target
 * 
 */
public class ParserLetterRecognition extends Parser {

	private String delim = ",";

	/**
	 * 
	 * Constructor -- Only call is to super.
	 * 
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 */
	public ParserLetterRecognition(String file) throws FileNotFoundException {
		super(file);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * 
	 * Overriding method call. Parses the letter recognition data set.
	 * 
	 * @see CharacterRecognition.Parser#parseLine(java.lang.String)
	 */
	protected double[][] parseLine(String line) {
		double[][] data = new double[2][];
		String[] elements = line.split(delim);

		// read inputs. These values are all after the first index
		// (indices > 1)
		data[0] = new double[elements.length - 1];
		for (int i = 1; i < elements.length; i++)
			data[0][i - 1] = Double.valueOf(elements[i]);

		// read output. This is the first index.
		if (elements[0].length() == 1) {
			data[1] = new double[] { (double) elements[0].charAt(0) };
		} else {
			System.out.println("Output must be a single character.");
		}

		return data;
	}

}
