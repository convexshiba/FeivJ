package muffinc.frog.userinterface;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import muffinc.frog.object.FrogImg;

import java.io.Serializable;

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
public class PhotoGem implements Serializable{
    private IntegerProperty photoCount;
//    private Image img;
    private StringProperty fileName;
    private StringProperty location;
    private FrogImg frogImg;

    public PhotoGem(FrogImg frogImg) {

        this.frogImg = frogImg;

        photoCount = frogImg.detectedFaces;

//        img = SwingFXUtils.toFXImage(frogImg.currentIplImage.getBufferedImage(), null);

        fileName = new SimpleStringProperty(frogImg.getFile().getName());

        location = new SimpleStringProperty(frogImg.getFile().getAbsolutePath());
    }

    public String getLocation() {
        return location.get();
    }

    public StringProperty locationProperty() {
        return location;
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public FrogImg getFrogImg() {
        return frogImg;
    }

    public String getFileName() {
        return fileName.get();
    }

    public StringProperty fileNameProperty() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName.set(fileName);
    }

    public IntegerProperty photoCountProperty() {
        return photoCount;
    }

    public void setPhotoCount(int photoCount) {
        this.photoCount.set(photoCount);
    }

//    public Image getImg() {
//        return img;
//    }
//
//    public void setImg(Image img) {
//        this.img = img;
//    }
}
