package muffinc.frog.test.helper;

import static org.bytedeco.javacpp.opencv_core.*;

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
}
