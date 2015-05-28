package muffinc.frog.test.helper;

import muffinc.frog.test.Jama.Matrix;
import muffinc.frog.test.displayio.Display;
import muffinc.frog.test.eigenface.*;
import muffinc.frog.test.eigenface.PCA;

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
public class ImageHelper {

    public static IplImage toGrey(IplImage img) {
        IplImage greyImg = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
        cvCvtColor(img, greyImg, CV_BGR2GRAY);
        return greyImg;
    }

    public static IplImage resize(IplImage img) {
//        IplImage greyImg = toGrey(img);
//        CvSize size = cvSize(PCA.FACE_WIDTH, PCA.FACE_HEIGHT);

        IplImage resizedImg = IplImage.create(PCA.FACE_WIDTH, PCA.FACE_HEIGHT, IPL_DEPTH_8U, 1);

        cvResize(img, resizedImg);

        Display.display(resizedImg);

        return resizedImg;
    }

    public static Matrix getMatrixFromGrey(IplImage img) {
        CvMat mat = img.asCvMat();
        double[][] array = new double[img.height()][img.width()];
        for (int i = 0; i < mat.height(); i++) {
            for (int j = 0; j < mat.width(); j++) {
                array[i][j] = cvGet2D(mat, i, j).get(0);
            }
        }
        return new Matrix(array);
    }
}
