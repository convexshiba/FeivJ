package muffinc.frog.test.object;

import muffinc.frog.test.Jama.Matrix;
import muffinc.frog.test.eigenface.TrainingEngine;
import org.bytedeco.javacpp.opencv_core;

import java.util.ArrayList;
import java.util.LinkedList;

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
public class Human {
//    public TrainingEngine trainingEngine;

    public final String name;

//    public boolean isTrain = false;

    public ArrayList<FrogImg> trainingSet = new ArrayList<>();

    public final LinkedList<FrogImg> frogImgs;

    public int fileNums;

    private Matrix idMatrix = null;

    public Human(String name) {
        this.name = name;
//        this.trainingEngine = trainingEngine;
        frogImgs = new LinkedList<>();
        fileNums = 0;
    }

    public void addImg(FrogImg frogImg, opencv_core.CvRect cvRect) {
        frogImgs.add(frogImg);
        // already set in HumanFactory
        frogImg.setCvRectHuman(this, cvRect);
        fileNums++;
        calculateID();
    }

    public void calculateID() {
        Matrix sum = new Matrix(TrainingEngine.COMPONENT_NUMBER, 1, 0);
        for (FrogImg frogImg : frogImgs) {
            for (opencv_core.CvRect cvRect : frogImg.humanToRects.get(this)) {
                sum.plusEquals(frogImg.idMatrices.get(cvRect));
            }
        }
        idMatrix = sum.timesEquals(1 / ((double) frogImgs.size()));

    }

    public Matrix getIdMatrix() {
        if (idMatrix != null) {
            return idMatrix;
        } else {
            throw new IllegalAccessError(name + "'s idMatrix has not been calculated");
        }
    }
}