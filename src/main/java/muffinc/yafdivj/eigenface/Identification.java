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

import muffinc.yafdivj.datatype.Metric;
import muffinc.yafdivj.datatype.YafTrainImg;
import muffinc.yafdivj.Jama.Matrix;

import java.util.ArrayList;


public class Identification {

    public String assignLabel(YafTrainImg[] trainingSet, Matrix testFace, double threshold, Metric metric) {
        return findString(trainingSet, testFace, threshold, metric);
    }

    private static String findString(YafTrainImg[] trainingSet, Matrix testFace, double threshold, Metric metric) {
        double smallest = Double.MAX_VALUE;
        double biggest = Double.MIN_VALUE;
        YafTrainImg nearest = null;

        for (int i = 0; i < trainingSet.length; i++) {
            trainingSet[i].setDistance(metric.getDistance(testFace, trainingSet[i].getIdMatrix()));
            if (trainingSet[i].getDistance() < smallest) {
                nearest = trainingSet[i];
                smallest = trainingSet[i].getDistance();
            }
        }

        if (smallest < threshold) {
        } else {
            return "New Person";
        }
        return nearest.getHuman().name;

    }

    @Deprecated
    private static String getSmallest(ArrayList<YafTrainImg> neibors) {
        if (neibors.size() == 1) {
            return neibors.get(0).getHuman().name;
        } else if (neibors.size() == 0) {
            System.out.println("People Not Found");
        } else {
            System.out.println("Found Multible people");
        }
        return "Error";
    }
}
