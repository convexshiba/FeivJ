package muffinc.frog.test.detection;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.CanvasFrame;

import javax.swing.*;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_objdetect.*;

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
public class FaceDetection_Obselete {


    public static final String XML_FILE =
            "/Users/Meth/Dropbox/Thesis/FROG/src/test/resources/xml/haarcascade_frontalface_default.xml";

    public static void main(String[] args) {
        long total = 0;
        for (int i = 1; i >= 1; i--) {
            IplImage img = cvLoadImage("/Users/Meth/Dropbox/Thesis/FROG/src/test/resources/FERET2/00728fa010_941201.tif");
            long t1 = System.currentTimeMillis();
            detect(img);
            total += System.currentTimeMillis() - t1;
        }
        System.out.println(total / 200);
    }

    public static void detect(IplImage img) {
        CvHaarClassifierCascade cascade =
                new CvHaarClassifierCascade(cvLoad(XML_FILE));

        CvMemStorage storage = CvMemStorage.create();

        CvSeq sign = cvHaarDetectObjects(
                img,
                cascade,
                storage,
                1.5,
                3,
                CV_HAAR_DO_CANNY_PRUNING);

        cvClearMemStorage(storage);

        System.out.println("sign = " + sign.total());

        // Total Detected Faces
        int totalFaces = sign.total();

        // Tag all faces
        for (int i = 0; i < totalFaces; i++) {
            CvRect rect = new CvRect(cvGetSeqElem(sign, i));
            cvRectangle(
                    img,
                    cvPoint(rect.x(), rect.y()),
                    cvPoint(rect.width() + rect.x(), rect.height() + rect.y()),
                    CvScalar.RED,
                    2,
                    CV_AA,
                    0);
        }
        CanvasFrame canvas = new CanvasFrame("LuckyFrame");
        canvas.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        canvas.showImage(img);
        canvas.pack();
//        cvShowImage("Detection Result", img);
//        cvWaitKey(0);
    }

}
