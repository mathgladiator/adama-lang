/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.natives;

/** a complex number */
public class NtComplex {
    public final double real;
    public final double imaginary;

    public NtComplex(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NtComplex ntComplex = (NtComplex) o;
        return Double.compare(ntComplex.real, real) == 0 && Double.compare(ntComplex.imaginary, imaginary) == 0;
    }

    public long memory() {
        return 16;
    }
}
