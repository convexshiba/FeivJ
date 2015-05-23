package muffinc.frog.test.object;

import muffinc.frog.test.Jama.Matrix;
import muffinc.frog.test.eigenface.Train;

import java.util.ArrayList;

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
public class People {

    public boolean isTrain = false;

    public ArrayList<ImgMatrix> trainingSet = new ArrayList<ImgMatrix>();

    public final String name;

    public final ArrayList<ImgMatrix> imgMatrices;

    public int fileNums;

    public Matrix projectedIDMatrix = null;

    public Train train;

    public People(String name, Train train) {
        this.name = name;
        this.train = train;
        imgMatrices = new ArrayList<ImgMatrix>();
        fileNums = 0;
    }

    public void addImgMatrix(ImgMatrix imgMatrix, boolean isTrainImg) {
        imgMatrices.add(imgMatrix);
        imgMatrix.setPeople(this);
        fileNums++;
        if (isTrainImg) {
            trainingSet.add(imgMatrix);
            isTrain = true;
        }
        recalculateProjected();
    }

    public void recalculateProjected() {

    }
}