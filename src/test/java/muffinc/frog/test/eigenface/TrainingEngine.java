package muffinc.frog.test.eigenface;

import muffinc.frog.test.Jama.Matrix;
import muffinc.frog.test.common.Metric;
import muffinc.frog.test.displayio.Display;
import muffinc.frog.test.eigenface.metric.CosineDissimilarity;
import muffinc.frog.test.eigenface.metric.EuclideanDistance;
import muffinc.frog.test.eigenface.metric.L1Distance;
import muffinc.frog.test.helper.Writer;
import muffinc.frog.test.object.ImgMatrix;
import muffinc.frog.test.object.Human;

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

public class TrainingEngine {


    public static final int COMPONENT_NUMBER = 22;
    public static final double ID_THRESHOLD = 3000;
    public static final double IS_FACE_THRESHOLD = 200;
    public static final int METRIC_COSINE = 0;
    public static final int METRIC_L1D = 1;
    public static final int METRIC_EUCILDEAN = 2;

    public HumanFactory humanFactory;

    private int componentsRetained;
    private int trainNums;
    private int knn_k;

    public PCA pca;

    HashMap<String, ArrayList<Integer>> trainMap;
    HashMap<String, ArrayList<Integer>> testMap;

    ArrayList<Matrix> trainingSet = new ArrayList<Matrix>();
    ArrayList<String> labels = new ArrayList<String>();

    ArrayList<ImgMatrix> trainingImgSet = new ArrayList<ImgMatrix>();

    ArrayList<Matrix> testingSet = new ArrayList<Matrix>();
    ArrayList<String> trueLabels = new ArrayList<String>();

    ArrayList<ImgMatrix> testingImgSet = new ArrayList<ImgMatrix>();

    public TrainingEngine() {
        this(COMPONENT_NUMBER, 5, 2);
    }


