package muffinc.frog.test.eigenface;
import muffinc.frog.test.Jama.EigenvalueDecomposition;
import muffinc.frog.test.Jama.Matrix;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

class projectedTrainingMatrix {
	Matrix matrix;
	String label;
	double distance = 0;

    public projectedTrainingMatrix(Matrix m, String l) {
		this.matrix = m;
		this.label = l;
	}
}

class ImgMatrix {
    public PCA pca;
    public File file;
    public Matrix matrix;
    private Matrix projected;


    public ImgMatrix(Matrix matrix, File file) {
        this.matrix = matrix;
        this.file = file;
    }

    public ImgMatrix(Matrix matrix, File file, PCA pca) {
        this(matrix, file);
        this.pca = pca;
        projected = pca.project(matrix);
    }

    public void project(PCA pca) {
        if (!isProjected()) {
            projected = pca.project(matrix);
        }
    }

    public boolean isProjected() {
        return projected != null;
    }

    public Matrix getProjected() {
        if (!isProjected()) {
            throw new IllegalAccessError("This Matrix has not been projected");
        } else {
            return projected;
        }
    }
}

public class PCA {
    public static final int FACE_WIDTH = 92;
    public static final int FACE_HEIGHT = 112;
	ArrayList<Matrix> trainingSet;
	ArrayList<String> labels;
	int numOfComponents;
	Matrix meanMatrix;
	// Output
	Matrix W;
	ArrayList<projectedTrainingMatrix> projectedTrainingSet;

	public PCA(ArrayList<Matrix> trainingSet, ArrayList<String> labels,
			   int numOfComponents, Train train) throws Exception {
		
		if(numOfComponents >= trainingSet.size()){
			throw new Exception("the expected dimensions could not be achieved!");
		}
		
		this.trainingSet = trainingSet;
		this.labels = labels;
		this.numOfComponents = numOfComponents;

		this.meanMatrix = getMean(this.trainingSet);
		this.W = getFeature(this.trainingSet, this.numOfComponents);

		// Construct projectedTrainingMatrix
		this.projectedTrainingSet = new ArrayList<projectedTrainingMatrix>();
		for (int i = 0; i < trainingSet.size(); i++) {
			projectedTrainingMatrix ptm = new projectedTrainingMatrix(project(trainingSet.get(i)),
                    labels.get(i));

			this.projectedTrainingSet.add(ptm);
		}
	}

    // calculate projected Matrix
    public Matrix project(Matrix input) {
        return W.transpose().times(input.minus(meanMatrix));
    }

	// extract features, namely W
	private Matrix getFeature(ArrayList<Matrix> input, int K) {
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

        System.out.println(d.length);

		assert d.length >= K : "number of eigenvalues is less than K";
		int[] indexes = this.getIndexesOfKEigenvalues(d, K);

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
		int i;
		for (i = 0; i < d.length; i++)
			mixes[i] = new mix(i, d[i]);

		Arrays.sort(mixes);

		int[] result = new int[k];
		for (i = 0; i < k; i++)
			result[i] = mixes[i].index;
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

	public ArrayList<projectedTrainingMatrix> getProjectedTrainingSet() {
		return this.projectedTrainingSet;
	}
	
	public Matrix getMeanMatrix() {
		// TODO Auto-generated method stub
		return meanMatrix;
	}
	
	public ArrayList<Matrix> getTrainingSet(){
		return this.trainingSet;
	}
	
	public Matrix reconstruct(int whichImage, int dimensions) throws Exception {
		if(dimensions > this.numOfComponents)
			throw new Exception("dimensions should be smaller than the number of components");
		
		Matrix afterPCA = this.projectedTrainingSet.get(whichImage).matrix.getMatrix(0, dimensions-1, 0, 0);
		Matrix eigen = this.getW().getMatrix(0, 10304-1, 0, dimensions - 1);
		return eigen.times(afterPCA).plus(this.getMeanMatrix());
		
	}

}
