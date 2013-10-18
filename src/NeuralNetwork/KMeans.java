package NeuralNetwork;

import LinearAlgebra.Operations;

public class KMeans {
	
	int maxIterations = 100;
	double maxError = 0.00001;
	Operations ops = new Operations();
	double[][] means;
	
	KMeans(double[][] data, int numClusters) {
		
		// store first "numClusters" examples as initial mean values
		means = new double[numClusters][];
		for (int meanNum = 0; meanNum < means.length; meanNum++) {
			means[meanNum] = data[meanNum];
		}
		
		double error = Double.MAX_VALUE;
		int iterations = 0;
		while (error > maxError && iterations < maxIterations) {
		
			// calculate new clusters
			double[][] clusters = new double[numClusters][data[0].length];
			int[] clusterCount = new int[numClusters];
			for (int exampleNum = 0; exampleNum < data.length; exampleNum++) {
				
				double[] example = data[exampleNum];
				
				// find closest mean to the current point
				double minDistance = Integer.MAX_VALUE;
				int cluster = 0;
				for (int meanNum = 0; meanNum < means.length; meanNum++) {
					double distance = ops.euclidean(example, means[meanNum]);
					if (distance < minDistance) {
						cluster = meanNum;
						minDistance = distance;
					}
				}
				
				// add current point to closest cluster
				clusterCount[cluster]++;
				for (int exampleElement = 0; exampleElement < example.length; exampleElement++) {
					clusters[cluster][exampleElement] += example[exampleElement];
				}
				
			}
			
			// find mean of each new cluster
			error = 0.0;
			for (int meanNum = 0; meanNum < means.length; meanNum++) {
				for (int clusterElement = 0; clusterElement < clusters[meanNum].length; clusterElement++) {
					if (clusterCount[meanNum] > 0) {
						double newElement = clusters[meanNum][clusterElement] / clusterCount[meanNum];
						error = Math.max(error, Math.pow(newElement - means[meanNum][clusterElement], 2));
						means[meanNum][clusterElement] = newElement;
					} else {
						means[meanNum][clusterElement] += 1; // offset to one if overlapping
					}
				}
			}
			
			iterations++;
		
		}
		
	}
	
	public double[][] getMeans() {
		return means;
	}
	
}
