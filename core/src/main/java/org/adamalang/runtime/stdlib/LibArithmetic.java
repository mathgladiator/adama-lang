/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtComplex;
import org.adamalang.runtime.natives.NtList;
import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.runtime.reactives.*;

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

    public static NtMaybe<Double> ImD(int x, NtMaybe<Double> y) {
      if (y.has()) {
        return ID(x, y.get());
      }
      return y;
    }

    public static NtMaybe<Double> ID(int x, double y) {
      double z = ((double) x) / y;
      if (Double.isNaN(z) || Double.isInfinite(z)) {
        return new NtMaybe<>();
      }
      return new NtMaybe<>(z);
    }

    public static NtMaybe<NtComplex> ImC(int x, NtMaybe<NtComplex> y) {
      if (y.has()) {
        return IC(x, y.get());
      }
      return y;
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

    public static NtMaybe<Double> LmD(long x, NtMaybe<Double> y) {
      if (y.has()) {
        return LD(x, y.get());
      }
      return y;
    }

    public static NtMaybe<Double> LD(long x, double y) {
      double z = ((double) x) / y;
      if (Double.isNaN(z) || Double.isInfinite(z)) {
        return new NtMaybe<>();
      }
      return new NtMaybe<>(z);
    }

    public static NtMaybe<NtComplex> LmC(long x, NtMaybe<NtComplex> y) {
      if (y.has()) {
        return LC(x, y.get());
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

    public static NtMaybe<Double> DmD(double x, NtMaybe<Double> y) {
      if (y.has()) {
        return DD(x, y.get());
      }
      return y;
    }

    public static NtMaybe<Double> DD(double x, double y) {
      double z = x / y;
      if (Double.isNaN(z) || Double.isInfinite(z)) {
        return new NtMaybe<>();
      }
      return new NtMaybe<>(z);
    }

    public static NtMaybe<NtComplex> DmC(double x, NtMaybe<NtComplex> y) {
      if (y.has()) {
        return DC(x, y.get());
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

    // Left=maybe<double>
    public static NtMaybe<Double> mDI(NtMaybe<Double> x, int y) {
      if (x.has()) {
        return DI(x.get(), y);
      }
      return x;
    }

    // left=Double
    public static NtMaybe<Double> DI(double x, int y) {
      double z = x / y;
      if (Double.isNaN(z) || Double.isInfinite(z)) {
        return new NtMaybe<>();
      }
      return new NtMaybe<>(z);
    }

    public static NtMaybe<Double> mDL(NtMaybe<Double> x, long y) {
      if (x.has()) {
        return DL(x.get(), y);
      }
      return x;
    }

    public static NtMaybe<Double> DL(double x, long y) {
      double z = x / y;
      if (Double.isNaN(z) || Double.isInfinite(z)) {
        return new NtMaybe<>();
      }
      return new NtMaybe<>(z);
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
      return new NtMaybe<>(new NtComplex(r.real * x.get(), r.imaginary * x.get()));
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

    // Left=Complex
    public static NtMaybe<NtComplex> CI(NtComplex x, int y) {
      if (y == 0) {
        return new NtMaybe<>();
      }
      return new NtMaybe<>(new NtComplex(x.real / y, x.imaginary / y));
    }

    public static NtMaybe<NtComplex> mCL(NtMaybe<NtComplex> x, long y) {
      if (x.has()) {
        return CL(x.get(), y);
      }
      return x;
    }

    public static NtMaybe<NtComplex> CL(NtComplex x, long y) {
      if (y == 0) {
        return new NtMaybe<>();
      }
      return new NtMaybe<>(new NtComplex(x.real / y, x.imaginary / y));
    }

    public static NtMaybe<NtComplex> mCD(NtMaybe<NtComplex> x, double y) {
      if (x.has()) {
        return CD(x.get(), y);
      }
      return x;
    }

    public static NtMaybe<NtComplex> CD(NtComplex x, double y) {
      if (LibMath.near(y, 0)) {
        return new NtMaybe<>();
      }
      return new NtMaybe<>(new NtComplex(x.real / y, x.imaginary / y));
    }

    public static NtMaybe<NtComplex> mCmD(NtMaybe<NtComplex> x, NtMaybe<Double> y) {
      if (x.has()) {
        return CmD(x.get(), y);
      }
      return x;
    }

    public static NtMaybe<NtComplex> CmD(NtComplex x, NtMaybe<Double> y) {
      if (y.has()) {
        return CD(x, y.get());
      }
      return new NtMaybe<>();
    }

    public static NtMaybe<NtComplex> mCC(NtMaybe<NtComplex> x, NtComplex y) {
      if (x.has()) {
        return CC(x.get(), y);
      }
      return x;
    }

    public static NtMaybe<NtComplex> CC(NtComplex x, NtComplex y) {
      if (y.zero()) {
        return new NtMaybe<>();
      }
      NtComplex r = y.recip();
      return new NtMaybe<>(new NtComplex(x.real * r.real - x.imaginary * y.imaginary, x.real * y.imaginary + x.imaginary * y.real));
    }

    public static NtMaybe<NtComplex> mCmC(NtMaybe<NtComplex> x, NtMaybe<NtComplex> y) {
      if (x.has()) {
        return CmC(x.get(), y);
      }
      return y;
    }

    public static NtMaybe<NtComplex> CmC(NtComplex x, NtMaybe<NtComplex> y) {
      if (y.has()) {
        return CC(x, y.get());
      }
      return y;
    }
  }

  public static class Multiply {
    public static NtMaybe<NtComplex> CmC(NtComplex x, NtMaybe<NtComplex> y) {
      if (y.has()) {
        return new NtMaybe<>(CC(x, y.get()));
      }
      return y;
    }

    public static NtComplex CC(NtComplex x, NtComplex y) {
      return new NtComplex(x.real * y.real - x.imaginary * y.imaginary, x.real * y.imaginary + y.real * x.imaginary);
    }

    // Left=maybe<complex>
    public static NtMaybe<NtComplex> mCI(NtMaybe<NtComplex> x, int y) {
      if (x.has()) {
        return new NtMaybe<>(CI(x.get(), y));
      }
      return new NtMaybe<>();
    }

    // Left=maybe<complex>
    public static NtComplex CI(NtComplex x, int y) {
      return new NtComplex(x.real * y, x.imaginary * y);
    }

    public static NtMaybe<NtComplex> mCL(NtMaybe<NtComplex> x, long y) {
      if (x.has()) {
        return new NtMaybe<>(CL(x.get(), y));
      }
      return new NtMaybe<>();
    }

    public static NtComplex CL(NtComplex x, long y) {
      return new NtComplex(x.real * y, x.imaginary * y);
    }

    public static NtMaybe<NtComplex> mCD(NtMaybe<NtComplex> x, double y) {
      if (x.has()) {
        return new NtMaybe<>(CD(x.get(), y));
      }
      return new NtMaybe<>();
    }

    public static NtComplex CD(NtComplex x, double y) {
      return new NtComplex(x.real * y, x.imaginary * y);
    }

    public static NtMaybe<NtComplex> mCmD(NtMaybe<NtComplex> x, NtMaybe<Double> y) {
      if (x.has()) {
        return new NtMaybe<>(CmD(x.get(), y));
      }
      return new NtMaybe<>();
    }

    public static NtMaybe<NtComplex> CmD(NtComplex x, NtMaybe<Double> y) {
      if (y.has()) {
        return new NtMaybe<>(new NtComplex(x.real * y.get(), x.imaginary * y.get()));
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

    public static NtMaybe<Double> mDmD(NtMaybe<Double> x, NtMaybe<Double> y) {
      if (y.has()) {
        return mDD(x, y.get());
      }
      return y;
    }

    public static NtMaybe<Double> mDD(NtMaybe<Double> x, double y) {
      if (x.has()) {
        return new NtMaybe<>(x.get() * y);
      }
      return x;
    }
  }

  public static class Subtract {
    public static NtMaybe<Double> ImD(int x, NtMaybe<Double> y) {
      if (y.has()) {
        return new NtMaybe<>(x - y.get());
      }
      return y;
    }

    public static NtMaybe<NtComplex> ImC(int x, NtMaybe<NtComplex> y) {
      if (y.has()) {
        return new NtMaybe<>(IC(x, y.get()));
      }
      return y;
    }

    public static NtComplex IC(int x, NtComplex y) {
      return new NtComplex(x - y.real, -y.imaginary);
    }

    public static NtMaybe<Double> LmD(long x, NtMaybe<Double> y) {
      if (y.has()) {
        return new NtMaybe<>(x - y.get());
      }
      return y;
    }

    public static NtMaybe<NtComplex> LmC(long x, NtMaybe<NtComplex> y) {
      if (y.has()) {
        return new NtMaybe<>(LC(x, y.get()));
      }
      return y;
    }

    public static NtComplex LC(long x, NtComplex y) {
      return new NtComplex(x - y.real, -y.imaginary);
    }

    public static NtMaybe<Double> DmD(double x, NtMaybe<Double> y) {
      if (y.has()) {
        return new NtMaybe<>(x - y.get());
      }
      return y;
    }

    public static NtMaybe<NtComplex> DmC(double x, NtMaybe<NtComplex> y) {
      if (y.has()) {
        return new NtMaybe<>(DC(x, y.get()));
      }
      return y;
    }

    public static NtComplex DC(double x, NtComplex y) {
      return new NtComplex(x - y.real, -y.imaginary);
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

    public static NtMaybe<Double> mDmD(NtMaybe<Double> x, NtMaybe<Double> y) {
      if (y.has()) {
        return mDD(x, y.get());
      }
      return y;
    }

    public static NtMaybe<Double> mDD(NtMaybe<Double> x, double y) {
      if (x.has()) {
        return new NtMaybe<>(x.get() - y);
      }
      return x;
    }

    public static NtMaybe<NtComplex> mDmC(NtMaybe<Double> x, NtMaybe<NtComplex> y) {
      if (y.has()) {
        return mDC(x, y.get());
      }
      return y;
    }

    public static NtMaybe<NtComplex> mDC(NtMaybe<Double> x, NtComplex y) {
      if (x.has()) {
        return new NtMaybe<>(DC(x.get(), y));
      }
      return new NtMaybe<>();
    }

    public static NtMaybe<NtComplex> mCI(NtMaybe<NtComplex> x, int y) {
      if (x.has()) {
        return new NtMaybe<>(CI(x.get(), y));
      }
      return x;
    }

    public static NtComplex CI(NtComplex x, int y) {
      return new NtComplex(x.real - y, x.imaginary);
    }

    public static NtMaybe<NtComplex> mCL(NtMaybe<NtComplex> x, long y) {
      if (x.has()) {
        return new NtMaybe<>(CL(x.get(), y));
      }
      return x;
    }

    public static NtComplex CL(NtComplex x, long y) {
      return new NtComplex(x.real - y, x.imaginary);
    }

    public static NtMaybe<NtComplex> mCD(NtMaybe<NtComplex> x, double y) {
      if (x.has()) {
        return new NtMaybe<>(CD(x.get(), y));
      }
      return x;
    }

    public static NtComplex CD(NtComplex x, double y) {
      return new NtComplex(x.real - y, x.imaginary);
    }

    public static NtMaybe<NtComplex> mCmD(NtMaybe<NtComplex> x, NtMaybe<Double> y) {
      if (x.has()) {
        return new NtMaybe<>(CmD(x.get(), y));
      }
      return x;
    }

    public static NtMaybe<NtComplex> CmD(NtComplex x, NtMaybe<Double> y) {
      if (y.has()) {
        return new NtMaybe<>(CD(x, y.get()));
      }
      return new NtMaybe<>();
    }

    public static NtMaybe<NtComplex> mCC(NtMaybe<NtComplex> x, NtComplex y) {
      if (x.has()) {
        return new NtMaybe<>(CC(x.get(), y));
      }
      return x;
    }

    public static NtComplex CC(NtComplex x, NtComplex y) {
      return new NtComplex(x.real - y.real, x.imaginary - y.imaginary);
    }

    public static NtMaybe<NtComplex> mCmC(NtMaybe<NtComplex> x, NtMaybe<NtComplex> y) {
      if (x.has()) {
        return new NtMaybe<>(CmC(x.get(), y));
      }
      return x;
    }

    public static NtMaybe<NtComplex> CmC(NtComplex x, NtMaybe<NtComplex> y) {
      if (y.has()) {
        return new NtMaybe<>(CC(x, y.get()));
      }
      return y;
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

    public static NtMaybe<NtComplex> mCI(NtMaybe<NtComplex> x, int y) {
      if (x.has()) {
        return new NtMaybe<>(CI(x.get(), y));
      }
      return x;
    }

    public static NtComplex CI(NtComplex x, int y) {
      return new NtComplex(x.real + y, x.imaginary);
    }

    public static NtMaybe<NtComplex> mCL(NtMaybe<NtComplex> x, long y) {
      if (x.has()) {
        return new NtMaybe<>(CL(x.get(), y));
      }
      return x;
    }

    public static NtComplex CL(NtComplex x, long y) {
      return new NtComplex(x.real + y, x.imaginary);
    }

    public static NtMaybe<NtComplex> mCD(NtMaybe<NtComplex> x, double y) {
      if (x.has()) {
        return new NtMaybe<>(CD(x.get(), y));
      }
      return x;
    }

    public static NtComplex CD(NtComplex x, double y) {
      return new NtComplex(x.real + y, x.imaginary);
    }

    public static NtMaybe<NtComplex> mCmD(NtMaybe<NtComplex> x, NtMaybe<Double> y) {
      if (x.has()) {
        return new NtMaybe<>(CmD(x.get(), y));
      }
      return x;
    }

    public static NtMaybe<NtComplex> CmD(NtComplex x, NtMaybe<Double> y) {
      if (y.has()) {
        return new NtMaybe<>(new NtComplex(x.real + y.get(), x.imaginary));
      }
      return new NtMaybe<>();
    }

    public static NtMaybe<NtComplex> mCmC(NtMaybe<NtComplex> x, NtMaybe<NtComplex> y) {
      if (x.has()) {
        return new NtMaybe<>(mCC(y, x.get()));
      }
      return x;
    }

    public static NtMaybe<NtComplex> mCC(NtMaybe<NtComplex> x, NtComplex y) {
      if (x.has()) {
        return new NtMaybe<>(CC(x.get(), y));
      }
      return x;
    }

    public static NtComplex CC(NtComplex x, NtComplex y) {
      return new NtComplex(x.real + y.real, x.imaginary + y.imaginary);
    }
  }

  public static class Mod {
    public static NtMaybe<Integer> II(int x, int y) {
      if (y == 0) {
        return new NtMaybe<>();
      }
      return new NtMaybe<>(x % y);
    }

    public static NtMaybe<Long> LI(long x, int y) {
      if (y == 0) {
        return new NtMaybe<>();
      }
      return new NtMaybe<>(x % y);
    }

    public static NtMaybe<Long> LL(long x, long y) {
      if (y == 0) {
        return new NtMaybe<>();
      }
      return new NtMaybe<>(x % y);
    }
  }

  public static class ListMath {
    public static int addToII(NtList<RxInt32> x, int y) {
      for (RxInt32 item : x) {
        item.opAddTo(y);
      }
      return x.size();
    }

    public static int addToLI(NtList<RxInt64> x, int y) {
      for (RxInt64 item : x) {
        item.opAddTo(y);
      }
      return x.size();
    }

    public static int addToLL(NtList<RxInt64> x, long y) {
      for (RxInt64 item : x) {
        item.opAddTo(y);
      }
      return x.size();
    }

    public static int addToDI(NtList<RxDouble> x, int y) {
      for (RxDouble item : x) {
        item.opAddTo(y);
      }
      return x.size();
    }

    public static int addToDL(NtList<RxDouble> x, long y) {
      for (RxDouble item : x) {
        item.opAddTo(y);
      }
      return x.size();
    }

    public static int addToDD(NtList<RxDouble> x, double y) {
      for (RxDouble item : x) {
        item.opAddTo(y);
      }
      return x.size();
    }

    public static int addToCI(NtList<RxComplex> x, int y) {
      for (RxComplex item : x) {
        item.opAddTo(y);
      }
      return x.size();
    }

    public static int addToCL(NtList<RxComplex> x, long y) {
      for (RxComplex item : x) {
        item.opAddTo(y);
      }
      return x.size();
    }

    public static int addToCD(NtList<RxComplex> x, double y) {
      for (RxComplex item : x) {
        item.opAddTo(y);
      }
      return x.size();
    }

    public static int addToCC(NtList<RxComplex> x, NtComplex y) {
      for (RxComplex item : x) {
        item.opAddTo(y);
      }
      return x.size();
    }

    public static NtComplex subFromCC(NtList<RxComplex> x, NtComplex y) {
      double real = 0;
      double imaginary = 0;
      for (RxComplex item : x) {
        item.opSubFrom(y);
        real += item.get().real;
        imaginary += item.get().imaginary;
      }
      return new NtComplex(real, imaginary);
    }

    public static String addToSO(NtList<RxString> x, Object y) {
      StringBuilder sb = new StringBuilder();
      for (RxString item : x) {
        sb.append(item.opAddTo(y.toString()));
      }
      return sb.toString();
    }

    public static int multByII(NtList<RxInt32> x, int y) {
      for (RxInt32 item : x) {
        item.opMultBy(y);
      }
      return x.size();
    }

    public static int multByLI(NtList<RxInt64> x, int y) {
      for (RxInt64 item : x) {
        item.opMultBy(y);
      }
      return x.size();
    }

    public static int multByLL(NtList<RxInt64> x, long y) {
      for (RxInt64 item : x) {
        item.opMultBy(y);
      }
      return x.size();
    }

    public static int multByDI(NtList<RxDouble> x, int y) {
      for (RxDouble item : x) {
        item.opMultBy(y);
      }
      return x.size();
    }

    public static int multByDL(NtList<RxDouble> x, long y) {
      for (RxDouble item : x) {
        item.opMultBy(y);
      }
      return x.size();
    }

    public static int multByDD(NtList<RxDouble> x, double y) {
      for (RxDouble item : x) {
        item.opMultBy(y);
      }
      return x.size();
    }

    public static int multByCI(NtList<RxComplex> x, int y) {
      for (RxComplex item : x) {
        item.opMultBy(y);
      }
      return x.size();
    }

    public static int multByCL(NtList<RxComplex> x, long y) {
      for (RxComplex item : x) {
        item.opMultBy(y);
      }
      return x.size();
    }

    public static int multByCD(NtList<RxComplex> x, double y) {
      for (RxComplex item : x) {
        item.opMultBy(y);
      }
      return x.size();
    }

    public static int multByCC(NtList<RxComplex> x, NtComplex y) {
      for (RxComplex item : x) {
        item.opMultBy(y);
      }
      return x.size();
    }
  }

  public static class And {
    public static boolean mBB(NtMaybe<Boolean> x, boolean y) {
      if (x.has()) {
        return x.get() && y;
      }
      return false;
    }

    public static boolean BmB(boolean x, NtMaybe<Boolean> y) {
      if (y.has()) {
        return x && y.get();
      }
      return false;
    }

    public static boolean mBmB(NtMaybe<Boolean> x, NtMaybe<Boolean> y) {
      if (x.has() && y.has()) {
        return x.get() && y.get();
      }
      return false;
    }
  }

  public static class Or {
    public static boolean mBB(NtMaybe<Boolean> x, boolean y) {
      if (x.has()) {
        return x.get() || y;
      }
      return y;
    }

    public static boolean BmB(boolean x, NtMaybe<Boolean> y) {
      if (y.has()) {
        return x || y.get();
      }
      return x;
    }

    public static boolean mBmB(NtMaybe<Boolean> x, NtMaybe<Boolean> y) {
      if (x.has() && y.has()) {
        return x.get() || y.get();
      } else if (x.has()) { // y is false
        return x.get();
      } else if (y.has()) { // x is false
        return y.get();
      }
      return false;
    }
  }

  public static class Xor {
    public static boolean mBB(NtMaybe<Boolean> x, boolean y) {
      if (x.has()) {
        return LibMath.xor(x.get(), y);
      }
      return LibMath.xor(false, y);
    }

    public static boolean BmB(boolean x, NtMaybe<Boolean> y) {
      if (y.has()) {
        return LibMath.xor(x, y.get());
      }
      return LibMath.xor(x, false);
    }

    public static boolean mBmB(NtMaybe<Boolean> x, NtMaybe<Boolean> y) {
      if (x.has() && y.has()) {
        LibMath.xor(x.get(), y.get());
      } else if (x.has()) { // y is false
        LibMath.xor(x.get(), false);
      } else if (y.has()) { // x is false
        LibMath.xor(false, y.get());
      }
      return false;
    }
  }
}
