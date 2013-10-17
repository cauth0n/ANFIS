package CharacterRecognition;

import LinearAlgebra.Matrix;

public class MatrixTest {

	public static void main(String[] args) {
		
		double[][] a = new double[][]{{1,2,3},{4,5,6},{7,8,9}};
		double[][] b = new double[][]{{9,8,7},{6,5,4},{3,2,1}};
		
		Matrix A = new Matrix(a);
		Matrix B = new Matrix(b);
		
		Matrix C = A.multiply(B);
		Matrix D = A.subtract(B);
		Matrix E = A.scalarDivide(5);
		
		System.out.println("A");
		A.printMatrix();
		System.out.println("B");
		B.printMatrix();
		
		A.setColumn(0, B.getColumn(0));
		
		
		A.setColumn(1, B.getColumn(1));
		//.printMatrixMatlab();
		A.printMatrix();
		
		
	}

}
