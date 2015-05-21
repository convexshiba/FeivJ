package muffinc.frog.test.object;

import muffinc.frog.test.Jama.Matrix;
import muffinc.frog.test.eigenface.PCA;

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
    public String peopleName;
    public People people;
    public File file;
    public Matrix matrix;
    private Matrix projected;
    public double distance = -1;

    public ImgMatrix(Matrix projected, String peopleName) {
        this.peopleName = peopleName;
        this.projected = projected;
    }

    public ImgMatrix(File file, Matrix matrix) {
        this.matrix = matrix;
        this.file = file;
    }

    public ImgMatrix(File file, Matrix matrix, PCA pca) {
        this(file, matrix);
        this.pca = pca;
        projected = pca.project(matrix);
    }

    public People getPeople() {
        if (people != null) {
            return people;
        } else {
            throw new IllegalAccessError("Please set people First");
        }
    }

    public void setPeople(People people) {
        this.people = people;
    }

    public PCA getPca() {
        return pca;
    }

    public void setPca(PCA pca) {
        this.pca = pca;
    }

    public void project(PCA pca) {
        if (!isProjected()) {
            projected = pca.project(matrix);
        }
    }

    public boolean isProjected() {
        return projected != null;
    }

    public Matrix getProjected() {
        if (!isProjected()) {
            throw new IllegalAccessError("This Matrix has not been projected");
        } else {
            return projected;
        }
    }
}
