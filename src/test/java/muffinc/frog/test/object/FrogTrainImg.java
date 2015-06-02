package muffinc.frog.test.object;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import muffinc.frog.test.Jama.Matrix;
import muffinc.frog.test.detection.FaceDetection;
import muffinc.frog.test.eigenface.PCA;
import muffinc.frog.test.eigenface.TrainingEngine;
import org.bytedeco.javacpp.opencv_core.*;

import java.io.File;
import java.util.LinkedList;

import static org.bytedeco.javacpp.opencv_highgui.*;

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
public class FrogTrainImg {
//    public PCA pca;
//    public TrainingEngine trainingEngine;
//    public String peopleName;
    public Human human;
    public File file;
    public Matrix matrix;
    public Matrix vectorized;
    private boolean isFace;
    private Matrix idMatrix;
    private double distance = -1;
//    private boolean detected = false;
    public IntegerProperty detectedFaces = new SimpleIntegerProperty(-1);
    private LinkedList<CvRect> cvRects = null;
    private Metadata metadata = null;

    private boolean isScaned;

    public FrogTrainImg(File file) {
        this.file = file;
        try {
            metadata = ImageMetadataReader.readMetadata(file);
        } catch (Exception  e) {
//            e.printStackTrace();
            metadata = new Metadata();
        }
    }

    public FrogTrainImg(File file, Matrix matrix, TrainingEngine trainingEngine) {
        this(file);
        this.matrix = matrix;
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
        return idMatrix != null;
    }

    public Matrix getIdMatrix() {
        if (!isProjected()) {
            throw new IllegalAccessError("This Matrix has not been idMatrix");
        } else {
            return idMatrix;
        }
    }

    public void setIdMatrix(Matrix idMatrix) {
        this.idMatrix = idMatrix;
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

    public void project(PCA pca) {
        if (!isProjected()) {
            idMatrix = pca.project(matrix);
        }
    }

    public boolean isFace() {
        return isFace;
    }

    public void setIsFace(boolean isFace) {
        this.isFace = isFace;
    }

    public IplImage toIplImage() {
        return cvLoadImage(file.getAbsolutePath());
    }

    public boolean isDetected() {
        return detectedFaces.toString().equals("-1");
    }

    public boolean hasFace() {
        if (!isDetected()) {
            throw new IllegalAccessError(file.getAbsolutePath() + "has not be Detected");
        } else {
            return cvRects.size() != 0;
        }
    }

    public int faceNumber() {
        return cvRects.size();
    }

    public void detectFace() {
        cvRects = FaceDetection.detectFaces(file);
        detectedFaces.setValue(cvRects.size());
        System.out.println("detectFace() found " + cvRects.size() + " faces");
    }

    public void removeCvRect(int index) {
        if (cvRects.remove(cvRects.get(index))) {
            System.out.println("cvRect has been successfully removed");
        } else {
            System.out.println("Does not contain CvRect");
        }
    }
}
