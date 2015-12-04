/*
 * Copyright (c) 2015.  Jun Zhou
 *
 * YaF-DIVJ, Yet another Face Detection Image Viewer in Java
 * <p/>
 * This file is part of YaF-DIVJ.
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

package muffinc.yafdivj.eigenface;

import muffinc.yafdivj.datatype.Metric;
import muffinc.yafdivj.eigenface.metric.EuclideanDistance;
import muffinc.yafdivj.eigenface.metric.L1Distance;
import muffinc.yafdivj.datatype.YafImg;
import muffinc.yafdivj.datatype.YafTrainImg;
import muffinc.yafdivj.Jama.Matrix;
import muffinc.yafdivj.datatype.Human;
import muffinc.yafdivj.eigenface.metric.CosineDissimilarity;
import muffinc.yafdivj.helper.ImageHelper;
import org.bytedeco.javacpp.opencv_core;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TrainingEngine {


    public static final int COMPONENT_NUMBER = 18;
    public static final double ID_THRESHOLD = 3000;
    public static final double IS_FACE_THRESHOLD = 3500;
    public static final int METRIC_COSINE = 0;
    public static final int METRIC_L1D = 1;
    public static final int METRIC_EUCILDEAN = 2;

    public static final String HUMAN_DIRECTORY = "Humans";
    public static final String TRAINING_DIRECROTY = "trainingSet";

    public HumanFactory humanFactory;
    public PCA pca;

    ArrayList<YafTrainImg> trainingImgSet = new ArrayList<>();


    private Metric euclidean = new EuclideanDistance();


    public TrainingEngine() {

        File trainingFolder = new File(getClass().getClassLoader().getResource(TRAINING_DIRECROTY).getPath());


        OUTER:
        for (File people : trainingFolder.listFiles()) {
            if (people.isDirectory()) {
                for (File photo : people.listFiles()) {
                    try {
                        Matrix temp = FileManager.convertPGMtoMatrix(photo);

                        YafTrainImg yafTrainImg = new YafTrainImg(photo, temp, this);

                        yafTrainImg.setVectorized(ImageHelper.vectorize(temp));
                        trainingImgSet.add(yafTrainImg);

                        if(trainingImgSet.size() == 200) {
                            break OUTER;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }



        try {
            pca = new PCA(trainingImgSet, COMPONENT_NUMBER, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        humanFactory = new HumanFactory(this);
    }

    public YafImg addNewImg(File file, boolean doScan) {
        if (humanFactory.frogImgTable.containsKey(file)) {
            System.out.println(file.getAbsolutePath() + " is already in the library");
            return humanFactory.frogImgTable.get(file);
        } else {
            YafImg yafImg = new YafImg(file, this);
            humanFactory.frogImgTable.put(file, yafImg);
            if (doScan) {
                yafImg.detectAndID();
            }
            return yafImg;
        }
    }

    //TODO delete not yet finished
    public void removeImg(YafImg yafImg) {

        humanFactory.frogImgTable.remove(yafImg.getFile());
    }


    public static Metric getMetric(int i) {
        switch (i) {
            case METRIC_COSINE:
                return new CosineDissimilarity();
            case METRIC_L1D:
                return new L1Distance();
            case METRIC_EUCILDEAN:
                return new EuclideanDistance();
            default:
                throw new IllegalArgumentException("Please use a valid Metric");
        }
    }


    public void projectAllCvRect(YafImg yafImg) {
        for (opencv_core.CvRect cvRect : yafImg.getCvRects()) {
            yafImg.idMatrices.put(cvRect, pca.project(FileManager.getColMatrix(yafImg, cvRect)));
        }
    }

    public Human IDCvRectInFrogImg(YafImg yafImg, opencv_core.CvRect cvRect) {
        Matrix thisID = yafImg.idMatrices.get(cvRect);

        Human minHuman = null;
        double min = Double.MAX_VALUE;

        for (Human human : humanFactory.nameTable.values()) {
            if (human.hasIDMatrix()) {
                if (euclidean.getDistance(thisID, human.getIdMatrix()) < (IS_FACE_THRESHOLD < min ? IS_FACE_THRESHOLD : min)) {
                    minHuman = human;
                    min = euclidean.getDistance(thisID, human.getIdMatrix());

                }
            }
        }
        return minHuman;
    }


}
