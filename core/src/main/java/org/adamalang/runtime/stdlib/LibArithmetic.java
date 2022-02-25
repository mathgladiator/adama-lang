/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtComplex;
import org.adamalang.runtime.natives.NtMaybe;

/**
 * Since division is complicated, we need to think about what it means to build up arithmetic using
 * maybe<N> where N is a numeric type of int, long, double
 */
public class LibArithmetic {

  public static class Divide {

    // Left=Integer
    public static NtMaybe<Double> II(int x, int y) {
      double z = ((double) x) / y;
      if (Double.isNaN(z) || Double.isInfinite(z)) {
        return new NtMaybe<>();
      }
      return new NtMaybe<>(z);
    }

    public static NtMaybe<Double> IL(int x, long y) {
      double z = ((double) x) / y;
      if (Double.isNaN(z) || Double.isInfinite(z)) {
        return new NtMaybe<>();
      }
      return new NtMaybe<>(z);
    }

    public static NtMaybe<Double> ID(int x, double y) {
      double z = ((double) x) / y;
      if (Double.isNaN(z) || Double.isInfinite(z)) {
        return new NtMaybe<>();
      }
      return new NtMaybe<>(z);
    }

    public static NtMaybe<NtComplex> IC(int x, NtComplex y) {
      if (y.zero()) {
        return new NtMaybe<>();
      }
      NtComplex r = y.recip();
      return new NtMaybe<>(new NtComplex(r.real * x, r.imaginary * x));
    }

    // Left=Long
    public static NtMaybe<Double> LI(long x, int y) {
      double z = ((double) x) / y;
      if (Double.isNaN(z) || Double.isInfinite(z)) {
        return new NtMaybe<>();
      }
      return new NtMaybe<>(z);
    }

    public static NtMaybe<Double> LL(long x, long y) {
      double z = ((double) x) / y;
      if (Double.isNaN(z) || Double.isInfinite(z)) {
        return new NtMaybe<>();
      }
      return new NtMaybe<>(z);
    }

    public static NtMaybe<Double> LD(long x, double y) {
      double z = ((double) x) / y;
      if (Double.isNaN(z) || Double.isInfinite(z)) {
        return new NtMaybe<>();
      }
      return new NtMaybe<>(z);
    }

    public static NtMaybe<NtComplex> LC(long x, NtComplex y) {
      if (y.zero()) {
        return new NtMaybe<>();
      }
      NtComplex r = y.recip();
      return new NtMaybe<>(new NtComplex(r.real * x, r.imaginary * x));
    }

    // left=Double
    public static NtMaybe<Double> DI(double x, int y) {
      double z = x / y;
      if (Double.isNaN(z) || Double.isInfinite(z)) {
        return new NtMaybe<>();
      }
      return new NtMaybe<>(z);
    }

    public static NtMaybe<Double> DL(double x, long y) {
      double z = x / y;
      if (Double.isNaN(z) || Double.isInfinite(z)) {
        return new NtMaybe<>();
      }
      return new NtMaybe<>(z);
    }

    public static NtMaybe<Double> DD(double x, double y) {
      double z = x / y;
      if (Double.isNaN(z) || Double.isInfinite(z)) {
        return new NtMaybe<>();
      }
      return new NtMaybe<>(z);
    }

    public static NtMaybe<NtComplex> DC(double x, NtComplex y) {
      if (y.zero()) {
        return new NtMaybe<>();
      }
      NtComplex r = y.recip();
      return new NtMaybe<>(new NtComplex(r.real * x, r.imaginary * x));
    }

    // Left=Complex

    public static NtMaybe<NtComplex> CI(NtComplex x, int y) {
      if (y == 0) {
        return new NtMaybe<>();
      }
      return new NtMaybe<>(new NtComplex(x.real / y, x.imaginary / y));
    }

    public static NtMaybe<NtComplex> CL(NtComplex x, long y) {
      if (y == 0) {
        return new NtMaybe<>();
      }
      return new NtMaybe<>(new NtComplex(x.real / y, x.imaginary / y));
    }

    public static NtMaybe<NtComplex> CD(NtComplex x, double y) {
      if (LibMath.near(y, 0)) {
        return new NtMaybe<>();
      }
      return new NtMaybe<>(new NtComplex(x.real / y, x.imaginary / y));
    }

    public static NtMaybe<NtComplex> CC(NtComplex x, NtComplex y) {
      if (y.zero()) {
        return new NtMaybe<>();
      }
      NtComplex r = y.recip();
      return new NtMaybe<>(new NtComplex(x.real * r.real - x.imaginary * y.imaginary, x.real * y.imaginary + x.imaginary * y.real));
    }








    public static NtMaybe<Double> DD(NtMaybe<Double> x, double y) {
      if (x.has()) {
        return DD(x.get(), y);
      }
      return x;
    }

    public static NtMaybe<Double> DD(double x, NtMaybe<Double> y) {
      if (y.has()) {
        return DD(x, y.get());
      }
      return y;
    }

    public static NtMaybe<Double> DD(NtMaybe<Double> x, NtMaybe<Double> y) {
      if (x.has()) {
        if (y.has()) {
          return DD(x.get(), y.get());
        }
        return y;
      } else {
        return x;
      }
    }

    public static NtMaybe<NtComplex> CmD(NtComplex x, NtMaybe<Double> mY) {
      if (mY.has()) {
        double y = mY.get();
        if (LibMath.near(y, 0)) {
          return new NtMaybe<>();
        }
        return new NtMaybe<>(new NtComplex(x.real / y, x.imaginary / y));
      }
      return new NtMaybe<>();
    }

    public static NtMaybe<NtComplex> mDC(NtMaybe<Double> x, NtComplex y) {
      if (!x.has() || y.zero()) {
        return new NtMaybe<>();
      }
      NtComplex r = y.recip();
      return new NtMaybe<>(new NtComplex(r.real * x.get(), r.imaginary *  x.get()));
    }
  }






  /*
  public static NtComplex addCI(NtComplex x, int y) {
    return new NtComplex(x.real + y, x.imaginary);
  }
  public static NtComplex addCL(NtComplex x, long y) {
    return new NtComplex(x.real + y, x.imaginary);
  }
  public static NtComplex addCD(NtComplex x, double y) {
    return new NtComplex(x.real + y, x.imaginary);
  }
  public static NtComplex addCC(NtComplex x, NtComplex y) {
    return new NtComplex(x.real + y.real, x.imaginary + y.imaginary);
  }
  */
}
