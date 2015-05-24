package muffinc.frog.test.object;

import muffinc.frog.test.Jama.Matrix;
import muffinc.frog.test.eigenface.PCA;
import muffinc.frog.test.eigenface.TrainingEngine;

import java.io.File;

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
public class ImgMatrix {
    public PCA pca;
    public TrainingEngine trainingEngine;
//    public String peopleName;
    public Human human;
    public File file;
    public Matrix matrix;
    public Matrix vectorized;
    private Matrix projectedVector;
    private double distance = -1;

    public ImgMatrix(File file) {
        this.file = file;
    }

    public ImgMatrix(File file, Matrix matrix, TrainingEngine trainingEngine) {
        this.matrix = matrix;
        this.file = file;
    }

    public Matrix getVectorized() {
        return vectorized;
    }

    public void setVectorized(Matrix vectorized) {
        this.vectorized = vectorized;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public boolean isProjected() {
        return projectedVector != null;
    }

    public Matrix getProjectedVector() {
        if (!isProjected()) {
            throw new IllegalAccessError("This Matrix has not been projectedVector");
        } else {
            return projectedVector;
        }
    }

    public void setProjectedVector(Matrix projectedVector) {
        this.projectedVector = projectedVector;
    }

    public double getDistance() {
        if (distance == -1) {
            throw new IllegalAccessError("distance has not been initialized");
        } else {
            return distance;
        }
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Human getHuman() {
        if (human != null) {
            return human;
        } else {
            throw new IllegalAccessError("Please set people First");
        }
    }

    public void setHuman(Human human) {
        this.human = human;
    }

    public PCA getPca() {
        return pca;
    }

    public void setPca(PCA pca) {
        this.pca = pca;
    }

    public void project(PCA pca) {
        if (!isProjected()) {
            projectedVector = pca.project(matrix);
        }
    }
}
