package CharacterRecognition;

import java.io.FileNotFoundException;

/**
 * Parses data in various text file formats.
 *
 */
public class ParserLetterRecognition extends Parser {
	
	private String delim = ",";

	public ParserLetterRecognition(String file) throws FileNotFoundException {
		super(file);
	}
	
	protected double[][] parseLine(String line) {
		double[][] data = new double[2][];
		String[] elements = line.split(delim);
		
		// read inputs
		data[0] = new double[elements.length - 1];
		for (int i  = 1; i < elements.length; i++)
			data[0][i - 1] = Double.valueOf(elements[i]);
		
		// read output
		if (elements[0].length() == 1) {
			data[1] = new double[]{(double)elements[0].charAt(0)};
		} else {
			System.out.println("Output must be a single character.");
		}
		
		return data;
	}

}
