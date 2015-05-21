package muffinc.frog.test.detection;

import muffinc.frog.test.displayio.Display;
import muffinc.frog.test.helper.FileHelper;
import muffinc.frog.test.helper.ImageHelper;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.JavaCvErrorCallback;


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
public class FaceDetectionII {

    // The cascade definition to be used for detection.
    public static final String CASCADE_FILE =
            "/Users/Meth/Documents/FROG/src/test/resources/xml/haarcascade_frontalface_alt.xml";

    public static final String FILE =
            "/Users/Meth/Documents/FROG/src/test/resources/testtesttest copy/201404201524468276e.jpg";


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
            cvRectangle(originalImage, cvPoint(r.x(), r.y()),
                    cvPoint(r.x() + r.width(), r.y() + r.height()), CvScalar.YELLOW, 1, CV_AA, 0);
        }

        // Save the image to a new file.
//        cvSaveImage(FileHelper.addNameSuffix(FILE, "detected"), originalImage);
        Display.display(originalImage.getBufferedImage());
    }

    public static ArrayList<CvRect> detectFaces(IplImage img) {
        IplImage greyImg = img.clone();
        if (img.nChannels() != 1) {
            greyImg = ImageHelper.toGrey(greyImg);
        }

        CvMemStorage storage = CvMemStorage.create();

        CvHaarClassifierCascade cascade = new CvHaarClassifierCascade(cvLoad(CASCADE_FILE));

        CvSeq faces = cvHaarDetectObjects(greyImg, cascade, storage, 1.1, 1, CV_HAAR_SCALE_IMAGE);

        cvClearMemStorage(storage);

        ArrayList<CvRect> rects = new ArrayList<CvRect>();

        for (int i = 0; i < faces.total(); i++) {
            CvRect rect = new CvRect(cvGetSeqElem(faces, i));
            Rect rectEnlarged = new Rect(rect.x(), rect.y(), rect.width(), rect.height());
            rects.add(rect);
        }
        return rects;
    }

}
