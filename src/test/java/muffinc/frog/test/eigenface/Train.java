package muffinc.frog.test.eigenface;

//import muffinc.frog.test.helper.ImageHelper;
//import org.bytedeco.javacpp.opencv_core.*;
//import org.bytedeco.javacpp.opencv_highgui.*;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.io.IOException;
//import java.util.*;
//import javax.swing.*;
//
//import static org.bytedeco.javacpp.opencv_core.*;
//import static org.bytedeco.javacpp.opencv_highgui.*;
//import static org.bytedeco.javacpp.opencv_objdetect.*;
//import static org.bytedeco.javacpp.opencv_imgproc.*;
//
//
//import org.bytedeco.javacv.*;
//import org.bytedeco.javacpp.opencv_imgproc.*;
//import org.bytedeco.javacpp.*;
//import org.bytedeco.javacpp.indexer.*;
//import org.ejml.simple.SimpleMatrix;
//
//import static org.bytedeco.javacpp.opencv_core.*;
//import static org.bytedeco.javacpp.opencv_imgproc.*;
//import static org.bytedeco.javacpp.opencv_calib3d.*;
//import static org.bytedeco.javacpp.opencv_objdetect.*;

import muffinc.frog.test.Jama.Matrix;
import muffinc.frog.test.common.Metric;
import muffinc.frog.test.eigenface.metric.CosineDissimilarity;
import muffinc.frog.test.eigenface.metric.EuclideanDistance;
import muffinc.frog.test.eigenface.metric.L1Distance;

import java.io.File;
import java.io.IOException;
import java.util.*;

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

public class Train {

    public HashMap<String, People> nameTable = new HashMap<String, People>();

    public HashMap<File, People> imgTable = new HashMap<File, People>();

    public HashMap<File, ImgMatrix> matrixTable = new HashMap<File, ImgMatrix>();

    public void associate(String peopleName, File file) {
        if (!nameTable.containsKey(peopleName)) {
            throw new IllegalArgumentException("PeopleTable does not contain this people");
        } else {
            nameTable.get(peopleName).addFile(file);
            imgTable.put(file, nameTable.get(peopleName));
        }
    }

