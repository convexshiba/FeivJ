package muffinc.frog.eigenface;
import muffinc.frog.Jama.EigenvalueDecomposition;
import muffinc.frog.Jama.Matrix;
import muffinc.frog.displayio.Display;
import muffinc.frog.eigenface.metric.EuclideanDistance;
import muffinc.frog.helper.ImageHelper;
import muffinc.frog.object.FrogTrainImg;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

public class PCA {
    public static final int FACE_WIDTH = 92;
    public static final int FACE_HEIGHT = 112;
	ArrayList<Matrix> trainingSet;

	@Deprecated
	ArrayList<String> labels;

	int numOfComponents;
	Matrix meanMatrix;
	// Output
	Matrix W;
	ArrayList<FrogTrainImg> projectedTrainingSet;
    TrainingEngine trainingEngine;

//    public double infoRetainRatio;
    private BigDecimal sumOfEigenVal = BigDecimal.ZERO;
    private BigDecimal sumOfSelected = BigDecimal.ZERO;

    public PCA(ArrayList<FrogTrainImg> trainingImg, int numOfComponents, TrainingEngine trainingEngine) throws Exception {

		if(numOfComponents >= trainingImg.size()){
			throw new Exception("the expected dimensions could not be achieved!");
		}

        trainingSet = new ArrayList<Matrix>();
//        labels = new ArrayList<String>();

        for (FrogTrainImg frogTrainImg : trainingImg) {
            trainingSet.add(frogTrainImg.getVectorized());
//            labels.add(imgMatrix.human.name);
        }

		this.numOfComponents = numOfComponents;
        this.trainingEngine = trainingEngine;

		this.meanMatrix = getMean(this.trainingSet);
		this.W = getFeature(this.trainingSet, this.numOfComponents);

		// Construct projectedTrainingMatrix
		this.projectedTrainingSet = new ArrayList<FrogTrainImg>();
//		for (int i = 0; i < trainingSet.size(); i++) {
////			ImgMatrix ptm = new ImgMatrix(project(trainingSet.get(i)), labels.get(i));
//            trainingImg.get(i).setIdMatrix(project(trainingImg.get(i).getVectorized()));
//
//            // setIsFace for trainingImg
//            setAndReturnIsFace(trainingImg.get(i));
//
////			this.projectedTrainingSet.add(tr);
//		}
//        projectedTrainingSet = trainingImg;
//
//        // project testImg
//        for (FrogTrainImg frogTrainImg : trainingEngine.testingImgSet) {
//            frogTrainImg.setIdMatrix(project(frogTrainImg.getVectorized()));
//
//            // setIsFace for testImg
//            setAndReturnIsFace(frogTrainImg);
//
//        }

//        for (Human p : trainingEngine.humanFactory.nameTable.values()) {
//            p.calculateID();
//        }


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
		this.projectedTrainingSet = new ArrayList<FrogTrainImg>();
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
	public BufferedImage reconstBufferImg(Matrix idMatrix) {
		Matrix restoredMatrix = getW().times(idMatrix).plus(getMeanMatrix());
        return FileManager.convertColMatrixToImage(restoredMatrix);
    }

    public Matrix reconstMatrix(Matrix idMatrix) {
        return getW().times(idMatrix).plus(getMeanMatrix());
    }

    public boolean setAndReturnIsFace(FrogTrainImg frogTrainImg) {
		Matrix reconstructedVector = ImageHelper.vectorize(reconstMatrix(frogTrainImg.getIdMatrix()));
        boolean isFace = TrainingEngine.IS_FACE_THRESHOLD > new EuclideanDistance().getDistance(reconstructedVector, frogTrainImg.getVectorized());
        frogTrainImg.setIsFace(isFace);
        return frogTrainImg.isFace();
    }

	@Deprecated
	public boolean isMatrixFace(Matrix matrix) {
        Matrix recon = ImageHelper.vectorize(reconstMatrix(project(ImageHelper.vectorize(matrix))));
		BufferedImage image = reconstBufferImg(project(ImageHelper.vectorize(matrix)));
        System.out.println(new EuclideanDistance().getDistance(recon, ImageHelper.vectorize(matrix)));
        Display.display(image);

        return TrainingEngine.IS_FACE_THRESHOLD > new EuclideanDistance().getDistance(recon, ImageHelper.vectorize(matrix));
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

	public ArrayList<FrogTrainImg> getProjectedTrainingSet() {
		return this.projectedTrainingSet;
	}
	
	public Matrix getMeanMatrix() {
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
		
		Matrix afterPCA = this.projectedTrainingSet.get(whichImage).getIdMatrix().getMatrix(0, dimensions - 1, 0, 0);
		Matrix eigen = this.getW().getMatrix(0, 10304-1, 0, dimensions - 1);
		return eigen.times(afterPCA).plus(this.getMeanMatrix());
		
	}

}
