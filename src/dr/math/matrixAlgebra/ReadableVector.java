/*
 * WrappedVector.java
 *
 * Copyright (c) 2002-2017 Alexei Drummond, Andrew Rambaut and Marc Suchard
 *
 * This file is part of BEAST.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership and licensing.
 *
 * BEAST is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 *  BEAST is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with BEAST; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301  USA
 */

package dr.math.matrixAlgebra;


import dr.inference.model.Parameter;

/**
 * @author Marc A. Suchard
 */

public interface ReadableVector {

    double get(final int i);

    int getDim();

    class Sum implements ReadableVector {

        private final ReadableVector lhs;
        private final ReadableVector rhs;

        public Sum(final ReadableVector lhs, final ReadableVector rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }

        @Override
        public double get(int i) {
            return lhs.get(i) + rhs.get(i);
        }

        @Override
        public int getDim() {
            return Math.min(lhs.getDim(), rhs.getDim());
        }
    }

    class Quotient implements ReadableVector {

        private final ReadableVector numerator;
        private final ReadableVector denominator;

        public Quotient(final ReadableVector numerator, final ReadableVector denominator) {
            this.numerator = numerator;
            this.denominator = denominator;
        }

        @Override
        public double get(int i) {
            return numerator.get(i) / denominator.get(i);
        }

        @Override
        public int getDim() {
            return Math.min(numerator.getDim(), denominator.getDim());
        }
    }

    class Product implements ReadableVector {

        private final ReadableVector lhs;
        private final ReadableVector rhs;

        public Product(final ReadableVector lhs, final ReadableVector rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }

        @Override
        public double get(int i) {
            return lhs.get(i) * rhs.get(i);
        }

        @Override
        public int getDim() {
            return Math.min(lhs.getDim(), rhs.getDim());
        }
    }

    class Scale implements ReadableVector {

        private final ReadableVector vector;
        private final double scalar;

        public Scale(final double scalar, final ReadableVector vector) {
            this.vector = vector;
            this.scalar = scalar;
        }

        @Override
        public double get(int i) { return scalar * vector.get(i); }

        @Override
        public int getDim() { return vector.getDim(); }
    }

    class Utils {

        public static void setParameter(ReadableVector position, Parameter parameter) {
            for (int j = 0, dim = position.getDim(); j < dim; ++j) {
                parameter.setParameterValueQuietly(j, position.get(j));
            }
            parameter.fireParameterChangedEvent();
        }

        public static double innerProduct(ReadableVector lhs, ReadableVector rhs) {

            assert (lhs.getDim() == rhs.getDim());

            double sum = 0;
            for (int i = 0, dim = lhs.getDim(); i < dim; ++i) {
                sum += lhs.get(i) * rhs.get(i);
            }

            return sum;
        }

        public static double norm(ReadableVector vector) {

            return Math.sqrt(innerProduct(vector, vector));
        }
    }
}
