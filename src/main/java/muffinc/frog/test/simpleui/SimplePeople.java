package muffinc.frog.test.simpleui;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import muffinc.frog.test.object.Human;

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
public class SimplePeople {
    private final StringProperty name;
    private final IntegerProperty fileNums;
//    private final StringProperty fileAddress;

    public SimplePeople(StringProperty name, IntegerProperty fileNums, StringProperty fileAddress) {
        this.name = name;
        this.fileNums = fileNums;
//        this.fileAddress = fileAddress;
    }

    public SimplePeople(Human human) {
        name = new SimpleStringProperty(human.name);
        fileNums = human.fileNumber;
//        fileAddress = new SimpleStringProperty(human.frogImgs.get(0).getFile().getAbsolutePath());
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

    public int getFileNums() {
        return fileNums.get();
    }

    public IntegerProperty fileNumsProperty() {
        return fileNums;
    }

    public void setFileNums(int fileNums) {
        this.fileNums.set(fileNums);
    }

//    public String getFileAddress() {
//        return fileAddress.get();
//    }
//
//    public StringProperty fileAddressProperty() {
//        return fileAddress;
//    }
//
//    public void setFileAddress(String fileAddress) {
//        this.fileAddress.set(fileAddress);
//    }
}
