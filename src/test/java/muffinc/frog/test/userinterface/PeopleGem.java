package muffinc.frog.test.userinterface;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import muffinc.frog.test.object.Human;

import java.io.Serializable;

/**
 * FROG, a Face Recognition Gallery in Java
 * Copyright (C) 2015 Jun Zhou
 * <p>
 * This file is part of FROG.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 * zj45499 (at) gmail (dot) com
 */
public class PeopleGem implements Serializable{

    private IntegerProperty photoNumber;
    private StringProperty name;
    private Human human;


    public PeopleGem(Human human) {
        this.human = human;
        name = new SimpleStringProperty(human.name);
        photoNumber = human.fileNumber;
    }

    public int getPhotoNumber() {
        return photoNumber.get();
    }

    public IntegerProperty photoNumberProperty() {
        return photoNumber;
    }

    public void setPhotoNumber(int photoNumber) {
        this.photoNumber.set(photoNumber);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Human getHuman() {
        return human;
    }

    public void setHuman(Human human) {
        this.human = human;
    }
}
