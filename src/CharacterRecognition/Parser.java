package CharacterRecognition;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.SortedSet;
import java.util.TreeSet;

/*
 * Letter Rec: Letter, followed by 16 attributes (20000 examples in set)
 * Opt Digits: 64 attributes, last one is number (1797 test examples) (3823 train examples)
 * Pen Digits: 16 inputs, last one is number   (attributes have larger range, consider normalizing) (3498 test examples) (7494 train examples)
 *    Semeion: 256 attributes (16 x 16 array of pixels), followed by 10 element vector, digits 0-9   (1593 examples in set) (remember to shuffle)
 * 
 */

/**
 * Parses data in various text file formats.
 *
 */
public abstract class Parser {

	protected BufferedReader reader;
	protected int lines;
	protected double[] categories;
	
	public Parser(String file) throws FileNotFoundException {
		reader = new BufferedReader(new FileReader(file));
		readLines(new FileReader(file));
	}
	
	public int[] getCategories() {
		int[] cats = new int[categories.length];
		for (int i = 0; i < categories.length; i++)
			cats[i] = (int) categories[i];
		return cats;
	}
	
	private void readLines(FileReader fr) {
		LineNumberReader  lnr = new LineNumberReader(fr);
		try {
			lnr.skip(Long.MAX_VALUE);
		} catch (IOException e) {	e.printStackTrace();   }
		lines = lnr.getLineNumber();
	}
	
	protected String getNextLine() throws IOException {
		return reader.readLine();
	}
	
	public double[][][] getData() {
		double[][][] data = new double[lines][2][];
		String line;
		try {
			int i = 0;
			while ((line = this.getNextLine()) != null)
				data[i++] = parseLine(line);
		} catch (IOException e) {	e.printStackTrace();	}
		categorizeOutput(data);
		return data;
	}
	
	private double[] getDistinct(double[][][] data) {
		SortedSet<Double> set = new TreeSet<Double>();
	    for (int i = 0; i < data.length; i++)
	    	set.add(data[i][1][0]);
	    
	    // now convert it into double array
	    Double[] distinct = set.toArray(new Double[]{});
	    double[] vals = new double[distinct.length];
	    for (int i = 0; i < vals.length; i++)
	    	vals[i] = distinct[i];
		return vals;
	}
	
	private void categorizeOutput(double[][][] data) {
		categories = getDistinct(data);
		for (int i = 0; i < data.length; i++) {
			double output = data[i][1][0];
			data[i][1] = new double[categories.length];
			for (int j = 0; j < categories.length; j++) {
				if (output == categories[j])
					data[i][1][j] = 1.0;
				else
					data[i][1][j] = 0.0;
			}
		}
	}
	
	protected abstract double[][] parseLine(String line);

}
