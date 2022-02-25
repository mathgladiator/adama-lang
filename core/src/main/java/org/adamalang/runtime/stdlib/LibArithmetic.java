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

    public static NtMaybe<Double> ImD(int x, NtMaybe<Double> y) {
      if (y.has()) {
        return ID(x, y.get());
      }
      return y;
    }

    public static NtMaybe<NtComplex> ImC(int x, NtMaybe<NtComplex> y) {
      if (y.has()) {
        return IC(x, y.get());
      }
      return y;
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
    public static NtMaybe<Double> LmD(long x, NtMaybe<Double> y) {
      if (y.has()) {
        return LD(x, y.get());
      }
      return y;
    }

    public static NtMaybe<NtComplex> LC(long x, NtComplex y) {
      if (y.zero()) {
        return new NtMaybe<>();
      }
      NtComplex r = y.recip();
      return new NtMaybe<>(new NtComplex(r.real * x, r.imaginary * x));
    }

    public static NtMaybe<NtComplex> LmC(long x, NtMaybe<NtComplex> y) {
      if (y.has()) {
        return LC(x, y.get());
      }
      return y;
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

    public static NtMaybe<Double> DmD(double x, NtMaybe<Double> y) {
      if (y.has()) {
        return DD(x, y.get());
      }
      return y;
    }

    public static NtMaybe<NtComplex> DC(double x, NtComplex y) {
      if (y.zero()) {
        return new NtMaybe<>();
      }
      NtComplex r = y.recip();
      return new NtMaybe<>(new NtComplex(r.real * x, r.imaginary * x));
    }

    public static NtMaybe<NtComplex> DmC(double x, NtMaybe<NtComplex> y) {
      if (y.has()) {
        return DC(x, y.get());
      }
      return y;
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

    public static NtMaybe<NtComplex> CmD(NtComplex x, NtMaybe<Double> y) {
      if (y.has()) {
        return CD(x, y.get());
      }
      return new NtMaybe<>();
    }

    public static NtMaybe<NtComplex> CC(NtComplex x, NtComplex y) {
      if (y.zero()) {
        return new NtMaybe<>();
      }
      NtComplex r = y.recip();
      return new NtMaybe<>(new NtComplex(x.real * r.real - x.imaginary * y.imaginary, x.real * y.imaginary + x.imaginary * y.real));
    }

    public static NtMaybe<NtComplex> CmC(NtComplex x, NtMaybe<NtComplex> y) {
      if (y.has()) {
        return CC(x, y.get());
      }
      return y;
    }

    // Left=maybe<double>
    public static NtMaybe<Double> mDI(NtMaybe<Double> x, int y) {
      if (x.has()) {
        return DI(x.get(), y);
      }
      return x;
    }

    public static NtMaybe<Double> mDL(NtMaybe<Double> x, long y) {
      if (x.has()) {
        return DL(x.get(), y);
      }
      return x;
    }

    public static NtMaybe<Double> mDD(NtMaybe<Double> x, double y) {
      if (x.has()) {
        return DD(x.get(), y);
      }
      return x;
    }

    public static NtMaybe<Double> mDmD(NtMaybe<Double> x, NtMaybe<Double> y) {
      if (x.has()) {
        if (y.has()) {
          return DD(x.get(), y.get());
        }
        return y;
      } else {
        return x;
      }
    }

    public static NtMaybe<NtComplex> mDC(NtMaybe<Double> x, NtComplex y) {
      if (!x.has() || y.zero()) {
        return new NtMaybe<>();
      }
      NtComplex r = y.recip();
      return new NtMaybe<>(new NtComplex(r.real * x.get(), r.imaginary *  x.get()));
    }

    public static NtMaybe<NtComplex> mDmC(NtMaybe<Double> x, NtMaybe<NtComplex> y) {
      if (x.has() && y.has()) {
        return DC(x.get(), y.get());
      }
      return new NtMaybe<>();
    }

    // left=maybe<complex>
    public static NtMaybe<NtComplex> mCI(NtMaybe<NtComplex> x, int y) {
      if (x.has()) {
        return CI(x.get(), y);
      }
      return x;
    }

    public static NtMaybe<NtComplex> mCL(NtMaybe<NtComplex> x, long y) {
      if (x.has()) {
        return CL(x.get(), y);
      }
      return x;
    }

    public static NtMaybe<NtComplex> mCD(NtMaybe<NtComplex> x, double y) {
      if (x.has()) {
        return CD(x.get(), y);
      }
      return x;
    }

    public static NtMaybe<NtComplex> mCmD(NtMaybe<NtComplex> x, NtMaybe<Double> y) {
      if (x.has()) {
        return CmD(x.get(), y);
      }
      return x;
    }

    public static NtMaybe<NtComplex> mCC(NtMaybe<NtComplex> x, NtComplex y) {
      if (x.has()) {
        return CC(x.get(), y);
      }
      return x;
    }

    public static NtMaybe<NtComplex> mCmC(NtMaybe<NtComplex> x, NtMaybe<NtComplex> y) {
      if (x.has()) {
        return CmC(x.get(), y);
      }
      return y;
    }
  }

  public static class Multiply {
    // Left=maybe<complex>
    public static NtComplex CI(NtComplex x, int y) {
      return new NtComplex(x.real * y, x.imaginary * y);
    }
    public static NtComplex CL(NtComplex x, long y) {
      return new NtComplex(x.real * y, x.imaginary * y);
    }
    public static NtComplex CD(NtComplex x, double y) {
      return new NtComplex(x.real * y, x.imaginary * y);
    }
    public static NtMaybe<NtComplex> CmD(NtComplex x, NtMaybe<Double> y) {
      if (y.has()) {
        return new NtMaybe<>(new NtComplex(x.real * y.get(), x.imaginary * y.get()));
      }
      return new NtMaybe<>();
    }
    public static NtComplex CC(NtComplex x, NtComplex y) {
      return new NtComplex(x.real * y.real - x.imaginary * y.imaginary, x.real * y.imaginary + y.real * x.imaginary);
    }
    public static NtMaybe<NtComplex> CmC(NtComplex x, NtMaybe<NtComplex> y) {
      if (y.has()) {
        return new NtMaybe<>(CC(x, y.get()));
      }
      return y;
    }
    // Left=maybe<complex>
    public static NtMaybe<NtComplex> mCI(NtMaybe<NtComplex> x, int y) {
      if (x.has()) {
        return new NtMaybe<>(CI(x.get(), y));
      }
      return new NtMaybe<>();
    }
    public static NtMaybe<NtComplex> mCL(NtMaybe<NtComplex> x, long y) {
      if (x.has()) {
        return new NtMaybe<>(CL(x.get(), y));
      }
      return new NtMaybe<>();
    }
    public static NtMaybe<NtComplex> mCD(NtMaybe<NtComplex> x, double y) {
      if (x.has()) {
        return new NtMaybe<>(CD(x.get(), y));
      }
      return new NtMaybe<>();
    }
    public static NtMaybe<NtComplex> mCmD(NtMaybe<NtComplex> x, NtMaybe<Double> y) {
      if (x.has()) {
        return new NtMaybe<>(CmD(x.get(), y));
      }
      return new NtMaybe<>();
    }
    public static NtMaybe<NtComplex> mCC(NtMaybe<NtComplex> x, NtComplex y) {
      if (x.has()) {
        return new NtMaybe<>(CC(x.get(), y));
      }
      return new NtMaybe<>();
    }
    public static NtMaybe<NtComplex> mCmC(NtMaybe<NtComplex> x, NtMaybe<NtComplex> y) {
      if (x.has() && y.has()) {
        return new NtMaybe<>(CC(x.get(), y.get()));
      }
      return new NtMaybe<>();
    }
    public static NtMaybe<Double> mDI(NtMaybe<Double> x, int y) {
      if (x.has()) {
        return new NtMaybe<>(x.get() * y);
      }
      return x;
    }
    public static NtMaybe<Double> mDL(NtMaybe<Double> x, long y) {
      if (x.has()) {
        return new NtMaybe<>(x.get() * y);
      }
      return x;
    }
    public static NtMaybe<Double> mDD(NtMaybe<Double> x, double y) {
      if (x.has()) {
        return new NtMaybe<>(x.get() * y);
      }
      return x;
    }
    public static NtMaybe<Double> mDmD(NtMaybe<Double> x, NtMaybe<Double> y) {
      if (y.has()) {
        return mDD(x, y.get());
      }
      return y;
    }
  }

  public static class Subtract {
    public static NtMaybe<Double> ImD(int x, NtMaybe<Double> y) {
      if (y.has()) {
        return new NtMaybe<>(x - y.get());
      }
      return y;
    }
    public static NtComplex IC(int x, NtComplex y) {
      return new NtComplex(x - y.real, -y.imaginary);
    }
    public static NtMaybe<NtComplex> ImC(int x, NtMaybe<NtComplex> y) {
      if (y.has()) {
        return new NtMaybe<>(IC(x, y.get()));
      }
      return y;
    }
    public static NtMaybe<Double> LmD(long x, NtMaybe<Double> y) {
      if (y.has()) {
        return new NtMaybe<>(x - y.get());
      }
      return y;
    }
    public static NtComplex LC(long x, NtComplex y) {
      return new NtComplex(x - y.real, - y.imaginary);
    }
    public static NtMaybe<NtComplex> LmC(long x, NtMaybe<NtComplex> y) {
      if (y.has()) {
        return new NtMaybe<>(LC(x, y.get()));
      }
      return y;
    }
    public static NtMaybe<Double> DmD(double x, NtMaybe<Double> y) {
      if (y.has()) {
        return new NtMaybe<>(x - y.get());
      }
      return y;
    }
    public static NtComplex DC(double x, NtComplex y) {
      return new NtComplex(x - y.real, - y.imaginary);
    }
    public static NtMaybe<NtComplex> DmC(double x, NtMaybe<NtComplex> y) {
      if (y.has()) {
        return new NtMaybe<>(DC(x, y.get()));
      }
      return y;
    }
    public static NtMaybe<Double> mDI(NtMaybe<Double> x, int y) {
      if (x.has()) {
        return new NtMaybe<>(x.get() - y);
      }
      return x;
    }
    public static NtMaybe<Double> mDL(NtMaybe<Double> x, long y) {
      if (x.has()) {
        return new NtMaybe<>(x.get() - y);
      }
      return x;
    }
    public static NtMaybe<Double> mDD(NtMaybe<Double> x, double y) {
      if (x.has()) {
        return new NtMaybe<>(x.get() - y);
      }
      return x;
    }
    public static NtMaybe<Double> mDmD(NtMaybe<Double> x, NtMaybe<Double> y) {
      if (y.has()) {
        return mDD(x, y.get());
      }
      return y;
    }
    public static NtMaybe<NtComplex> mDC(NtMaybe<Double> x, NtComplex y) {
      if (x.has()) {
        return new NtMaybe<>(DC(x.get(), y));
      }
      return new NtMaybe<>();
    }
    public static NtMaybe<NtComplex> mDmC(NtMaybe<Double> x, NtMaybe<NtComplex> y) {
      if (y.has()) {
        return mDC(x, y.get());
      }
      return y;
    }
    public static NtComplex CI(NtComplex x, int y) {
      return new NtComplex(x.real - y, x.imaginary);
    }
    public static NtComplex CL(NtComplex x, long y) {
      return new NtComplex(x.real - y, x.imaginary);
    }
    public static NtComplex CD(NtComplex x, double y) {
      return new NtComplex(x.real - y, x.imaginary);
    }
    public static NtMaybe<NtComplex> CmD(NtComplex x, NtMaybe<Double> y) {
      if (y.has()) {
        return new NtMaybe<>(CD(x, y.get()));
      }
      return new NtMaybe<>();
    }
    public static NtComplex CC(NtComplex x, NtComplex y) {
      return new NtComplex(x.real - y.real, x.imaginary - y.imaginary);
    }
    public static NtMaybe<NtComplex> CmC(NtComplex x, NtMaybe<NtComplex> y) {
      if (y.has()) {
        return new NtMaybe<>(CC(x, y.get()));
      }
      return y;
    }
    public static NtMaybe<NtComplex> mCI(NtMaybe<NtComplex> x, int y) {
      if (x.has()) {
        return new NtMaybe<>(CI(x.get(), y));
      }
      return x;
    }
    public static NtMaybe<NtComplex> mCL(NtMaybe<NtComplex> x, long y) {
      if (x.has()) {
        return new NtMaybe<>(CL(x.get(), y));
      }
      return x;
    }
    public static NtMaybe<NtComplex> mCD(NtMaybe<NtComplex> x, double y) {
      if (x.has()) {
        return new NtMaybe<>(CD(x.get(), y));
      }
      return x;
    }
    public static NtMaybe<NtComplex> mCmD(NtMaybe<NtComplex> x, NtMaybe<Double> y) {
      if (x.has()) {
        return new NtMaybe<>(CmD(x.get(), y));
      }
      return x;
    }
    public static NtMaybe<NtComplex> mCC(NtMaybe<NtComplex> x, NtComplex y) {
      if (x.has()) {
        return new NtMaybe<>(CC(x.get(), y));
      }
      return x;
    }
    public static NtMaybe<NtComplex> mCmC(NtMaybe<NtComplex> x, NtMaybe<NtComplex> y) {
      if (x.has()) {
        return new NtMaybe<>(CmC(x.get(), y));
      }
      return x;
    }
  }

  public static class Add {
    public static NtMaybe<Double> mDI(NtMaybe<Double> x, int y) {
      if (x.has()) {
        return new NtMaybe<>(x.get() + y);
      }
      return x;
    }
    public static NtMaybe<Double> mDL(NtMaybe<Double> x, long y) {
      if (x.has()) {
        return new NtMaybe<>(x.get() + y);
      }
      return x;
    }
    public static NtMaybe<Double> mDD(NtMaybe<Double> x, double y) {
      if (x.has()) {
        return new NtMaybe<>(x.get() + y);
      }
      return x;
    }
    public static NtMaybe<Double> mDmD(NtMaybe<Double> x, NtMaybe<Double> y) {
      if (x.has() && y.has()) {
        return new NtMaybe<>(x.get() + y.get());
      }
      return new NtMaybe<>();
    }
    public static NtComplex CI(NtComplex x, int y) {
      return new NtComplex(x.real + y, x.imaginary);
    }
    public static NtComplex CL(NtComplex x, long y) {
      return new NtComplex(x.real + y, x.imaginary);
    }
    public static NtComplex CD(NtComplex x, double y) {
      return new NtComplex(x.real + y, x.imaginary);
    }
    public static NtMaybe<NtComplex> CmD(NtComplex x, NtMaybe<Double> y) {
      if (y.has()) {
        return new NtMaybe<>(new NtComplex(x.real + y.get(), x.imaginary));
      }
      return new NtMaybe<>();
    }
    public static NtComplex CC(NtComplex x, NtComplex y) {
      return new NtComplex(x.real + y.real, x.imaginary + y.imaginary);
    }
    public static NtMaybe<NtComplex> mCI(NtMaybe<NtComplex> x, int y) {
      if (x.has()) {
        return new NtMaybe<>(CI(x.get(), y));
      }
      return x;
    }
    public static NtMaybe<NtComplex> mCL(NtMaybe<NtComplex> x, long y) {
      if (x.has()) {
        return new NtMaybe<>(CL(x.get(), y));
      }
      return x;
    }
    public static NtMaybe<NtComplex> mCD(NtMaybe<NtComplex> x, double y) {
      if (x.has()) {
        return new NtMaybe<>(CD(x.get(), y));
      }
      return x;
    }
    public static NtMaybe<NtComplex> mCmD(NtMaybe<NtComplex> x, NtMaybe<Double> y) {
      if (x.has()) {
        return new NtMaybe<>(CmD(x.get(), y));
      }
      return x;
    }
    public static NtMaybe<NtComplex> mCC(NtMaybe<NtComplex> x, NtComplex y) {
      if (x.has()) {
        return new NtMaybe<>(CC(x.get(), y));
      }
      return x;
    }
    public static NtMaybe<NtComplex> mCmC(NtMaybe<NtComplex> x, NtMaybe<NtComplex> y) {
      if (x.has()) {
        return new NtMaybe<>(mCC(y, x.get()));
      }
      return x;
    }
  }

  public static class Mod {
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
