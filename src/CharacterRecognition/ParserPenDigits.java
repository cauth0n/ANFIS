package CharacterRecognition;

import java.io.FileNotFoundException;

/**
 * Parses optical digits data -- Data is of form [<f1>, <f2>, ...
 * <f16>, <target>] where <fn> is the nth feature. There are 16
 * features and 1 target.
 * 
 */
public class ParserPenDigits extends Parser {

	private String delim = ",";

	/**
	 * 
	 * Constructor. Only call is to super.
	 * 
	 * @param file
	 * @throws FileNotFoundException
	 */
	public ParserPenDigits(String file) throws FileNotFoundException {
		super(file);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * 
	 * Parses through data, line by line.
	 * 
	 * @see CharacterRecognition.Parser#parseLine(java.lang.String)
	 */
	protected double[][] parseLine(String line) {
		double[][] data = new double[2][];
		String[] elements = line.split(delim);

		// read inputs. Every value except the last value are
		// features.
		data[0] = new double[elements.length - 1];
		for (int i = 0; i < elements.length - 1; i++)
			data[0][i] = Double.valueOf(elements[i]);

		// read output. The output will be the last index in the file.
		elements[elements.length - 1] = elements[elements.length - 1].replaceAll("\\s+", "");
		if (elements[elements.length - 1].length() == 1) {
			data[1] = new double[] { (double) elements[elements.length - 1].charAt(0) };
		} else {
			System.out.println("Output must be a single character.");
		}

		return data;
	}

}
