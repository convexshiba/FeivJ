/*
 * Copyright (c) 2015.  Jun Zhou
 *
 * YaF-DIVJ, Yet another Face Detection Image Viewer in Java
 * <p/>
 * This file is part of YaF-DIVJ.
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

package muffinc.yafdivj.eigenface;

import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;

import muffinc.yafdivj.Jama.Matrix;
import muffinc.yafdivj.datatype.Metric;
import muffinc.yafdivj.datatype.YafImg;
import muffinc.yafdivj.datatype.YafTrainImg;
import muffinc.yafdivj.datatype.Human;
import org.bytedeco.javacpp.opencv_core;

public class KNN{

    static class Pair implements Comparable<Pair>{
        YafImg yafImg;
        opencv_core.CvRect cvRect;
        double distance;
        Human human;

        public Pair(YafImg yafImg, opencv_core.CvRect cvRect, double distance) {
            this.yafImg = yafImg;
            this.cvRect = cvRect;
            human = yafImg.rectToHuman.get(cvRect);
            this.distance = distance;
        }

        @Override
        public int compareTo(Pair o) {
            return ((int) (o.distance - distance));
        }
    }

    public static Human assignHuman(HumanFactory humanFactory, YafImg yafImg, opencv_core.CvRect cvRect, int k, Metric metric) {
        Pair[] neighbors = findKNN(humanFactory, yafImg, cvRect, k, metric);
        return classify(neighbors);
    }

    private static Pair[] findKNN(HumanFactory humanFactory, YafImg img, opencv_core.CvRect imgCvRect, int k, Metric metric) {

        Pair[] neighbors = new Pair[k];

        PriorityQueue<Pair> pairPriorityQueue = new PriorityQueue<>();

        for (YafImg yafImg : humanFactory.frogImgTable.values()) {
            for (opencv_core.CvRect cvRect : yafImg.rectToHuman.keySet()) {
                pairPriorityQueue.add(new Pair(yafImg, cvRect, metric.getDistance(img.idMatrices.get(imgCvRect), yafImg.idMatrices.get(cvRect))));
            }
        }

        for (int i = 0; i < k; i++) {
            neighbors[i] = pairPriorityQueue.remove();
        }

        return neighbors;
    }

    private static Human classify(Pair[] neighbors) {
        HashMap<Human, Double> hashMap = new HashMap<>();

        for (Pair pair : neighbors) {
            if (hashMap.containsKey(pair.human)) {
                hashMap.put(pair.human, hashMap.get(pair.human) + 1 / pair.distance);
            } else {
                hashMap.put(pair.human, 1 / pair.distance);
            }
        }

        Human maxHuman = null;
        double maxDistance = Double.MIN_VALUE;

        for (Human human : hashMap.keySet()) {
            if (hashMap.get(human) > maxDistance) {
                maxDistance = hashMap.get(human);
                maxHuman = human;
            }
        }

        return maxHuman;
    }

    public String assignHuman(YafTrainImg[] trainingSet, Matrix testFace, int K, Metric metric) {
        YafTrainImg[] neighbors = findKNN(trainingSet, testFace, K, metric);
        return classify(neighbors);
    }

    // testFace has been projected to the subspace
    private static YafTrainImg[] findKNN(YafTrainImg[] trainingSet, Matrix testFace, int K, Metric metric) {
        int NumOfTrainingSet = trainingSet.length;
        assert K <= NumOfTrainingSet : "K is lager than the length of trainingSet!";

        // initialization
        YafTrainImg[] neighbors = new YafTrainImg[K];
        int i;
        for (i = 0; i < K; i++) {
            trainingSet[i].setDistance(metric.getDistance(trainingSet[i].getIdMatrix(),
                    testFace));
//			System.out.println("index: " + i + " distance: "
//					+ trainingSet[i].distance);
            neighbors[i] = trainingSet[i];
        }

        // go through the remaining records in the trainingSet to find K nearest
        // neighbors
        for (i = K; i < NumOfTrainingSet; i++) {
            trainingSet[i].setDistance(metric.getDistance(trainingSet[i].getIdMatrix(),
                    testFace));
//			System.out.println("index: " + i + " distance: "
//					+ trainingSet[i].distance);

            int maxIndex = 0;
            for (int j = 0; j < K; j++) {
                if (neighbors[j].getDistance() > neighbors[maxIndex].getDistance())
                    maxIndex = j;
            }

            if (neighbors[maxIndex].getDistance() > trainingSet[i].getDistance())
                neighbors[maxIndex] = trainingSet[i];
        }
        return neighbors;
    }

    // get the class label by using neighbors
    private static String classify(YafTrainImg[] neighbors) {
        HashMap<String, Double> map = new HashMap<String, Double>();
        int num = neighbors.length;

        for (int index = 0; index < num; index++) {
            YafTrainImg temp = neighbors[index];
            String key = temp.getHuman().name;
            if (!map.containsKey(key))
                map.put(key, 1 / temp.getDistance());
            else {
                double value = map.get(key);
                value += 1 / temp.getDistance();
                map.put(key, value);
            }
        }

        // Find the most likely label
        double maxSimilarity = 0;
        String returnLabel = "";
        Set<String> labelSet = map.keySet();
        Iterator<String> it = labelSet.iterator();
        while (it.hasNext()) {
            String label = it.next();
            double value = map.get(label);
            if (value > maxSimilarity) {
                maxSimilarity = value;
                returnLabel = label;
            }
        }

        return returnLabel;
    }
}

