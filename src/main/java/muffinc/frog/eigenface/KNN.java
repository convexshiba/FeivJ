package muffinc.frog.eigenface;

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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import muffinc.frog.object.FrogTrainImg;
import muffinc.frog.Jama.Matrix;
import muffinc.frog.common.Metric;

public class KNN{

    public String assignLabel(FrogTrainImg[] trainingSet,Matrix testFace, int K, Metric metric) {
        FrogTrainImg[] neighbors = findKNN(trainingSet, testFace, K, metric);
        return classify(neighbors);
    }

    // testFace has been projected to the subspace
    private static FrogTrainImg[] findKNN(FrogTrainImg[] trainingSet,Matrix testFace, int K, Metric metric) {
        int NumOfTrainingSet = trainingSet.length;
        assert K <= NumOfTrainingSet : "K is lager than the length of trainingSet!";

        // initialization
        FrogTrainImg[] neighbors = new FrogTrainImg[K];
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
    private static String classify(FrogTrainImg[] neighbors) {
        HashMap<String, Double> map = new HashMap<String, Double>();
        int num = neighbors.length;

        for (int index = 0; index < num; index++) {
            FrogTrainImg temp = neighbors[index];
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

