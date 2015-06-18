package muffinc.frog.test.eigenface.metric;

import muffinc.frog.test.Jama.Matrix;
import muffinc.frog.test.Jama.util.JamaUtils;
import muffinc.frog.test.common.Metric;

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
public class CosineDissimilarity implements Metric {
    @Override
    public double getDistance(Matrix m1, Matrix m2) {
        assert m1.getRowDimension() == m2.getRowDimension();
        int size = m1.getRowDimension();
        double m1Norm = m1.normF();
        double m2Norm = m2.normF();
        double dotProduct = Math.abs(JamaUtils.dotproduct(m1, m2));

        double cosine = dotProduct / (m1Norm * m2Norm);

        if (cosine == 0) {
            return Double.MAX_VALUE;
        } else {
            return 1 / cosine;
        }
    }
}
