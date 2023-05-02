/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.natives;

import org.adamalang.runtime.stdlib.LibMath;

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

  @Override
  public String toString() {
    return real + " " + imaginary + "i";
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
}
