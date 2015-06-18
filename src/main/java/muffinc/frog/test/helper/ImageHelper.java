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
    public static String getImgType(int imgTypeInt)
    {
        int numImgTypes = 35; // 7 base types, with five channel options each (none or C1, ..., C4)

        int enum_ints[] = {CV_8U,  CV_8UC1,  CV_8UC2,  CV_8UC3,  CV_8UC4,
                CV_8S,  CV_8SC1,  CV_8SC2,  CV_8SC3,  CV_8SC4,
                CV_16U, CV_16UC1, CV_16UC2, CV_16UC3, CV_16UC4,
                CV_16S, CV_16SC1, CV_16SC2, CV_16SC3, CV_16SC4,
                CV_32S, CV_32SC1, CV_32SC2, CV_32SC3, CV_32SC4,
                CV_32F, CV_32FC1, CV_32FC2, CV_32FC3, CV_32FC4,
                CV_64F, CV_64FC1, CV_64FC2, CV_64FC3, CV_64FC4};

        String enum_strings[] = {"CV_8U",  "CV_8UC1",  "CV_8UC2",  "CV_8UC3",  "CV_8UC4",
                "CV_8S",  "CV_8SC1",  "CV_8SC2",  "CV_8SC3",  "CV_8SC4",
                "CV_16U", "CV_16UC1", "CV_16UC2", "CV_16UC3", "CV_16UC4",
                "CV_16S", "CV_16SC1", "CV_16SC2", "CV_16SC3", "CV_16SC4",
                "CV_32S", "CV_32SC1", "CV_32SC2", "CV_32SC3", "CV_32SC4",
                "CV_32F", "CV_32FC1", "CV_32FC2", "CV_32FC3", "CV_32FC4",
                "CV_64F", "CV_64FC1", "CV_64FC2", "CV_64FC3", "CV_64FC4"};

        for(int i=0; i<numImgTypes; i++)
        {
            if(imgTypeInt == enum_ints[i]) return enum_strings[i];
        }
        return "unknown image type";
    }

    public static IplImage toGrey(IplImage img) {
        if (img.nChannels() != 1) {
            IplImage greyImg = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
            cvCvtColor(img, greyImg, CV_BGR2GRAY);
            return greyImg;
        } else {
            return img;
        }
    }

    public static IplImage resize(IplImage img) {
//        IplImage greyImg = toGrey(img);
//        CvSize size = cvSize(PCA.FACE_WIDTH, PCA.FACE_HEIGHT);

        IplImage resizedImg = IplImage.create(PCA.FACE_WIDTH, PCA.FACE_HEIGHT, IPL_DEPTH_8U, 1);

        cvResize(img, resizedImg);

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

    public static Matrix vectorize(Matrix input){
        int m = input.getRowDimension();
        int n = input.getColumnDimension();

        Matrix result = new Matrix(m*n,1);
        for(int p = 0; p < n; p ++){
            for(int q = 0; q < m; q ++){
                result.set(p*m+q, 0, input.get(q, p));
            }
        }
        return result;
    }
}
