package muffinc.frog.eigenface;

import org.bytedeco.javacpp.opencv_core.*;
import java.util.Arrays;

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
public class Face implements Cloneable {
    final String faceId;

    double[] faceVector;
    double[] faceCoeficients;

    public Face(String faceId, double[] faceVector) {
        this.faceVector = faceVector;
        this.faceId = faceId;
    }

    public Face(String faceID, IplImage img) {
        this.faceId = faceID;

    }

    public double[] getFaceVector() {
        return faceVector;
    }

    public double[] getFaceCoeficients() {
        return faceCoeficients;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceCoeficients(double[] faceCoeficients) {
        this.faceCoeficients = faceCoeficients;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(faceVector);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return faceId;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        super.clone();
        return new Face(faceId, Arrays.copyOf(faceVector, faceVector.length));
    }

    public Face deepCopy() {
        try {
            Face f = (Face)this.clone();
            return f;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }


}
