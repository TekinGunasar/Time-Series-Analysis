import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang.ArrayUtils;
import org.ejml.simple.SimpleMatrix;
import org.json.JSONException;
import java.lang.*;

public class MultipleDimensionalHypothesisFunction {
	
	ArrayList<double[][]> inputMatrices = new ArrayList<>();
	double[][] outputMatrix;
	int numberOfIndividualParameters;
	double[][] designMatrix;
	SimpleMatrix parameterVector;
	int numIVs;
	
	public MultipleDimensionalHypothesisFunction(ArrayList<double[][]> inputMatrices, double[][] outputMatrix, int numberOfIndividualParameters, int numIVs) {
		this.inputMatrices = inputMatrices;
		this.outputMatrix = outputMatrix;
		this.numberOfIndividualParameters = numberOfIndividualParameters;
		this.numIVs = numIVs;
	}
	
	public static double[][] getTimeSeries(int length) {
		double[][] timeSeries = new double[length][1];
		for(int i = 0;i<length;i++) {
			timeSeries[i][0] = i+1;
		}
		return timeSeries;
	}
	
	public static double[][] joinMatrices(double[][] m1, double[][] m2) {
		double[][] result = new double[m1.length][m1[0].length + m2[0].length];
		for(int i = 0;i<m1.length;i++) {
			double[] currentVector = ArrayUtils.addAll(m1[i], m2[i]);
			result[i] = currentVector;
		}
		return result;
	}
	
	 public static double[][] joinMatrices(ArrayList<double[][]> matrices) {
		    while (matrices.size() > 1) {
		      ArrayList<double[][]> currentMatrices = new ArrayList<>();

		      for (int i = 0; i < matrices.size()-1; i += 2) {
		        double[][] m1 = matrices.get(i);
		        double[][] m2 = matrices.get(i + 1);
		        currentMatrices.add(joinMatrices(m1, m2));
		      }

		      matrices = currentMatrices; 
		    }
		    return matrices.get(0);
		  }
	
	public ArrayList<double[][]> generateDesignMatrices() {
		ArrayList<double[][]> designMatrices = new ArrayList<>();
		for(int i = 0;i<inputMatrices.size();i++) {
			double[][] currentInputMatrix = inputMatrices.get(i);
			HypothesisFunction currentHypothesisFunction = new HypothesisFunction(
					currentInputMatrix,outputMatrix,numberOfIndividualParameters);
			currentHypothesisFunction.buildDesignMatrix();
			designMatrices.add(currentHypothesisFunction.designMatrix);
		}
		return designMatrices;
	}
	
	public ArrayList<HypothesisFunction> generateHypothesisFunctions() {
		ArrayList<HypothesisFunction> individualFunctions = new ArrayList<>();
		double[][] timeSeries = getTimeSeries(inputMatrices.get(0).length);
		for(int i = 0;i<inputMatrices.size();i++) {
			double[][] currentOutput = inputMatrices.get(i);
			HypothesisFunction currentHypothesisFunction = new HypothesisFunction(
					timeSeries,currentOutput, numberOfIndividualParameters
					);
			individualFunctions.add(currentHypothesisFunction);
		}
		return individualFunctions;
	}
	
	public ArrayList<double[][]> getFutureInputs(int n) throws IOException {
		ArrayList<HypothesisFunction> allFunctions = generateHypothesisFunctions();
		ArrayList<double[][]> futureInputs = new ArrayList<>();
		int j = 0;
		for(HypothesisFunction h0 : allFunctions) {
			h0.setParameterVector2();
			h0.setAlternateTimeSeries(h0.inputMatrix.length + n);
			double[][] currentPredicted = new double[n][1];
			for(int i = 100;i<100+n;i++) {
				currentPredicted[i-100][0] = h0.HoX(i);
			}
			futureInputs.add(currentPredicted);
			j++;
		}
		return futureInputs;
	}
	
	public void setDesignMatrix() {
		ArrayList<double[][]> individualDesignMatrices = generateDesignMatrices();
		designMatrix = joinMatrices(individualDesignMatrices);
		addOneToBeginning();
	}
	
