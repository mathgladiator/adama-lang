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

import org.adamalang.runtime.stdlib.LibMath;

/** a complex number */
public class NtComplex {
  public double real;
  public double imaginary;

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

  public boolean zero() {
    return LibMath.near(real, 0) && LibMath.near(imaginary, 0);
  }

  public NtComplex recip() {
    double len2 = real * real + imaginary * imaginary;
    return new NtComplex(real / len2, -imaginary / len2);
  }

  public long memory() {
    return 16;
  }

  @Override
  public String toString() {
    return real + " " + imaginary + "i";
  }

  public NtComplex opAddTo(int x) {
    real += x;
    return this;
  }

  public NtComplex opAddTo(long x) {
    real += x;
    return this;
  }

  public NtComplex opAddTo(double x) {
    real += x;
    return this;
  }

  public NtComplex opAddTo(NtComplex x) {
    real += x.real;
    imaginary += x.imaginary;
    return this;
  }

  public NtComplex opSubFrom(NtComplex x) {
    real -= x.real;
    imaginary -= x.imaginary;
    return this;
  }

  public NtComplex opMultBy(double x) {
    real *= x;
    imaginary *= x;
    return this;
  }

  public NtComplex opMultBy(NtComplex x) {
    double nReal = real * x.real - imaginary * x.imaginary;
    imaginary = real * x.imaginary + imaginary * x.real;
    real = nReal;
    return this;
  }

  public NtComplex copy() {
    return new NtComplex(real, imaginary);
  }
}
