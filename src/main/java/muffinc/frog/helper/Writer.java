package muffinc.frog.helper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

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
public class Writer {

    String path;
    BufferedWriter writer;
    Object origin;

    public Writer(String filename, Object o) {
        try {
            path = "/Users/Meth/Documents/FROG/src/test/data/" + filename;
            writer = new BufferedWriter(new FileWriter(path));
            origin = o;
            System.out.println("Writer created in "+o.getClass().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Writer(String filename) {
        try {
            path = "/Users/Meth/Documents/FROG/src/test/data/" + filename;
            writer = new BufferedWriter(new FileWriter(path));

            System.out.println("Writer created in a static class");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(int i) {
        write(String.valueOf(i));
    }

    public void write(double d) {
        write(String.valueOf(d));
    }

    public void write(String str) {
        try {
            writer.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void newLine() {
        write("\n");
    }

    public void close() {
        try {
            writer.close();
            System.out.println("Write Successful at " + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
