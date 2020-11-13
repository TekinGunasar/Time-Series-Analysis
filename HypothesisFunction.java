import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.ejml.simple.SimpleMatrix;

public class HypothesisFunction {
	
	private double[][] designMatrix;
	public double[] predictedData;
	private double[][] outputMatrix;
	public double[][] inputMatrix;
	private static SimpleMatrix parameterVector;
	private static int numberOfParameters;
	
	public HypothesisFunction(double[][] outputMatrix,int numberOfParameters) {
		this.outputMatrix = outputMatrix;
		this.numberOfParameters = numberOfParameters;
	}
	
	private void setTimeSeries() {
		inputMatrix = new double[outputMatrix.length][1];
		for(int i = 0;i<outputMatrix.length;i++) {
			inputMatrix[i][0] = i+1;
		}
	}
	
	protected void setAlternateTimeSeries(int n) {
		double[][] alternateTimeSeries = new double[n][1];
		for(int i = 0;i<alternateTimeSeries.length;i++) {
			alternateTimeSeries[i][0] = i+1;
		}
		inputMatrix = alternateTimeSeries;
	}
			
	private void setDesignMatrix() {
		setTimeSeries();
		int numberOfRows = inputMatrix.length;
		int numberOfColumns = (numberOfParameters * inputMatrix[0].length);
		designMatrix = new double[numberOfRows][numberOfColumns];
		double[] elements = new double[numberOfRows * numberOfColumns];
		int curIndex = 0;
		
		for(int i = 0;i<inputMatrix.length;i++) {
			double[] curVec = inputMatrix[i];
			for(int j = 0;j<curVec.length;j++) {
				for(int k = 1;k<=numberOfParameters;k++) {
					elements[curIndex] = Math.cos(curVec[j] * k);
					curIndex++;
				}
			} 
		
			int assigningIndex = 0;
			for(int i = 0;i<designMatrix.length;i++) {
				for(int j = 0;j<designMatrix[i].length;j++) {
					designMatrix[i][j] = elements[assigningIndex];
					assigningIndex++;
				}
			}
		}
		int assigningIndex = 0;
		for (int i = 0; i < designMatrix.length; i++) {
			for (int j = 0; j < designMatrix[i].length; j++) {
				designMatrix[i][j] = elements[assigningIndex];
				assigningIndex++;
			}
		}
		addOneToBeginning();
	}
	
	protected void setParameterVector() throws IOException {
		setTimeSeries();
		setDesignMatrix();
		SimpleMatrix x = new SimpleMatrix(designMatrix);
		SimpleMatrix y = new SimpleMatrix(outputMatrix);
		parameterVector = x.transpose().mult(x).pseudoInverse().mult(x.transpose()).mult(y);
	}
	
	public double HoX(double x) throws IOException {
			double currentSum = parameterVector.get(0);
			for(int k = 1;k<numberOfParameters;k++) {
				currentSum += parameterVector.get(k) * Math.cos(k * x);
			}
			return currentSum;
		}
	
	public void setPredicted() throws IOException {
		predictedData = new double[inputMatrix.length];
		for(int i = 0;i<inputMatrix.length;i++) {
			predictedData[i] = HoX(inputMatrix[i][0]);
		}
	}
			
	public double[] getPredicted() throws IOException {
		setPredicted();
		return predictedData;
	}
				
	private void addOneToBeginning() {
		double[][] result = new double[designMatrix.length][designMatrix[0].length+1];
		for(int i = 0;i<result.length;i++) {
			for(int j = 1;j<result[i].length;j++) {
				result[i][j] = designMatrix[i][j-1];
			}
			result[i][0] = 1;
		}
		designMatrix = result;
	}
}