	public void setParameterVector() {
		setDesignMatrix();
		SimpleMatrix x = new SimpleMatrix(designMatrix);
		SimpleMatrix y = new SimpleMatrix(outputMatrix);
		parameterVector = x.transpose().mult(x).pseudoInverse().mult(x.transpose()).mult(y);
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
	
	public <T> ArrayList<T[]> splitUpParameterVector() {
		ArrayList<T> parameterVector = (ArrayList<T>) parameterVectorToArrayList();
		return chunks(parameterVector,numberOfIndividualParameters);
	}
	
	public ArrayList<Double> parameterVectorToArrayList() {
		ArrayList<Double> result = new ArrayList<>();
		for(int i = 0;i<parameterVector.numRows();i++) {
			result.add(parameterVector.get(i,0));
		}
		return result;
	}
	
	public <T> ArrayList<T[]> chunks(ArrayList<T> bigList,int n){
	    ArrayList<T[]> chunks = new ArrayList<T[]>();

	    for (int i = 1; i < bigList.size(); i += n) {
	        T[] chunk = (T[])bigList.subList(i, Math.min(bigList.size(), i + n)).toArray();         
	        chunks.add(chunk);
	    }
	    return chunks;
	}
	
	public <T> double HoX(double[] inputVector) {
		double result = parameterVector.get(0);
		ArrayList<T[]> chunks = splitUpParameterVector();
		for(int i = 0;i<inputVector.length;i++) {
			T[] currentChunk = chunks.get(i);
			double x_i = inputVector[i];
			for(int j = 1;j<=currentChunk.length;j++) {
				double currentParameter = (double) currentChunk[j-1];
				result += currentParameter * Math.cos(j * x_i);
			}
		}
		return result;
	}
	
	public double[] getInputVector(int n) {
		double[] inputVector = new double[numIVs];
		int index = 0;
		for(int i = 0;i<inputMatrices.size();i++) {
			double[][] currentVector = inputMatrices.get(i);
			for(int j = 0;j<currentVector.length;j++) {
				if(j == n) {
					inputVector[index] = currentVector[j][0];
					index++;
				}
			}
		}
		return inputVector;
	}
	
	public double[] getPreviousPredicted() {
		setParameterVector();
		double[] result = new double[outputMatrix.length];
		for(int i = 0;i<outputMatrix.length;i++) {
			double[] inputVector = getInputVector(i);
			result[i] = HoX(inputVector);
		}
		return result;
	}
	
	public double getAverageError() throws IOException {
		double[] predictedData = getPreviousPredicted();
		double sumOfErrors = 0;
		for(int i = 0;i<outputMatrix.length;i++) {
			sumOfErrors += Math.abs(outputMatrix[i][0]-predictedData[i]);
		}
		return sumOfErrors/outputMatrix.length;
	}
	
	public double[] getFutureInputVector(int n,ArrayList<double[][]> futureInputMatrices) throws IOException {
		double[] inputVector = new double[numIVs];
		int index = 0;
		for(int i = 0;i<futureInputMatrices.size();i++) {
			double[][] currentVector = inputMatrices.get(i);
			for(int j = 0;j<currentVector.length;j++) {
				if(j == n) {
					inputVector[index] = currentVector[j][0];
					index++;
				}
			}
		}
		return inputVector;
	}
	
	public double[] getNext(int n) throws IOException {
		setParameterVector();
		double[] result = new double[n];
		ArrayList<double[][]> futureInputs = getFutureInputs(n);
		ArrayList<double[]> futureInputVectors = new ArrayList<>();
		for(int i = 0;i<n;i++) {
			double[] currentFutureInputVector = getFutureInputVector(i,futureInputs);
			futureInputVectors.add(currentFutureInputVector);
		}
		
		for(int i = 0;i<n;i++) {
			double[] currentInputVector = futureInputVectors.get(i);
			result[i] = HoX(currentInputVector);
		}
		return result;
	}
}
