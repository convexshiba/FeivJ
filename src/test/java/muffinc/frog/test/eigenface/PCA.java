package muffinc.frog.test.eigenface;
import muffinc.frog.test.Jama.EigenvalueDecomposition;
import muffinc.frog.test.Jama.Matrix;
import muffinc.frog.test.Jama.util.JamaUtils;
import muffinc.frog.test.common.Metric;
import muffinc.frog.test.eigenface.metric.EuclideanDistance;
import muffinc.frog.test.object.ImgMatrix;
import muffinc.frog.test.object.Human;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

public class PCA {
    public static final int FACE_WIDTH = 92;
    public static final int FACE_HEIGHT = 112;
	ArrayList<Matrix> trainingSet;
	ArrayList<String> labels;
	int numOfComponents;
	Matrix meanMatrix;
	// Output
	Matrix W;
	ArrayList<ImgMatrix> projectedTrainingSet;
    TrainingEngine trainingEngine;

//    public double infoRetainRatio;
    private BigDecimal sumOfEigenVal = BigDecimal.ZERO;
    private BigDecimal sumOfSelected = BigDecimal.ZERO;

    public PCA(ArrayList<ImgMatrix> trainingImg, int numOfComponents, TrainingEngine trainingEngine) throws Exception {

		if(numOfComponents >= trainingImg.size()){
			throw new Exception("the expected dimensions could not be achieved!");
		}

        trainingSet = new ArrayList<Matrix>();
        labels = new ArrayList<String>();

        for (ImgMatrix imgMatrix : trainingImg) {
            trainingSet.add(imgMatrix.getVectorized());
            labels.add(imgMatrix.human.name);
        }

		this.numOfComponents = numOfComponents;
        this.trainingEngine = trainingEngine;

		this.meanMatrix = getMean(this.trainingSet);
		this.W = getFeature(this.trainingSet, this.numOfComponents);

		// Construct projectedTrainingMatrix
		this.projectedTrainingSet = new ArrayList<ImgMatrix>();
		for (int i = 0; i < trainingSet.size(); i++) {
//			ImgMatrix ptm = new ImgMatrix(project(trainingSet.get(i)), labels.get(i));
            trainingImg.get(i).setProjectedVector(project(trainingImg.get(i).getVectorized()));

            // setIsFace for trainingImg
//            setAndReturnIsFace(trainingImg.get(i));

//			this.projectedTrainingSet.add(tr);
		}
        projectedTrainingSet = trainingImg;

        // project testImg
        for (ImgMatrix imgMatrix : trainingEngine.testingImgSet) {
            imgMatrix.setProjectedVector(project(imgMatrix.getVectorized()));

            // setIsFace for testImg
//            setAndReturnIsFace(imgMatrix);

        }

        for (Human p : trainingEngine.humanFactory.nameTable.values()) {
            p.calculateID();
        }


    }

	@Deprecated
	public PCA(ArrayList<Matrix> trainingSet, ArrayList<String> labels,
			   int numOfComponents, TrainingEngine trainingEngine) throws Exception {

		if(numOfComponents >= trainingSet.size()){
			throw new Exception("the expected dimensions could not be achieved!");
		}

		this.trainingSet = trainingSet;
		this.labels = labels;
		this.numOfComponents = numOfComponents;

		this.meanMatrix = getMean(this.trainingSet);
		this.W = getFeature(this.trainingSet, this.numOfComponents);

		// Construct projectedTrainingMatrix
		this.projectedTrainingSet = new ArrayList<ImgMatrix>();
		for (int i = 0; i < trainingSet.size(); i++) {
//			ImgMatrix ptm = new ImgMatrix(project(trainingSet.get(i)), labels.get(i));

//            ImgMatrix ptm = new ImgMatrix()
//			this.projectedTrainingSet.add(ptm);
		}
	}

    // calculate projected Matrix
    public Matrix project(Matrix input) {
        return W.transpose().times(input.minus(meanMatrix));
    }

    // returns the reconstructed Matrix as a col Matrix
	public Matrix reconstruct(Matrix idMatrix) {
//        Matrix reconstedMatrix = new Matrix(FACE_HEIGHT * FACE_WIDTH, 1);
//        double[] eigenVals = idMatrix.getRowPackedCopy();
//
//        for (int i = 0; i < getW().getColumnDimension(); i++) {
//            reconstedMatrix.plusEquals(getW().getMatrix(0, getW().getRowDimension() - 1, i, i)).times(eigenVals[i]);
//        }
//        return reconstedMatrix.plus(getMeanMatrix());

//        Matrix weights = idMatrix;
        Matrix eigen = this.getW().getMatrix(0, FACE_HEIGHT * FACE_WIDTH - 1, 0, TrainingEngine.COMPONENT_NUMBER - 1);
        return eigen.times(idMatrix).plus(this.getMeanMatrix());

    }

