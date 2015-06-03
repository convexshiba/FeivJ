package muffinc.frog.test.object;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import muffinc.frog.test.Jama.Matrix;
import muffinc.frog.test.detection.FaceDetection;
import muffinc.frog.test.eigenface.PCA;
import muffinc.frog.test.eigenface.TrainingEngine;
import org.bytedeco.javacpp.opencv_core;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

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
public class FrogImg {

    public IplImage originalIplImage;
    public IplImage currentIplImage;
    public Image currentImage;

//    private Human human;
    private File file;
    private Matrix matrix;
    private Matrix vectorized;
    private boolean isFace;
    private Matrix idMatrix;
//    private double distance = -1;
//    private boolean detected = false;

    private boolean isScaned;
    public IntegerProperty detectedFaces = new SimpleIntegerProperty(-1);
    private LinkedList<CvRect> cvRects = null;
    public HashMap<CvRect, Human> rectToHuman;
    public HashMap<Human, LinkedList<CvRect>> humanToRects;
    public HashMap<CvRect, Matrix> idMatrices = null;

    private Metadata metadata = null;


//    public FrogImg() {
//        super();
//    }


    public FrogImg(File file) {
        this.file = file;
        originalIplImage = cvLoadImage(file.getAbsolutePath());
        currentIplImage = originalIplImage.clone();
        currentImage = SwingFXUtils.toFXImage(currentIplImage.getBufferedImage(), null);

        isScaned = false;

        try {
            metadata = ImageMetadataReader.readMetadata(file);
        } catch (Exception  e) {
//            e.printStackTrace();
            metadata = new Metadata();
        }
    }

//    public FrogImg(File file, Matrix matrix, TrainingEngine trainingEngine) {
//        this(file);
//        this.matrix = matrix;
//    }


    public void setCvRectHuman(Human human, CvRect cvRect) {
        rectToHuman.put(cvRect, human);

        if (humanToRects.containsKey(human)) {
            humanToRects.get(human).add(cvRect);
        } else {
            LinkedList<CvRect> temp = new LinkedList<>();
            temp.add(cvRect);
            humanToRects.put(human, temp);
        }
    }

    private void updateCurrentIplImage() {

        CvFont font = new CvFont();
        cvInitFont(font, CV_FONT_HERSHEY_SIMPLEX, 1, 1, 0, 2, CV_AA);

        for (int i = 0; i < cvRects.size(); i++) {
            CvRect cvRect = cvRects.get(i);
            cvPutText(currentIplImage, String.valueOf(i + 1), cvPoint(cvRect.x() + cvRect.width() - 20, cvRect.y() + cvRect.height() - 10), font, CvScalar.MAGENTA);
            cvRectangle(currentIplImage, cvPoint(cvRect.x(), cvRect.y()),
                    cvPoint(cvRect.x() + cvRect.width(), cvRect.y() + cvRect.height()), CvScalar.YELLOW, 1, CV_AA, 0);
        }
        currentImage = SwingFXUtils.toFXImage(currentIplImage.getBufferedImage(), null);
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

//    public double getDistance() {
//        if (distance == -1) {
//            throw new IllegalAccessError("distance has not been initialized");
//        } else {
//            return distance;
//        }
//    }
//
//    public void setDistance(double distance) {
//        this.distance = distance;
//    }

//    public Human[] getHuman() {
//        if (human != null) {
//            return human;
//        } else {
//            throw new IllegalAccessError("Please set people First");
//        }
//    }
//
//    public void setCvRectHuman(Human human, CvRect cvRect) {
//        this.human = human;
//    }

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

    public opencv_core.IplImage toIplImage() {
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
        if (!isDetected()) {
            cvRects = FaceDetection.detectFaces(file);
            detectedFaces.setValue(cvRects.size());
            System.out.println("detectFace() found " + cvRects.size() + " faces");
        }
        updateCurrentIplImage();
    }

    public Image getCurrentImage() {
        return currentImage;
    }

    public void removeCvRect(int index) {
        if (cvRects.remove(cvRects.get(index))) {
            System.out.println("cvRect has been successfully removed");
        } else {
            System.out.println("Does not contain CvRect");
        }
        updateCurrentIplImage();
    }

    public double getDistance() {
        return 0;
    }

    public void setDistance(double distance) {

    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isScaned() {
        return isScaned;
    }

    public void setIsScaned(boolean isScaned) {
        this.isScaned = isScaned;
    }

    public LinkedList<CvRect> getCvRects() {
        return cvRects;
    }

    public void setCvRects(LinkedList<CvRect> cvRects) {
        this.cvRects = cvRects;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public int getDetectedFaces() {
        return detectedFaces.get();
    }

    public IntegerProperty detectedFacesProperty() {
        return detectedFaces;
    }

    public void setDetectedFaces(int detectedFaces) {
        this.detectedFaces.set(detectedFaces);
    }

    public IplImage getOriginalIplImage() {
        return originalIplImage;
    }

    public void setOriginalIplImage(IplImage originalIplImage) {
        this.originalIplImage = originalIplImage;
    }


}
