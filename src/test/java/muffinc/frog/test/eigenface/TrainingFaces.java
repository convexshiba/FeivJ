package muffinc.frog.test.eigenface;

import org.ejml.interfaces.decomposition.EigenDecomposition;
import org.ejml.ops.EigenOps;
import org.ejml.simple.SimpleEVD;
import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * FROG, a Face Recognition Gallery in Java
 * Copyright (C) 2015 Jun Zhou
 * <p/>
 * This file is part of FROG.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 * zj45499 (at) gmail (dot) com
 */

class projectedFaceMatrix {
    SimpleMatrix matrix;
    String label;
    double distance = 0;

    public projectedFaceMatrix(SimpleMatrix matrix, String label) {
        this.matrix = matrix;
        this.label = label;
    }
}


public class TrainingFaces {
    ArrayList<SimpleMatrix> trainningSet;
    ArrayList<String> labels;
    int numOfComponents;
    SimpleMatrix meanMatrix;

    //output Matrix
    SimpleMatrix output;
    ArrayList<projectedFaceMatrix> projectedTrainingSet;


    public TrainingFaces(ArrayList<SimpleMatrix> trainningSet, ArrayList<String> labels,
                         int numOfComponents) throws Exception{

        if (numOfComponents >= trainningSet.size()) {
            throw new Exception("Dimension overflow");
        }

        this.trainningSet = trainningSet;
        this.labels = labels;
        this.numOfComponents = numOfComponents;

        meanMatrix = getMean(this.trainningSet);
        output = getFeature(this.trainningSet, this.numOfComponents);

        //construct projectedTrainingMatrix
        projectedTrainingSet = new ArrayList<projectedFaceMatrix>();

        for (int i = 0; i < trainningSet.size(); i++) {
            projectedFaceMatrix pfm = new projectedFaceMatrix(output.
                    transpose().mult(trainningSet.get(i).minus(meanMatrix)),
                    labels.get(i));

            projectedTrainingSet.add(pfm);
        }

    }

    //extract outputMatrix
    private SimpleMatrix getFeature(ArrayList<SimpleMatrix> input, int K) {
        int j;

        int row = input.get(0).getMatrix().numRows;
        int col = input.size();
        SimpleMatrix A = new SimpleMatrix(row, col);

        for (int i = 0; i < col; i++) {
            A.insertIntoThis(0, i, input.get(i).minus(meanMatrix));
        }

        //generate eigenvalues and eigenvectors
        SimpleMatrix AT = A.transpose();
        SimpleMatrix ATA = AT.mult(A);
        SimpleEVD d = ATA.eig();


        if (d.getNumberOfEigenvalues() < K) {
            throw new IllegalArgumentException("number of eigenvalues is less than K");
        }

        int[] indexes = getIndexesOfKEig(d, K);

        SimpleMatrix eigenVectors = A.mult(new SimpleMatrix(EigenOps.createMatrixV(d.getEVD())));
        SimpleMatrix selectedEigenVectors = new SimpleMatrix(eigenVectors.numRows(), indexes.length);
        for (int i = 0; i < indexes.length; i++) {
            selectedEigenVectors.insertIntoThis(1, i ,eigenVectors.extractVector(false, indexes[i]));
        }

        selectedEigenVectors = selectedEigenVectors.divide(selectedEigenVectors.normF());

        return selectedEigenVectors;
    }

    private static SimpleMatrix getMean(ArrayList<SimpleMatrix> input) {
        int rows = input.get(0).numRows();
        int length = input.size();
        SimpleMatrix sum = new SimpleMatrix(rows, 1);

        for (SimpleMatrix anInput : input) {
            sum.plus(anInput);
        }

        return sum.divide(((double) 1) / length);
    }

    private class mix implements Comparable {
        int index;
        double value;

        mix(int i, double v) {
            index = i;
            value = v;
        }

        public int compareTo(Object o) {
            return ((Comparable) value).compareTo(((mix) o).value);
        }
    }

    private int[] getIndexesOfKEig(SimpleEVD evd, int k) {
        double[] d = new double[evd.getNumberOfEigenvalues()];

        for (int i = 0; i < d.length; i++) {
            d[i] = evd.getEigenvalue(i).getReal();
        }

        mix[] mixes = new mix[d.length];

        for (int i = 0; i < d.length; i++) {
            mixes[i] = new mix(i, d[i]);
        }

        Arrays.sort(mixes);

        int[] result = new int[k];
        for (int i = 0; i < k; i++) {
            result[i] = mixes[i].index;
        }

        return result;
    }

    public SimpleMatrix getOutput() {
        return output;
    }

    public SimpleMatrix getMeanMatrix() {
        return meanMatrix;
    }

    public ArrayList<projectedFaceMatrix> getProjectedTrainingSet() {
        return projectedTrainingSet;
    }

    public ArrayList<SimpleMatrix> getTrainningSet() {
        return trainningSet;
    }

}
