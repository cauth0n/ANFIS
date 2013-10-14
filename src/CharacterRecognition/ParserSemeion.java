package CharacterRecognition;

import java.io.FileNotFoundException;

/**
 * Parses data in various text file formats.
 *
 */
public class ParserSemeion extends Parser {
	
	private String delim = " ";

	public ParserSemeion(String file) throws FileNotFoundException {
		super(file);
	}
	
	protected double[][] parseLine(String line) {
		double[][] data = new double[2][];
		String[] elements = line.split(delim);
		
		// read inputs
		data[0] = new double[elements.length - 1];
		for (int i  = 0; i < elements.length - 10; i++)
			data[0][i] = Double.valueOf(elements[i]);
		
		// read output
		for (int i = 0; i < 10; i++) {
			if (elements[elements.length - 10 + i].length() == 1) {
				//System.out.println(elements[elements.length - 10 + i].charAt(0));
				if (elements[elements.length - 10 + i].charAt(0) == '1') {
					data[1] = new double[]{(double)i};
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
