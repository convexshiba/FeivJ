package muffinc.frog.test.detection;

import muffinc.frog.test.Jama.Matrix;
import muffinc.frog.test.displayio.Display;
import muffinc.frog.test.eigenface.FileManager;
import muffinc.frog.test.eigenface.TrainingEngine;
import muffinc.frog.test.helper.FileHelper;
import muffinc.frog.test.helper.ImageHelper;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.JavaCvErrorCallback;


import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

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
public class FaceDetection {

    // The cascade definition to be used for detection.
    public static final String CASCADE_FILE =
            "/Users/Meth/Documents/FROG/src/test/resources/xml/haarcascade_frontalface_alt.xml";

    @Deprecated
    public static final String FILE =
            "/Users/Meth/Documents/FROG/src/test/resources/iph/IMG_0129.jpeg";

    public static ArrayList<CvRect> detectFaces(IplImage img) {
        IplImage greyImg = img.clone();
        if (greyImg.nChannels() != 1) {
            greyImg = ImageHelper.toGrey(greyImg);
        }

        CvMemStorage storage = CvMemStorage.create();

        CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(cvLoad(CASCADE_FILE));

        CvSeq faces = cvHaarDetectObjects(greyImg, cascade, storage, 1.1, 1, CV_HAAR_SCALE_IMAGE);

        cvClearMemStorage(storage);

        ArrayList<CvRect> rects = new ArrayList<>();

        for (int i = 0; i < faces.total(); i++) {
            CvRect rect = new CvRect(cvGetSeqElem(faces, i));
            rect = growRect(rect);
            rects.add(rect);
        }
        return rects;
    }

    public static ArrayList<CvRect> detectFaces(File file) {
        return detectFaces(cvLoadImage(file.getAbsolutePath(), 1));
    }


    @Deprecated
    public static void main(String[] args) throws Exception {
        // This will redirect the OpenCV errors to the Java console to give you
        // feedback about any problems that may occur.
        new JavaCvErrorCallback();

        // Load the original image.
        IplImage originalImage = cvLoadImage(FILE, 1);

        // We need a grayscale image in order to do the recognition, so we
        // create a new image of the same size as the original one.
        IplImage grayImage = IplImage.create(originalImage.width(),
                originalImage.height(), IPL_DEPTH_8U, 1);

        // We convert the original image to grayscale.
        cvCvtColor(originalImage, grayImage, CV_BGR2GRAY);

        // Save greysacle image
//        cvSaveImage(FileHelper.addNameSuffix(FILE, "grey"), grayImage);

        CvMemStorage storage = CvMemStorage.create();

        // We instantiate a classifier cascade to be used for detection, using the cascade definition.
        CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(
                cvLoad(CASCADE_FILE));

        // We detect the faces.
        CvSeq faces = cvHaarDetectObjects(grayImage, cascade, storage, 1.1, 1, CV_HAAR_SCALE_IMAGE);

        // Clear storage.
        cvClearMemStorage(storage);

        //We iterate over the discovered faces and draw yellow rectangles around them.
        for (int i = 0; i < faces.total(); i++) {
            CvRect r = new CvRect(cvGetSeqElem(faces, i));

            r = growRect(r);
            TrainingEngine trainingEngine = new TrainingEngine();

//            cvRectangle(originalImage, cvPoint(r.x(), r.y()),
//                        cvPoint(r.x() + r.width(), r.y() + r.height()), CvScalar.YELLOW, 1, CV_AA, 0);

            cvSetImageROI(grayImage, r);
            cvSaveImage(FileHelper.addNameSuffix(FILE, "cropped" + i), grayImage);
            cvResetImageROI(grayImage);

            cvRectangle(originalImage, cvPoint(r.x(), r.y()),
                    cvPoint(r.x() + r.width(), r.y() + r.height()), CvScalar.YELLOW, 1, CV_AA, 0);
        }

        // Save the image to a new file.
//        cvSaveImage(FileHelper.addNameSuffix(FILE, "detected"), originalImage);
        Display.display(originalImage);

    }


    public static CvRect growRect(CvRect cvRect) {
        int x = cvRect.x();
        int y = cvRect.y();
        int h_temp = cvRect.height();
        int w_temp = cvRect.width();

        x -= w_temp * 0;
        y -= h_temp * 0.2;
        h_temp *= 1.3;
        w_temp *= 1;

        return cvRect(x, y, w_temp, h_temp);
    }

    @Deprecated
    public static boolean isRectFace(CvRect cvRect, IplImage img, TrainingEngine trainingEngine) {

        cvSetImageROI(img, cvRect);
        IplImage newImg = cvCreateImage(cvGetSize(img), IPL_DEPTH_8U, 1);
        cvCopy(img, newImg);
        cvResetImageROI(img);

//        Display.display(newImg);
        Matrix matrix = ImageHelper.getMatrixFromGrey(ImageHelper.resize(newImg));
        BufferedImage image = FileManager.convertColMatrixToImage(TrainingEngine.vectorize(matrix));
        Display.display(image);

        return trainingEngine.pca.isMatrixFace(matrix);
    }

}
