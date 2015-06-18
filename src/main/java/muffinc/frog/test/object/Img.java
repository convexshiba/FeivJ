package muffinc.frog.test.object;

import muffinc.frog.test.Jama.Matrix;
import muffinc.frog.test.eigenface.PCA;
import org.bytedeco.javacpp.opencv_core;

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
public interface Img {
    Matrix getVectorized();

    void setVectorized(Matrix vectorized);

    Matrix getMatrix();

    void setMatrix(Matrix matrix);

    boolean isProjected();

    Matrix getIdMatrix();

    void setIdMatrix(Matrix idMatrix);

    double getDistance();

    void setDistance(double distance);

    Human getHuman();

//    void setCvRectHuman(Human human);

    void project(PCA pca);

    boolean isFace();

    void setIsFace(boolean isFace);

    opencv_core.IplImage toIplImage();

    boolean isDetected();

    File getFile();

    void setFile(File file);
}