    public boolean setAndReturnIsFace(ImgMatrix imgMatrix) {
        Matrix reconstructed = reconstruct(imgMatrix.getProjectedVector());
        Matrix adjustedInput = imgMatrix.getVectorized().minus(getMeanMatrix());
        Metric metric = new EuclideanDistance();
        imgMatrix.setIsFace(metric.getDistance(reconstructed, imgMatrix.getProjectedVector()) > TrainingEngine.IS_FACE_THRESHOLD);
        return imgMatrix.isFace();
    }

	// extract features, namely W
	private Matrix getFeature(ArrayList<Matrix> input, int componantNum) {
		int i, j;

		int row = input.get(0).getRowDimension();
		int column = input.size();
		Matrix X = new Matrix(row, column);

		for (i = 0; i < column; i++) {
			X.setMatrix(0, row - 1, i, i, input.get(i).minus(this.meanMatrix));
		}

		// get eigenvalues and eigenvectors
		Matrix XT = X.transpose();
		Matrix XTX = XT.times(X);
		EigenvalueDecomposition feature = XTX.eig();
		double[] d = feature.getd();


		assert d.length >= componantNum : "number of eigenvalues is less than componantNum";
		int[] indexes = this.getIndexesOfKEigenvalues(d, componantNum);

		Matrix eigenVectors = X.times(feature.getV());
		Matrix selectedEigenVectors = eigenVectors.getMatrix(0,
				eigenVectors.getRowDimension() - 1, indexes);

		// normalize the eigenvectors
		row = selectedEigenVectors.getRowDimension();
		column = selectedEigenVectors.getColumnDimension();
		for (i = 0; i < column; i++) {
			double temp = 0;
			for (j = 0; j < row; j++)
				temp += Math.pow(selectedEigenVectors.get(j, i), 2);
			temp = Math.sqrt(temp);

			for (j = 0; j < row; j++) {
				selectedEigenVectors.set(j, i, selectedEigenVectors.get(j, i) / temp);
			}
		}

		return selectedEigenVectors;

	}

	// get the first K indexes with the highest eigenValues
	private class mix implements Comparable {
		int index;
		double value;

		mix(int i, double v) {
			index = i;
			value = v;
		}

		public int compareTo(Object o) {
			double target = ((mix) o).value;
			if (value > target)
				return -1;
			else if (value < target)
				return 1;

			return 0;
		}
	}

	private int[] getIndexesOfKEigenvalues(double[] d, int k) {
		mix[] mixes = new mix[d.length];
		for (int i = 0; i < d.length; i++)
			mixes[i] = new mix(i, d[i]);

		Arrays.sort(mixes);

		int[] result = new int[k];
		for (int i = 0; i < k; i++) {
            result[i] = mixes[i].index;

            // calculate sumOfSelected
            sumOfSelected = sumOfSelected.add(BigDecimal.valueOf(mixes[i].value));
        }

        // calculate sumOfEigenVal
        sumOfEigenVal = sumOfEigenVal.add(sumOfSelected);
        for (int i = k; i < mixes.length; i++) {
            sumOfEigenVal = sumOfEigenVal.add(BigDecimal.valueOf(mixes[i].value));
        }

		return result;
	}

	// The matrix has already been vectorized
	private static Matrix getMean(ArrayList<Matrix> input) {
		int rows = input.get(0).getRowDimension();
		int length = input.size();
		Matrix all = new Matrix(rows, 1);

		for (int i = 0; i < length; i++) {
			all.plusEquals(input.get(i));
		}

		return all.times((double) 1 / length);
	}

	public Matrix getW() {
		return this.W;
	}

	public ArrayList<ImgMatrix> getProjectedTrainingSet() {
		return this.projectedTrainingSet;
	}
	
	public Matrix getMeanMatrix() {
		// TODO Auto-generated method stub
		return meanMatrix;
	}
	
	public ArrayList<Matrix> getTrainingSet(){
		return this.trainingSet;
	}

    public double getInfoRatio() {
//        System.out.println("getInfoRatie: " + sumOfSelected);
//        System.out.println("getInfoRatie: " + sumOfEigenVal);
//        System.out.println(Arrays.toString(d));

        return sumOfSelected.divide(sumOfEigenVal, BigDecimal.ROUND_CEILING).doubleValue();
    }
	
	public Matrix reconstruct(int whichImage, int dimensions) throws Exception {
		if(dimensions > this.numOfComponents)
			throw new Exception("dimensions should be smaller than the number of components");
		
		Matrix afterPCA = this.projectedTrainingSet.get(whichImage).getProjectedVector().getMatrix(0, dimensions - 1, 0, 0);
		Matrix eigen = this.getW().getMatrix(0, 10304-1, 0, dimensions - 1);
		return eigen.times(afterPCA).plus(this.getMeanMatrix());
		
	}

}
