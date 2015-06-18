package muffinc.frog.helper;

import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
public class FeretHandler {

    public static final String FOLDER = "/Users/Meth/Documents/FROG/src/test/resources/FERET1/";

    public static final String NEW_FOLDER = "/Users/Meth/Documents/FROG/src/test/resources/FERET1Sorted/";

    public static void move() {

        File file = new File(FOLDER);

        Path path = file.toPath();

        FileFilter fileFilter = new RegexFileFilter("0\\d{4}f.*tif");

        for (File file1 : file.listFiles(fileFilter)) {
            move(file1);
        }
    }

    public static void move(File file) {
        try {

            String newFolder = NEW_FOLDER + file.getName().substring(1, 5) + "/";
            File file1 = new File(newFolder);

            if (!file1.exists()) {
                file1.mkdirs();
            }

            Files.copy(file.toPath(), new File(newFolder + file.getName()).toPath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        moveToHumans();
    }

    public static void moveToHumans() {
        File file = new File(NEW_FOLDER);

        for (File file1 : file.listFiles()) {
            if (file1.isDirectory()) {
                for (File file2 : file1.listFiles(((FileFilter) new RegexFileFilter("\\w{10}d.*")))) {

                    try {
                        String newFolder = "/Users/Meth/Documents/FROG/src/test/resources/Humans/" + "H_" + file2.getName().substring(1,5) + "/";

                        File file3 = new File(newFolder);

                        if (!file3.exists()) {
                            file3.mkdirs();
                        }

                        Files.copy(file2.toPath(), new File(newFolder + file2.getName()).toPath());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public static void rename(String s) {
        for (File file : new File(s).listFiles()) {
            if (file.isDirectory()) {
                int size = file.listFiles(((FileFilter) new WildcardFileFilter("*.tif"))).length;

                file.renameTo(new File(file.getParent() + "/" + file.getName() + "_" + size));

            }
        }
    }
}
