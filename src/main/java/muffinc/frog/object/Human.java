package muffinc.frog.object;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import muffinc.frog.Jama.Matrix;
import muffinc.frog.eigenface.TrainingEngine;
import org.bytedeco.javacpp.opencv_core;

import java.util.HashMap;
import java.util.HashSet;

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
//    public boolean isTrain = false;

    public final String name;
//    public ArrayList<FrogImg> trainingSet = new ArrayList<>();
//    public final LinkedList<FrogImg> frogImgs;
    public final HashMap<FrogImg, HashSet<opencv_core.CvRect>> frogImgs;
//    public int fileNums;
    public IntegerProperty fileNumber = new SimpleIntegerProperty(-1);
    private Matrix idMatrix = null;

    public Human(String name) {
        this.name = name;
//        this.trainingEngine = trainingEngine;
        frogImgs = new HashMap<>();
        fileNumber = new SimpleIntegerProperty(0);
    }

    public void linkWithImgCvRect(FrogImg frogImg, opencv_core.CvRect cvRect) {

        if (frogImg.rectToHuman.keySet().contains(cvRect) && frogImg.rectToHuman.get(cvRect) != this) {
            frogImg.rectToHuman.get(cvRect).deleteImg(frogImg, cvRect);
        }

        if (!frogImgs.containsKey(frogImg)) {
            frogImgs.put(frogImg, new HashSet<>());
        }

        if (!frogImgs.get(frogImg).contains(cvRect)) {
            frogImgs.get(frogImg).add(cvRect);
            fileNumber.setValue(fileNumber.getValue() + 1);
            frogImg.setCvRectHuman(this, cvRect);
        }

        if (idMatrix == null) {
            idMatrix = frogImg.idMatrices.get(cvRect).copy();
        } else {
            calculateID();
        }



//        if (idMatrix != null) {
//            if (frogImgs.containsKey(frogImg)) {
//                if (!frogImgs.get(frogImg).contains(cvRect)) {
//                    frogImgs.get(frogImg).add(cvRect);
//                }
//                fileNumber.setValue(fileNumber.getValue() + 1);
//                frogImg.setCvRectHuman(this, cvRect);
//                calculateID();
//
//            } else {
//                frogImgs.put(frogImg, new HashSet<>());
//                linkWithImgCvRect(frogImg, cvRect);
//            }
//        } else {
//            idMatrix = frogImg.idMatrices.get(cvRect).copy();
//        }
    }

    public void deleteImg(FrogImg frogImg, opencv_core.CvRect cvRect) {
        if (frogImgs.containsKey(frogImg)) {
            if (frogImgs.get(frogImg).contains(cvRect)) {
                frogImgs.get(frogImg).remove(cvRect);
                if (frogImgs.get(frogImg).isEmpty()) {
                    frogImgs.remove(frogImg);
                }
                fileNumber.setValue(fileNumber.getValue() - 1);
                calculateID();
            }
        }
        frogImg.updatePeopleNames();
    }

    public void calculateID() {
        Matrix sum = new Matrix(TrainingEngine.COMPONENT_NUMBER, 1, 0);
        for (FrogImg frogImg : frogImgs.keySet()) {
            for (opencv_core.CvRect cvRect : frogImg.getHumanCvRects(this)) {
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

    public boolean hasIDMatrix() {
        return idMatrix != null;
    }
}