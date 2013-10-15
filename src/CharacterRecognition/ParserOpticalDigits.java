package CharacterRecognition;

import java.io.FileNotFoundException;

/**
 * Parses optical digits data -- Data is of form [<f1>, <f2>, ...
 * <f64>, <target>] where <fn> is the nth feature. There are 64
 * features and 1 target.
 * 
 */
public class ParserOpticalDigits extends Parser {

	private String delim = ",";

	/**
	 * 
	 * 
	 * 
	 * Constructor. Only call is to super.
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 */
	public ParserOpticalDigits(String file) throws FileNotFoundException {
		super(file);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * 
	 * Parses through data, line by line.
	 * 
	 * 
	 * @see CharacterRecognition.Parser#parseLine(java.lang.String)
	 */
	protected double[][] parseLine(String line) {
		double[][] data = new double[2][];
		String[] elements = line.split(delim);

		// read inputs. These elements make up all except the last
		// character in the text file.
		data[0] = new double[elements.length - 1];
		for (int i = 0; i < elements.length - 1; i++)
			data[0][i] = Double.valueOf(elements[i]);

		// read output. This is the last element in the file.
		if (elements[elements.length - 1].length() == 1) {
			data[1] = new double[] { (double) elements[elements.length - 1].charAt(0) };
		} else {
			System.out.println("Output must be a single character.");
		}

		return data;
	}

}