    public Train() {
        int componentsRetained = 25;
        int trainNums = 5;
        int knn_k = 2;

        //set trainSet and testSet
        HashMap<String, ArrayList<Integer>> trainMap = new HashMap<String, ArrayList<Integer>>();
        HashMap<String, ArrayList<Integer>> testMap = new HashMap<String, ArrayList<Integer>>();
        for(int i = 1; i <= 10; i ++ ){
            String label = "s"+i;

            People johnDoe = new People(label, this);

            nameTable.put(label, johnDoe);

            ArrayList<Integer> train = generateTrainNums(trainNums);
            ArrayList<Integer> test = generateTestNums(train);
            trainMap.put(label, train);
            testMap.put(label, test);
        }

        //trainingSet & respective labels
        ArrayList<Matrix> trainingSet = new ArrayList<Matrix>();
        ArrayList<String> labels = new ArrayList<String>();

        Set<String> labelSet = trainMap.keySet();
        Iterator<String> it = labelSet.iterator();
        while(it.hasNext()){
            String label = it.next();
            ArrayList<Integer> cases = trainMap.get(label);
            for(int i = 0; i < cases.size(); i ++){
                String filePath = "/Users/Meth/Documents/FROG/src/test/faces/"+label+"/"+cases.get(i)+".pgm";

                File file = new File(filePath);

                associate(label, file);

                Matrix temp;
                try {
                    temp = FileManager.convertPGMtoMatrix(filePath);

                    matrixTable.put(file, new ImgMatrix(temp, file));

                    trainingSet.add(vectorize(temp));
                    labels.add(label);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        ArrayList<Matrix> testingSet = new ArrayList<Matrix>();
        ArrayList<String> trueLabels = new ArrayList<String>();

        labelSet = testMap.keySet();
        it = labelSet.iterator();
        while(it.hasNext()){
            String label = it.next();
            ArrayList<Integer> cases = testMap.get(label);
            for(int i = 0; i < cases.size(); i ++){
                String filePath = "/Users/Meth/Documents/FROG/src/test/faces/"+label+"/"+cases.get(i)+".pgm";
                Matrix temp;
                try {
                    temp = FileManager.convertPGMtoMatrix(filePath);
                    testingSet.add(vectorize(temp));
                    trueLabels.add(label);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }


        //set featureExtraction
        try{
            PCA fe = new PCA(trainingSet, labels, componentsRetained, this);


//            Display.display(FileManager.convertVectorToImage(fe.getMeanMatrix()));

//            FileManager.convertMatricetoImage(fe.getW(), featureExtractionMode);


            for (int j = 0; j < 3; j++) {
                int metricType = j;
                String metricName = "";
                Metric metric = null;
                if(metricType == 0) {
                    metric = new CosineDissimilarity();
                    metricName = "Cosine";
                }
                else if (metricType == 1) {
                    metric = new L1Distance();
                    metricName = "L1";
                }
                else if (metricType == 2) {
                    metric = new EuclideanDistance();
                    metricName = " Euclidean";
                }

                assert metric != null : "metricType is wrong!";

                ArrayList<ImgMatrix> projectedTrainingSet = fe.getProjectedTrainingSet();
                int accurateNum = 0;
                for(int i = 0 ; i < testingSet.size(); i ++){
                    Matrix testCase = fe.getW().transpose().times(testingSet.get(i).minus(fe.getMeanMatrix()));
                    String result = KNN.assignLabel(projectedTrainingSet.toArray(new ImgMatrix[0]), testCase, knn_k, metric);

                    if(result.equals(trueLabels.get(i)))
                        accurateNum ++;
                }
                double accuracy = accurateNum / (double)testingSet.size();
                System.out.println("The accuracy of " + metricName + "is "+accuracy);
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void main2(String args[]) {
        //Test Different Methods
        //Notice that the second parameter which is a measurement of energy percentage does not apply to LDA and LPP
//        test(2,101,0,3,2);
//		test(2,60,1,3,2);
//		test(2,60,2,3,2);

        int componentsRetained = 25;
        int trainNums = 5;
        int knn_k = 2;

        //set trainSet and testSet
        HashMap<String, ArrayList<Integer>> trainMap = new HashMap();
        HashMap<String, ArrayList<Integer>> testMap = new HashMap();
        for(int i = 1; i <= 10; i ++ ){
            String label = "s"+i;
            ArrayList<Integer> train = generateTrainNums(trainNums);
            ArrayList<Integer> test = generateTestNums(train);
            trainMap.put(label, train);
            testMap.put(label, test);
        }

        //trainingSet & respective labels
        ArrayList<Matrix> trainingSet = new ArrayList<Matrix>();
        ArrayList<String> labels = new ArrayList<String>();

        Set<String> labelSet = trainMap.keySet();
        Iterator<String> it = labelSet.iterator();
        while(it.hasNext()){
            String label = it.next();
            ArrayList<Integer> cases = trainMap.get(label);
            for(int i = 0; i < cases.size(); i ++){
                String filePath = "/Users/Meth/Documents/FROG/src/test/faces/"+label+"/"+cases.get(i)+".pgm";
                Matrix temp;
                try {
                    temp = FileManager.convertPGMtoMatrix(filePath);
                    trainingSet.add(vectorize(temp));
                    labels.add(label);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        ArrayList<Matrix> testingSet = new ArrayList<Matrix>();
        ArrayList<String> trueLabels = new ArrayList<String>();

        labelSet = testMap.keySet();
        it = labelSet.iterator();
        while(it.hasNext()){
            String label = it.next();
            ArrayList<Integer> cases = testMap.get(label);
            for(int i = 0; i < cases.size(); i ++){
                String filePath = "/Users/Meth/Documents/FROG/src/test/faces/"+label+"/"+cases.get(i)+".pgm";
                Matrix temp;
                try {
                    temp = FileManager.convertPGMtoMatrix(filePath);
                    testingSet.add(vectorize(temp));
                    trueLabels.add(label);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }


        //set featureExtraction
        try{
            PCA fe = new PCA(trainingSet, labels,componentsRetained, new Train());


//            Display.display(FileManager.convertVectorToImage(fe.getMeanMatrix()));

//            FileManager.convertMatricetoImage(fe.getW(), featureExtractionMode);


            for (int j = 0; j < 3; j++) {
                int metricType = j;
                String metricName = "";
                Metric metric = null;
                if(metricType == 0) {
                    metric = new CosineDissimilarity();
                    metricName = "Cosine";
                }
                else if (metricType == 1) {
                    metric = new L1Distance();
                    metricName = "L1";
                }
                else if (metricType == 2) {
                    metric = new EuclideanDistance();
                    metricName = " Euclidean";
                }

                assert metric != null : "metricType is wrong!";

                ArrayList<ImgMatrix> projectedTrainingSet = fe.getProjectedTrainingSet();
                int accurateNum = 0;
                for(int i = 0 ; i < testingSet.size(); i ++){
                    Matrix testCase = fe.getW().transpose().times(testingSet.get(i).minus(fe.getMeanMatrix()));
                    String result = KNN.assignLabel(projectedTrainingSet.toArray(new ImgMatrix[0]), testCase, knn_k, metric);

                    if(result.equals(trueLabels.get(i)))
                        accurateNum ++;
                }
                double accuracy = accurateNum / (double)testingSet.size();
                System.out.println("The accuracy of " + metricName + "is "+accuracy);
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }

    }

    public static void main(String args[]){

        Train train1 = new Train();

    }

    /*metricType:
     * 	0: CosineDissimilarity
     * 	1: L1Distance
     * 	2: EuclideanDistance
     *
     * energyPercentage:
     *  PCA: components = samples * energyPercentage
     *  LDA: components = (c-1) *energyPercentage
     *  LLP: components = (c-1) *energyPercentage
     *
     * featureExtractionMode
     * 	0: PCA
     *	1: LDA
     * 	2: LLP
     *
     * trainNums: how many numbers in 1..10 are assigned to be training faces
     * for each class, randomly generate the set
     *
     * knn_k: number of K for KNN algorithm
     *
     * */
    static double test(int metricType, int componentsRetained, int featureExtractionMode, int trainNums, int knn_k){
        //determine which metric is used
        //metric
/*        Metric metric = null;
        if(metricType == 0)
            metric = new CosineDissimilarity();
        else if (metricType == 1)
            metric = new L1Distance();
        else if (metricType == 2)
            metric = new EuclideanDistance();

        assert metric != null : "metricType is wrong!";*/

        //set expectedComponents according to energyPercentage
        //componentsRetained
//		int trainingSize = trainNums * 40;
//		int componentsRetained = 0;
//		if(featureExtractionMode == 0)
//			componentsRetained = (int) (trainingSize * energyPercentage);
//		else if(featureExtractionMode == 1)
//			componentsRetained = (int) ((40 -1) * energyPercentage);
//		else if(featureExtractionMode == 2)
//			componentsRetained = (int) ((40 -1) * energyPercentage);

        //set trainSet and testSet
        HashMap<String, ArrayList<Integer>> trainMap = new HashMap();
        HashMap<String, ArrayList<Integer>> testMap = new HashMap();
        for(int i = 1; i <= 40; i ++ ){
            String label = "s"+i;
            ArrayList<Integer> train = generateTrainNums(trainNums);
            ArrayList<Integer> test = generateTestNums(train);
            trainMap.put(label, train);
            testMap.put(label, test);
        }

        //trainingSet & respective labels
        ArrayList<Matrix> trainingSet = new ArrayList<Matrix>();
        ArrayList<String> labels = new ArrayList<String>();

        Set<String> labelSet = trainMap.keySet();
        Iterator<String> it = labelSet.iterator();
        while(it.hasNext()){
            String label = it.next();
            ArrayList<Integer> cases = trainMap.get(label);
            for(int i = 0; i < cases.size(); i ++){
                String filePath = "faces/"+label+"/"+cases.get(i)+".pgm";
                Matrix temp;
                try {
                    temp = FileManager.convertPGMtoMatrix(filePath);
                    trainingSet.add(vectorize(temp));
                    labels.add(label);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        //testingSet & respective true labels
        ArrayList<Matrix> testingSet = new ArrayList<Matrix>();
        ArrayList<String> trueLabels = new ArrayList<String>();

        labelSet = testMap.keySet();
        it = labelSet.iterator();
        while(it.hasNext()){
            String label = it.next();
            ArrayList<Integer> cases = testMap.get(label);
            for(int i = 0; i < cases.size(); i ++){
                String filePath = "faces/"+label+"/"+cases.get(i)+".pgm";
                Matrix temp;
                try {
                    temp = FileManager.convertPGMtoMatrix(filePath);
                    testingSet.add(vectorize(temp));
                    trueLabels.add(label);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        //set featureExtraction
        try{
//            TrainingFaces fe = new TrainingFaces(trainingSet, labels,componentsRetained);

//            FileManager_SM.convertMatricetoImage(fe.getOutput(), featureExtractionMode);

            //PCA Reconstruction
//
//			Matrix hhMatrix = ((PCA) fe).reconstruct(50, 60);
//			FileManager.convertToImage(hhMatrix, 60);
//
//			hhMatrix = ((PCA) fe).reconstruct(50, 40);
//			FileManager.convertToImage(hhMatrix, 40);
//
//			hhMatrix = ((PCA) fe).reconstruct(50, 20);
//			FileManager.convertToImage(hhMatrix, 20);
//
//			hhMatrix = ((PCA) fe).reconstruct(50, 10);
//			FileManager.convertToImage(hhMatrix, 10);
//
//			hhMatrix = ((PCA) fe).reconstruct(50, 6);
//			FileManager.convertToImage(hhMatrix, 6);
//
//			hhMatrix = ((PCA) fe).reconstruct(50, 2);
//			FileManager.convertToImage(hhMatrix, 2);
//
//			hhMatrix = ((PCA)fe).getTrainingSet().get(50);




            //use test cases to validate
            //testingSet   trueLables
//            ArrayList<projectedFaceMatrix> projectedTrainingSet = fe.getProjectedTrainingSet();
//            int accurateNum = 0;
//            for(int i = 0 ; i < testingSet.size(); i ++){
//                Matrix testCase = fe.getW().transpose().times(testingSet.get(i).minus(fe.getMeanMatrix()));
//                String result = KNN.assignLabel(projectedTrainingSet.toArray(new projectedTrainingMatrix[0]), testCase, knn_k, metric);
//
//                if(result == trueLabels.get(i))
//                    accurateNum ++;
//            }
//            double accuracy = accurateNum / (double)testingSet.size();
//            System.out.println("The accuracy is "+accuracy);
//            return accuracy;

        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        return -1;
    }

    static ArrayList<Integer> generateTrainNums(int trainNum){
        Random random = new Random();
        ArrayList<Integer> result = new ArrayList<Integer>();

        while(result.size() < trainNum){
			int temp = random.nextInt(10)+1;
			while(result.contains(temp)){
				temp = random.nextInt(10)+1;
			}
            result.add(temp);

        }

        return result;
    }

    static ArrayList<Integer> generateTestNums(ArrayList<Integer> trainSet){
        ArrayList<Integer> result = new ArrayList<Integer>();
        for(int i= 1; i <= 10; i ++){
            if(!trainSet.contains(i))
                result.add(i);
        }
        return result;
    }

    //correct
    //Convert a m by n matrix into a m*n by 1 matrix
    public static Matrix vectorize(Matrix input){
        int m = input.getRowDimension();
        int n = input.getColumnDimension();

        Matrix result = new Matrix(m*n,1);
        for(int p = 0; p < n; p ++){
            for(int q = 0; q < m; q ++){
                result.set(p*m+q, 0, input.get(q, p));
            }
        }
        return result;

//        input.reshape(input.numRows() * input.numCols(), 1);
//        return input;
    }

}
