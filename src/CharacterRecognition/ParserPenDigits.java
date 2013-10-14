package CharacterRecognition;

import java.io.FileNotFoundException;

/**
 * Parses data in various text file formats.
 *
 */
public class ParserPenDigits extends Parser {
	
	private String delim = ",";

	public ParserPenDigits(String file) throws FileNotFoundException {
		super(file);
	}
	
	protected double[][] parseLine(String line) {
		double[][] data = new double[2][];
		String[] elements = line.split(delim);
		
		// read inputs
		data[0] = new double[elements.length - 1];
		for (int i  = 0; i < elements.length - 1; i++)
			data[0][i] = Double.valueOf(elements[i]);
		
		// read output
		elements[elements.length - 1] = elements[elements.length - 1].replaceAll("\\s+","");
		if (elements[elements.length - 1].length() == 1) {
			data[1] = new double[]{(double)elements[elements.length - 1].charAt(0)};
		} else {
			System.out.println("Output must be a single character.");
		}
		
		return data;
	}

}
