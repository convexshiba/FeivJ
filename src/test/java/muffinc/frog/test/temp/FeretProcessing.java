package muffinc.frog.test.temp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
public class FeretProcessing {

    public static void move() {
        Path original = new File("/Users/Meth/Documents/FROG/src/test/resources/testtesttest/00108fb011d_931230.jpeg").toPath();
        Path newPath = new File("/Users/Meth/Documents/FROG/src/test/resources/testtesttest/1/00108fb011d_931230.jpeg").toPath();
        new File("/Users/Meth/Documents/FROG/src/test/resources/testtesttest/1").mkdir();
        try {
            Files.move(original, newPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        move();
    }
}
