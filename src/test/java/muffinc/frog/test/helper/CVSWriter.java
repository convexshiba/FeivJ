package muffinc.frog.test.helper;

import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

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
public class CVSWriter {

    private ICsvListWriter csvWriter = null;
    public String path;

    public CVSWriter(String fileName) {
        try {
            path = "/Users/Meth/Documents/FROG/src/test/data/" + fileName;
            csvWriter = new CsvListWriter(new FileWriter(path),
                    CsvPreference.STANDARD_PREFERENCE);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeCsv(String[][] csvMatrix) {
        try {
            for (int i = 0; i < csvMatrix.length; i++) {
                csvWriter.write(csvMatrix[i]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                csvWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
         }

    }
}
