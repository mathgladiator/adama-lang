/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.runtime.natives;

import org.adamalang.runtime.stdlib.LibMath;

import java.util.Objects;

/** a complex number */
public class NtComplex implements Comparable<NtComplex> {
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

  @Override
  public int hashCode() {
    return Objects.hash(real, imaginary);
  }

  @Override
  public int compareTo(NtComplex o) {
    int delta = Double.compare(real, o.real);
    if (delta == 0) {
      return Double.compare(imaginary, o.imaginary);
    }
    return delta;
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