    public TrainingEngine(int componentsRetained, int trainNums, int knn_k) {
        this.componentsRetained = componentsRetained;
        this.trainNums = trainNums;
        this.knn_k = knn_k;

        humanFactory = new HumanFactory(this);

        //set trainSet and testSet
        trainMap = new HashMap<String, ArrayList<Integer>>();
        testMap = new HashMap<String, ArrayList<Integer>>();

        for(int i = 1; i <= 10; i ++ ){
            String label = "s"+i;

//            Human human = new Human(label, this);
//
//            humanFactory.nameTable.put(label, human);
            humanFactory.newHuman(label);



            ArrayList<Integer> train = generateTrainNums(trainNums);
            ArrayList<Integer> test = generateTestNums(train);
            trainMap.put(label, train);
            testMap.put(label, test);
        }

        //trainingSet & respective labels
        trainingSet = new ArrayList<Matrix>();
        labels = new ArrayList<String>();


        trainingImgSet = new ArrayList<ImgMatrix>();

        Set<String> labelSet = trainMap.keySet();
        Iterator<String> it = labelSet.iterator();
        while(it.hasNext()){
            String label = it.next();
            ArrayList<Integer> cases = trainMap.get(label);
            for (int i = 0; i < cases.size(); i ++){
                String filePath = "/Users/Meth/Documents/FROG/src/test/faces/"+label+"/"+cases.get(i)+".pgm";

                File file = new File(filePath);

                Matrix temp;
                try {
                    temp = FileManager.convertPGMtoMatrix(filePath);

                    ImgMatrix imgMatrix = new ImgMatrix(file, temp, this);
//                    humanFactory.imgMatrixTable.put(file, imgMatrix);
//                    humanFactory.nameTable.get(label).addTrainImg(imgMatrix);
                    humanFactory.addTrainImgToHuman(imgMatrix, label);


                    imgMatrix.setVectorized(vectorize(temp));
                    trainingImgSet.add(imgMatrix);

                    trainingSet.add(vectorize(temp));
                    labels.add(label);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        testingSet = new ArrayList<Matrix>();
        trueLabels = new ArrayList<String>();

        testingImgSet = new ArrayList<ImgMatrix>();

        labelSet = testMap.keySet();
        it = labelSet.iterator();
        while(it.hasNext()){
            String label = it.next();
            ArrayList<Integer> cases = testMap.get(label);
            for(int i = 0; i < cases.size(); i ++){
                String filePath = "/Users/Meth/Documents/FROG/src/test/faces/"+label+"/"+cases.get(i)+".pgm";
                File file = new File(filePath);
                Matrix temp;
                try {
                    temp = FileManager.convertPGMtoMatrix(filePath);

                    ImgMatrix imgMatrix = new ImgMatrix(file, temp, this);
//                    humanFactory.imgMatrixTable.put(file, imgMatrix);
//                    humanFactory.nameTable.get(label).addImg(imgMatrix);
                    humanFactory.addImgToHuman(imgMatrix, label);
                    imgMatrix.setVectorized(vectorize(temp));
                    testingImgSet.add(imgMatrix);

                    testingSet.add(vectorize(temp));
                    trueLabels.add(label);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            pca = new PCA(trainingImgSet, componentsRetained, this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public double[] testKNNAccuracy() {
        //test featureExtraction
        double[] accuracies = new double[3];
        try{

//            PCA fe = new PCA(trainingSet, labels, componentsRetained, this);


//            Display.display(FileManager.convertColMatrixToImage(fe.getMeanMatrix()));

//            FileManager.convertMatricetoImage(fe.getW(), featureExtractionMode);


            for (int metrictype = 0; metrictype < 3; metrictype++) {
                Metric metric = getMetric(metrictype);
                String metricName = metric.getClass().getSimpleName();

                ArrayList<ImgMatrix> projectedTrainingSet = pca.getProjectedTrainingSet();

                int accurateNum = 0;

                for (ImgMatrix testImg : testingImgSet) {

                    // testImg will be project in PCA
//                    testImg.setIdMatrix(pca.project(testImg.getVectorized()));

                    String result = new KNN().assignLabel(projectedTrainingSet.toArray(new ImgMatrix[0]), testImg.getIdMatrix(), knn_k, metric);

                    if (result.equals(testImg.human.name)) {
                        accurateNum++;
                    }
                }


                double accuracy = accurateNum / (double)testingSet.size();
                System.out.println("The accuracy of " + metricName + "is "+accuracy);
                accuracies[metrictype] = accuracy;
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return accuracies;
    }

    public double testThresholdAccuracy() {
        //test featureExtraction
        double accuracies = Double.NaN;
        try{

//            PCA fe = new PCA(trainingSet, labels, componentsRetained, this);


//            Display.display(FileManager.convertColMatrixToImage(fe.getMeanMatrix()));

//            FileManager.convertMatricetoImage(fe.getW(), featureExtractionMode);


            for (int metrictype = 2; metrictype < 3; metrictype++) {
                Metric metric = getMetric(metrictype);
                String metricName = metric.getClass().getSimpleName();

                ArrayList<ImgMatrix> projectedTrainingSet = pca.getProjectedTrainingSet();

                int accurateNum = 0;

                for (ImgMatrix testImg : testingImgSet) {

                    // testImg will be project in PCA
//                    testImg.setIdMatrix(pca.project(testImg.getVectorized()));

                    String result = new Identification().assignLabel(projectedTrainingSet.toArray(new ImgMatrix[0]), testImg.getIdMatrix(), ID_THRESHOLD, metric);

                    if (result.equals(testImg.human.name)) {
                        accurateNum++;
                    } else {
                        System.out.println(result + " should be IDed as " + testImg.human.name);
                    }
                }


                double accuracy = accurateNum / (double)testingSet.size();
                System.out.println("The accuracy of " + metricName + "is "+accuracy);
                accuracies = accuracy;
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return accuracies;
    }

    @Deprecated
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
            PCA fe = new PCA(trainingSet, labels,componentsRetained, new TrainingEngine(10, 5, 2));


//            Display.display(FileManager.convertColMatrixToImage(fe.getMeanMatrix()));

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
                    String result = new KNN().assignLabel(projectedTrainingSet.toArray(new ImgMatrix[0]), testCase, knn_k, metric);

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

    public static void main(String args[]) {

        TrainingEngine engine = new TrainingEngine();
//        engine.testKNNAccuracy();

        ImgMatrix test = engine.humanFactory.imgMatrixTable.values().toArray(new ImgMatrix[1])[5];
        Display.display(test);
        Display.display(engine.pca.reconstBufferImg(test.getIdMatrix()));

//        for (ImgMatrix imgMatrix : engine.humanFactory.imgMatrixTable.values()) {
//            System.out.println(imgMatrix.isFace());
//        }
    }


    public static void findnWriteThreshold() {

        Metric metric = getMetric(METRIC_EUCILDEAN);

        Writer writer = new Writer("Threshold.csv");

        writer.write("Name, Max_Distance\n");

        for (int i = 1; i < 16; i++) {


            System.out.println(i);
            TrainingEngine trainingEngine1 = new TrainingEngine(COMPONENT_NUMBER, 5, 2);

            for (Human p : trainingEngine1.humanFactory.nameTable.values()) {
                writer.write(p.name + ",");
                double sum = 0;
                for (ImgMatrix imgMatrix : p.imgMatrices) {
                    sum += metric.getDistance(p.getIdMatrix(), imgMatrix.getIdMatrix());
                }
                writer.write(sum / p.fileNums);
                writer.newLine();
            }
        }
        writer.close();

    }


    @Deprecated
    public void addImgMatrixtoPp(String peopleName, ImgMatrix imgMatrix, boolean isTraingImg) {
        if (!humanFactory.nameTable.containsKey(peopleName)) {
            throw new IllegalArgumentException("PeopleTable does not contain this people, Please create people first");
        } else {
            humanFactory.nameTable.get(peopleName).addTrainImg(imgMatrix);
        }
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
    @Deprecated
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

    public static Metric getMetric(int i) {
        switch (i) {
            case METRIC_COSINE:
                return new CosineDissimilarity();
            case METRIC_L1D:
                return new L1Distance();
            case METRIC_EUCILDEAN:
                return new EuclideanDistance();
            default:
                throw new IllegalArgumentException("Please use a valid Metric");
        }
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
