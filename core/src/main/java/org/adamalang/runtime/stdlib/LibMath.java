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
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtComplex;
import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.translator.reflect.Extension;
import org.adamalang.translator.reflect.HiddenType;
import org.adamalang.translator.reflect.Skip;
import org.adamalang.translator.reflect.UseName;

import java.util.function.BiFunction;

/** a very simple math library which extends some of the already exposed Math calls */
public class LibMath {
  public static double SQRT2 = 1.4142135623730950488016887242097;
  public static double E = Math.E;
  public static double PI = Math.PI;

  @Extension
  public static @HiddenType(clazz = NtComplex.class) NtMaybe<NtComplex> sqrt(@HiddenType(clazz = Double.class) final NtMaybe<Double> mx) {
    if (mx.has()) {
      return new NtMaybe<>(sqrt(mx.get()));
    }
    return new NtMaybe<>();
  }

  @Extension
  public static NtComplex sqrt(final double x) {
    if (x < 0) {
      return new NtComplex(0.0, Math.sqrt(-x));
    }
    return new NtComplex(Math.sqrt(x), 0.0);
  }

  /** isInfinite */
  @Extension
  public static boolean isInfinite(double x) {
    return Double.isInfinite(x);
  }

  @Extension
  public static boolean isInfinite(@HiddenType(clazz = Double.class) NtMaybe<Double> mx) {
    if (mx.has()) {
      return Double.isInfinite(mx.get());
    }
    return false;
  }

  @Extension
  public static boolean isNaN(double x) {
    return Double.isNaN(x);
  }

  @Extension
  public static boolean isNaN(@HiddenType(clazz = Double.class) NtMaybe<Double> mx) {
    if (mx.has()) {
      return Double.isNaN(mx.get());
    }
    return true;
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> valid(double x) {
    if (Double.isFinite(x) && !Double.isNaN(x)) {
      return new NtMaybe<>(x);
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> valid(@HiddenType(clazz = Double.class) NtMaybe<Double> mx) {
    if (mx.has()) {
      if (Double.isFinite(mx.get()) && !Double.isNaN(mx.get())) {
        return mx;
      }
      return new NtMaybe<>();
    }
    return mx;
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> min(@HiddenType(clazz = Double.class) NtMaybe<Double> mx, double y) {
    if (mx.has()) {
      return new NtMaybe<>(min(mx.get(), y));
    }
    return mx;
  }

  /** min */
  @Extension
  public static double min(final double x, final double y) {
    return Math.min(x, y);
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> min(double x, @HiddenType(clazz = Double.class) NtMaybe<Double> my) {
    if (my.has()) {
      return new NtMaybe<>(min(x, my.get()));
    }
    return my;
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> min(@HiddenType(clazz = Double.class) NtMaybe<Double> mx, @HiddenType(clazz = Double.class) NtMaybe<Double> my) {
    if (mx.has()) {
      if (my.has()) {
        return new NtMaybe<>(min(mx.get(), my.get()));
      }
      return my;
    }
    return mx;
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> max(@HiddenType(clazz = Double.class) NtMaybe<Double> mx, double y) {
    if (mx.has()) {
      return new NtMaybe<>(max(mx.get(), y));
    }
    return mx;
  }

  /** max */
  @Extension
  public static double max(final double x, final double y) {
    return Math.max(x, y);
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> max(double x, @HiddenType(clazz = Double.class) NtMaybe<Double> my) {
    if (my.has()) {
      return new NtMaybe<>(max(x, my.get()));
    }
    return my;
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> max(@HiddenType(clazz = Double.class) NtMaybe<Double> mx, @HiddenType(clazz = Double.class) NtMaybe<Double> my) {
    if (mx.has()) {
      if (my.has()) {
        return new NtMaybe<>(max(mx.get(), my.get()));
      }
      return my;
    }
    return mx;
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> atan2(@HiddenType(clazz = Double.class) NtMaybe<Double> mx, double y) {
    if (mx.has()) {
      return new NtMaybe<>(atan2(mx.get(), y));
    }
    return mx;
  }

  /** atan2 */
  @Extension
  public static double atan2(final double x, final double y) {
    return Math.atan2(x, y);
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> atan2(double x, @HiddenType(clazz = Double.class) NtMaybe<Double> my) {
    if (my.has()) {
      return new NtMaybe<>(atan2(x, my.get()));
    }
    return my;
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> atan2(@HiddenType(clazz = Double.class) NtMaybe<Double> mx, @HiddenType(clazz = Double.class) NtMaybe<Double> my) {
    if (mx.has()) {
      if (my.has()) {
        return new NtMaybe<>(atan2(mx.get(), my.get()));
      }
      return my;
    }
    return mx;
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> hypot(@HiddenType(clazz = Double.class) NtMaybe<Double> mx, double y) {
    if (mx.has()) {
      return new NtMaybe<>(hypot(mx.get(), y));
    }
    return mx;
  }

  /** hypot */
  @Extension
  public static double hypot(final double x, final double y) {
    return Math.hypot(x, y);
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> hypot(double x, @HiddenType(clazz = Double.class) NtMaybe<Double> my) {
    if (my.has()) {
      return new NtMaybe<>(hypot(x, my.get()));
    }
    return my;
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> hypot(@HiddenType(clazz = Double.class) NtMaybe<Double> mx, @HiddenType(clazz = Double.class) NtMaybe<Double> my) {
    if (mx.has()) {
      if (my.has()) {
        return new NtMaybe<>(hypot(mx.get(), my.get()));
      }
      return my;
    }
    return mx;
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> pow(@HiddenType(clazz = Double.class) NtMaybe<Double> mx, double y) {
    if (mx.has()) {
      return new NtMaybe<>(pow(mx.get(), y));
    }
    return mx;
  }

  /** pow */
  @Extension
  public static double pow(final double x, final double y) {
    return Math.pow(x, y);
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> pow(double x, @HiddenType(clazz = Double.class) NtMaybe<Double> my) {
    if (my.has()) {
      return new NtMaybe<>(pow(x, my.get()));
    }
    return my;
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> pow(@HiddenType(clazz = Double.class) NtMaybe<Double> mx, @HiddenType(clazz = Double.class) NtMaybe<Double> my) {
    if (mx.has()) {
      if (my.has()) {
        return new NtMaybe<>(pow(mx.get(), my.get()));
      }
      return my;
    }
    return mx;
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> IEEEremainder(@HiddenType(clazz = Double.class) NtMaybe<Double> mx, double y) {
    if (mx.has()) {
      return new NtMaybe<>(IEEEremainder(mx.get(), y));
    }
    return mx;
  }

  @Extension
  public static int sign(final double x) {
    if (x > 0) {
      return 1;
    } else if (x < 0) {
      return -1;
    }
    return 0;
  }

  @Extension
  public static int sign(final long x) {
    if (x > 0) {
      return 1;
    } else if (x < 0) {
      return -1;
    }
    return 0;
  }

  @Extension
  public static int sign(final int x) {
    if (x > 0) {
      return 1;
    } else if (x < 0) {
      return -1;
    }
    return 0;
  }

  @Extension
  @UseName(name = "sign")
  public static @HiddenType(clazz=Integer.class) NtMaybe<Integer> signD(final @HiddenType(clazz=Double.class) NtMaybe<Double> x) {
    if (x.has()) {
      if (x.get() > 0) {
        return new NtMaybe<>(1);
      } else if (x.get() < 0) {
        return new NtMaybe<>(-1);
      }
      return new NtMaybe<>(0);
    }
    return new NtMaybe<>();
  }

  @Extension
  @UseName(name = "sign")
  public static @HiddenType(clazz=Integer.class) NtMaybe<Integer> signL(final @HiddenType(clazz=Long.class) NtMaybe<Long> x) {
    if (x.has()) {
      if (x.get() > 0) {
        return new NtMaybe<>(1);
      } else if (x.get() < 0) {
        return new NtMaybe<>(-1);
      }
      return new NtMaybe<>(0);
    }
    return new NtMaybe<>();
  }

  @Extension
  @UseName(name = "sign")
  public static @HiddenType(clazz=Integer.class) NtMaybe<Integer> signI(final @HiddenType(clazz=Integer.class) NtMaybe<Integer> x) {
    if (x.has()) {
      if (x.get() > 0) {
        return new NtMaybe<>(1);
      } else if (x.get() < 0) {
        return new NtMaybe<>(-1);
      }
      return new NtMaybe<>(0);
    }
    return new NtMaybe<>();
  }

  public static int gcd(int x, int y) {
    if (x < 0 || y < 0) {
      return gcd(Math.abs(x), Math.abs(y));
    }
    if (y == 0) {
      return x;
    } else {
      return gcd(y, x % y);
    }
  }

  /** IEEEremainder */
  @Extension
  public static double IEEEremainder(final double x, final double y) {
    return Math.IEEEremainder(x, y);
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> IEEEremainder(double x, @HiddenType(clazz = Double.class) NtMaybe<Double> my) {
    if (my.has()) {
      return new NtMaybe<>(IEEEremainder(x, my.get()));
    }
    return my;
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> IEEEremainder(@HiddenType(clazz = Double.class) NtMaybe<Double> mx, @HiddenType(clazz = Double.class) NtMaybe<Double> my) {
    if (mx.has()) {
      if (my.has()) {
        return new NtMaybe<>(IEEEremainder(mx.get(), my.get()));
      }
      return my;
    }
    return mx;
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> copySign(@HiddenType(clazz = Double.class) NtMaybe<Double> mx, double y) {
    if (mx.has()) {
      return new NtMaybe<>(copySign(mx.get(), y));
    }
    return mx;
  }

  /** copySign */
  @Extension
  public static double copySign(final double x, final double y) {
    return Math.copySign(x, y);
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> copySign(double x, @HiddenType(clazz = Double.class) NtMaybe<Double> my) {
    if (my.has()) {
      return new NtMaybe<>(copySign(x, my.get()));
    }
    return my;
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> copySign(@HiddenType(clazz = Double.class) NtMaybe<Double> mx, @HiddenType(clazz = Double.class) NtMaybe<Double> my) {
    if (mx.has()) {
      if (my.has()) {
        return new NtMaybe<>(copySign(mx.get(), my.get()));
      }
      return my;
    }
    return mx;
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> sin(@HiddenType(clazz = Double.class) NtMaybe<Double> mx) {
    if (mx.has()) {
      return new NtMaybe<>(sin(mx.get()));
    }
    return mx;
  }

  /** sin */
  @Extension
  public static double sin(final double x) {
    return Math.sin(x);
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> cos(@HiddenType(clazz = Double.class) NtMaybe<Double> mx) {
    if (mx.has()) {
      return new NtMaybe<>(cos(mx.get()));
    }
    return mx;
  }

  /** cos */
  @Extension
  public static double cos(final double x) {
    return Math.cos(x);
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> tan(@HiddenType(clazz = Double.class) NtMaybe<Double> mx) {
    if (mx.has()) {
      return new NtMaybe<>(tan(mx.get()));
    }
    return mx;
  }

  /** tan */
  @Extension
  public static double tan(final double x) {
    return Math.tan(x);
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> asin(@HiddenType(clazz = Double.class) NtMaybe<Double> mx) {
    if (mx.has()) {
      return new NtMaybe<>(asin(mx.get()));
    }
    return mx;
  }

  /** asin */
  @Extension
  public static double asin(final double x) {
    return Math.asin(x);
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> acos(@HiddenType(clazz = Double.class) NtMaybe<Double> mx) {
    if (mx.has()) {
      return new NtMaybe<>(acos(mx.get()));
    }
    return mx;
  }

  /** acos */
  @Extension
  public static double acos(final double x) {
    return Math.acos(x);
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> atan(@HiddenType(clazz = Double.class) NtMaybe<Double> mx) {
    if (mx.has()) {
      return new NtMaybe<>(atan(mx.get()));
    }
    return mx;
  }

  /** atan */
  @Extension
  public static double atan(final double x) {
    return Math.atan(x);
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> sinh(@HiddenType(clazz = Double.class) NtMaybe<Double> mx) {
    if (mx.has()) {
      return new NtMaybe<>(sinh(mx.get()));
    }
    return mx;
  }

  /** sinh */
  @Extension
  public static double sinh(final double x) {
    return Math.sinh(x);
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> cosh(@HiddenType(clazz = Double.class) NtMaybe<Double> mx) {
    if (mx.has()) {
      return new NtMaybe<>(cosh(mx.get()));
    }
    return mx;
  }

  /** cosh */
  @Extension
  public static double cosh(final double x) {
    return Math.cosh(x);
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> tanh(@HiddenType(clazz = Double.class) NtMaybe<Double> mx) {
    if (mx.has()) {
      return new NtMaybe<>(tanh(mx.get()));
    }
    return mx;
  }

  /** tanh */
  @Extension
  public static double tanh(final double x) {
    return Math.tanh(x);
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> exp(@HiddenType(clazz = Double.class) NtMaybe<Double> mx) {
    if (mx.has()) {
      return new NtMaybe<>(exp(mx.get()));
    }
    return mx;
  }

  /** exp */
  @Extension
  public static double exp(final double x) {
    return Math.exp(x);
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> log(@HiddenType(clazz = Double.class) NtMaybe<Double> mx) {
    if (mx.has()) {
      return new NtMaybe<>(log(mx.get()));
    }
    return mx;
  }

  /** log */
  @Extension
  public static double log(final double x) {
    return Math.log(x);
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> log10(@HiddenType(clazz = Double.class) NtMaybe<Double> mx) {
    if (mx.has()) {
      return new NtMaybe<>(log10(mx.get()));
    }
    return mx;
  }

  /** log10 */
  @Extension
  public static double log10(final double x) {
    return Math.log10(x);
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> cbrt(@HiddenType(clazz = Double.class) NtMaybe<Double> mx) {
    if (mx.has()) {
      return new NtMaybe<>(cbrt(mx.get()));
    }
    return mx;
  }

  /** cbrt */
  @Extension
  public static double cbrt(final double x) {
    return Math.cbrt(x);
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> expm1(@HiddenType(clazz = Double.class) NtMaybe<Double> mx) {
    if (mx.has()) {
      return new NtMaybe<>(expm1(mx.get()));
    }
    return mx;
  }

  /** expm1 */
  @Extension
  public static double expm1(final double x) {
    return Math.expm1(x);
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> log1p(@HiddenType(clazz = Double.class) NtMaybe<Double> mx) {
    if (mx.has()) {
      return new NtMaybe<>(log1p(mx.get()));
    }
    return mx;
  }

  /** log1p */
  @Extension
  public static double log1p(final double x) {
    return Math.log1p(x);
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> signum(@HiddenType(clazz = Double.class) NtMaybe<Double> mx) {
    if (mx.has()) {
      return new NtMaybe<>(signum(mx.get()));
    }
    return mx;
  }

  /** signum */
  @Extension
  public static double signum(final double x) {
    return Math.signum(x);
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> ulp(@HiddenType(clazz = Double.class) NtMaybe<Double> mx) {
    if (mx.has()) {
      return new NtMaybe<>(ulp(mx.get()));
    }
    return mx;
  }

  /** ulp */
  @Extension
  public static double ulp(final double x) {
    return Math.ulp(x);
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> getExponent(@HiddenType(clazz = Double.class) NtMaybe<Double> mx) {
    if (mx.has()) {
      return new NtMaybe<>(getExponent(mx.get()));
    }
    return mx;
  }

  /** getExponent */
  @Extension
  public static double getExponent(final double x) {
    return Math.getExponent(x);
  }

  @UseName(name = "min")
  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> min_i(@HiddenType(clazz = Integer.class) NtMaybe<Integer> mx, int y) {
    if (mx.has()) {
      return new NtMaybe<>(min(mx.get(), y));
    }
    return mx;
  }

  /** min */
  @Extension
  public static int min(final int x, final int y) {
    return Math.min(x, y);
  }

  @UseName(name = "min")
  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> min_i(int x, @HiddenType(clazz = Integer.class) NtMaybe<Integer> my) {
    if (my.has()) {
      return new NtMaybe<>(min(x, my.get()));
    }
    return my;
  }

  @UseName(name = "min")
  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> min_i(@HiddenType(clazz = Integer.class) NtMaybe<Integer> mx, @HiddenType(clazz = Integer.class) NtMaybe<Integer> my) {
    if (mx.has()) {
      if (my.has()) {
        return new NtMaybe<>(min(mx.get(), my.get()));
      }
      return my;
    }
    return mx;
  }

  @UseName(name = "max")
  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> max_i(@HiddenType(clazz = Integer.class) NtMaybe<Integer> mx, int y) {
    if (mx.has()) {
      return new NtMaybe<>(max(mx.get(), y));
    }
    return mx;
  }

  /** max */
  @Extension
  public static int max(final int x, final int y) {
    return Math.max(x, y);
  }

  @UseName(name = "max")
  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> max_i(int x, @HiddenType(clazz = Integer.class) NtMaybe<Integer> my) {
    if (my.has()) {
      return new NtMaybe<>(max(x, my.get()));
    }
    return my;
  }

  @UseName(name = "max")
  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> max_i(@HiddenType(clazz = Integer.class) NtMaybe<Integer> mx, @HiddenType(clazz = Integer.class) NtMaybe<Integer> my) {
    if (mx.has()) {
      if (my.has()) {
        return new NtMaybe<>(max(mx.get(), my.get()));
      }
      return my;
    }
    return mx;
  }

  @UseName(name = "floorDiv")
  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> floorDiv_i(@HiddenType(clazz = Integer.class) NtMaybe<Integer> mx, int y) {
    if (mx.has()) {
      return new NtMaybe<>(floorDiv(mx.get(), y));
    }
    return mx;
  }

  /** floorDiv */
  @Extension
  public static int floorDiv(final int x, final int y) {
    return Math.floorDiv(x, y);
  }

  @UseName(name = "floorDiv")
  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> floorDiv_i(int x, @HiddenType(clazz = Integer.class) NtMaybe<Integer> my) {
    if (my.has()) {
      return new NtMaybe<>(floorDiv(x, my.get()));
    }
    return my;
  }

  @UseName(name = "floorDiv")
  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> floorDiv_i(@HiddenType(clazz = Integer.class) NtMaybe<Integer> mx, @HiddenType(clazz = Integer.class) NtMaybe<Integer> my) {
    if (mx.has()) {
      if (my.has()) {
        return new NtMaybe<>(floorDiv(mx.get(), my.get()));
      }
      return my;
    }
    return mx;
  }

  @UseName(name="intOf")
  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> intOf(double x) {
    if (Integer.MIN_VALUE < x && x < Integer.MAX_VALUE) {
      return new NtMaybe<>((int) x);
    }
    return new NtMaybe<>();
  }


  @UseName(name="clampIntOf")
  @Extension
  public static int clampIntOf(double x) {
    if (x < Integer.MIN_VALUE) {
      return Integer.MIN_VALUE;
    }
    if (x > Integer.MAX_VALUE) {
      return Integer.MAX_VALUE;
    }
    return (int) x;
  }

  @UseName(name="intOf")
  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> intOf(@HiddenType(clazz = Double.class) NtMaybe<Double> x) {
    if (x.has()) {
      return intOf(x.get());
    }
    return new NtMaybe<>();
  }

  @UseName(name="longOf")
  @Extension
  public static @HiddenType(clazz = Long.class) NtMaybe<Long> longOf(double x) {
    if (Long.MIN_VALUE < x && x < Long.MAX_VALUE) {
      return new NtMaybe<>((long) x);
    }
    return new NtMaybe<>();
  }

  @UseName(name="longOf")
  @Extension
  public static @HiddenType(clazz = Long.class) NtMaybe<Long> longOf(@HiddenType(clazz = Double.class) NtMaybe<Double> x) {
    if (x.has()) {
      return longOf(x.get());
    }
    return new NtMaybe<>();
  }

  @UseName(name = "floorMod")
  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> floorMod_i(@HiddenType(clazz = Integer.class) NtMaybe<Integer> mx, int y) {
    if (mx.has()) {
      return new NtMaybe<>(floorMod(mx.get(), y));
    }
    return mx;
  }

  /** floorMod */
  @Extension
  public static int floorMod(final int x, final int y) {
    return Math.floorMod(x, y);
  }

  @UseName(name = "floorMod")
  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> floorMod_i(int x, @HiddenType(clazz = Integer.class) NtMaybe<Integer> my) {
    if (my.has()) {
      return new NtMaybe<>(floorMod(x, my.get()));
    }
    return my;
  }

  @UseName(name = "floorMod")
  @Extension
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> floorMod_i(@HiddenType(clazz = Integer.class) NtMaybe<Integer> mx, @HiddenType(clazz = Integer.class) NtMaybe<Integer> my) {
    if (mx.has()) {
      if (my.has()) {
        return new NtMaybe<>(floorMod(mx.get(), my.get()));
      }
      return my;
    }
    return mx;
  }

  @UseName(name = "min")
  @Extension
  public static @HiddenType(clazz = Long.class) NtMaybe<Long> min_l(@HiddenType(clazz = Long.class) NtMaybe<Long> mx, long y) {
    if (mx.has()) {
      return new NtMaybe<>(min(mx.get(), y));
    }
    return mx;
  }

  /** min */
  @Extension
  public static long min(final long x, final long y) {
    return Math.min(x, y);
  }

  @UseName(name = "min")
  @Extension
  public static @HiddenType(clazz = Long.class) NtMaybe<Long> min_l(long x, @HiddenType(clazz = Long.class) NtMaybe<Long> my) {
    if (my.has()) {
      return new NtMaybe<>(min(x, my.get()));
    }
    return my;
  }

  @UseName(name = "min")
  @Extension
  public static @HiddenType(clazz = Long.class) NtMaybe<Long> min_l(@HiddenType(clazz = Long.class) NtMaybe<Long> mx, @HiddenType(clazz = Long.class) NtMaybe<Long> my) {
    if (mx.has()) {
      if (my.has()) {
        return new NtMaybe<>(min(mx.get(), my.get()));
      }
      return my;
    }
    return mx;
  }

  @UseName(name = "max")
  @Extension
  public static @HiddenType(clazz = Long.class) NtMaybe<Long> max_l(@HiddenType(clazz = Long.class) NtMaybe<Long> mx, long y) {
    if (mx.has()) {
      return new NtMaybe<>(max(mx.get(), y));
    }
    return mx;
  }

  /** max */
  @Extension
  public static long max(final long x, final long y) {
    return Math.max(x, y);
  }

  @UseName(name = "max")
  @Extension
  public static @HiddenType(clazz = Long.class) NtMaybe<Long> max_l(long x, @HiddenType(clazz = Long.class) NtMaybe<Long> my) {
    if (my.has()) {
      return new NtMaybe<>(max(x, my.get()));
    }
    return my;
  }

  @UseName(name = "max")
  @Extension
  public static @HiddenType(clazz = Long.class) NtMaybe<Long> max_l(@HiddenType(clazz = Long.class) NtMaybe<Long> mx, @HiddenType(clazz = Long.class) NtMaybe<Long> my) {
    if (mx.has()) {
      if (my.has()) {
        return new NtMaybe<>(max(mx.get(), my.get()));
      }
      return my;
    }
    return mx;
  }

  @UseName(name = "floorDiv")
  @Extension
  public static @HiddenType(clazz = Long.class) NtMaybe<Long> floorDiv_l(@HiddenType(clazz = Long.class) NtMaybe<Long> mx, long y) {
    if (mx.has()) {
      return new NtMaybe<>(floorDiv(mx.get(), y));
    }
    return mx;
  }

  /** floorDiv */
  @Extension
  public static long floorDiv(final long x, final long y) {
    return Math.floorDiv(x, y);
  }

  @UseName(name = "floorDiv")
  @Extension
  public static @HiddenType(clazz = Long.class) NtMaybe<Long> floorDiv_l(long x, @HiddenType(clazz = Long.class) NtMaybe<Long> my) {
    if (my.has()) {
      return new NtMaybe<>(floorDiv(x, my.get()));
    }
    return my;
  }

  @UseName(name = "floorDiv")
  @Extension
  public static @HiddenType(clazz = Long.class) NtMaybe<Long> floorDiv_l(@HiddenType(clazz = Long.class) NtMaybe<Long> mx, @HiddenType(clazz = Long.class) NtMaybe<Long> my) {
    if (mx.has()) {
      if (my.has()) {
        return new NtMaybe<>(floorDiv(mx.get(), my.get()));
      }
      return my;
    }
    return mx;
  }

  @UseName(name = "floorMod")
  @Extension
  public static @HiddenType(clazz = Long.class) NtMaybe<Long> floorMod_l(@HiddenType(clazz = Long.class) NtMaybe<Long> mx, long y) {
    if (mx.has()) {
      return new NtMaybe<>(floorMod(mx.get(), y));
    }
    return mx;
  }

  /** floorMod */
  @Extension
  public static long floorMod(final long x, final long y) {
    return Math.floorMod(x, y);
  }

  @UseName(name = "floorMod")
  @Extension
  public static @HiddenType(clazz = Long.class) NtMaybe<Long> floorMod_l(long x, @HiddenType(clazz = Long.class) NtMaybe<Long> my) {
    if (my.has()) {
      return new NtMaybe<>(floorMod(x, my.get()));
    }
    return my;
  }

  @UseName(name = "floorMod")
  @Extension
  public static @HiddenType(clazz = Long.class) NtMaybe<Long> floorMod_l(@HiddenType(clazz = Long.class) NtMaybe<Long> mx, @HiddenType(clazz = Long.class) NtMaybe<Long> my) {
    if (mx.has()) {
      if (my.has()) {
        return new NtMaybe<>(floorMod(mx.get(), my.get()));
      }
      return my;
    }
    return mx;
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> radians(@HiddenType(clazz = Double.class) NtMaybe<Double> mx) {
    if (mx.has()) {
      return new NtMaybe<>(radians(mx.get()));
    }
    return mx;
  }

  /** radians */
  @Extension
  public static double radians(final double x) {
    return Math.toRadians(x);
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> degrees(@HiddenType(clazz = Double.class) NtMaybe<Double> mx) {
    if (mx.has()) {
      return new NtMaybe<>(degrees(mx.get()));
    }
    return mx;
  }

  /** degrees */
  @Extension
  public static double degrees(final double x) {
    return Math.toDegrees(x);
  }

  /** ceil */
  @Extension
  public static double ceil(final double x) {
    return Math.ceil(x);
  }

  @Extension
  public static double ceil(final double x, double precision) {
    return Math.ceil(x / precision) * precision;
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> ceil(final @HiddenType(clazz = Double.class) NtMaybe<Double> x) {
    if (x.has()) {
      return new NtMaybe<>(Math.ceil(x.get()));
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> ceil(final @HiddenType(clazz = Double.class) NtMaybe<Double> x, double precision) {
    if (x.has()) {
      return new NtMaybe<>(Math.ceil(x.get() / precision) * precision);
    }
    return new NtMaybe<>();
  }

  /** floor() */
  @Extension
  public static double floor(final double x) {
    return Math.floor(x);
  }

  @Extension
  public static double floor(final double x, double precision) {
    return Math.floor(x / precision) * precision;
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> floor(final @HiddenType(clazz = Double.class) NtMaybe<Double> x) {
    if (x.has()) {
      return new NtMaybe<>(Math.floor(x.get()));
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> floor(final @HiddenType(clazz = Double.class) NtMaybe<Double> x, double precision) {
    if (x.has()) {
      return new NtMaybe<>(Math.floor(x.get() / precision) * precision);
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> round(final @HiddenType(clazz = Double.class) NtMaybe<Double> x) {
    if (x.has()) {
      return new NtMaybe<>(round(x.get()));
    }
    return x;
  }

  /** round() */
  @Extension
  public static double round(final double x) {
    return (int) Math.round(x);
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> round(final @HiddenType(clazz = Double.class) NtMaybe<Double> x, double precision) {
    if (x.has()) {
      return new NtMaybe<>(round(x.get(), precision));
    }
    return x;
  }

  @Extension
  public static double round(final double x, double precision) {
    return Math.round(x / precision) * precision;
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> roundTo(final @HiddenType(clazz = Double.class) NtMaybe<Double> x, int numberOfDigits) {
    if (x.has()) {
      return new NtMaybe<>(roundTo(x.get(), numberOfDigits));
    }
    return x;
  }

  /** roundTo */
  @Extension
  public static double roundTo(final double x, int numberOfDigits) {
    double shift = Math.pow(10, numberOfDigits);
    return Math.round(x * shift) / shift;
  }

  /** NEAR **/
  @Extension
  public static boolean near(final NtComplex a, final NtComplex b) {
    return near(a.real, b.real) && near(a.imaginary, b.imaginary);
  }

  @Extension
  public static boolean near(final double a, final double b) {
    final var diff = Math.abs(a - b);
    return diff < 0.0000001;
  }

  @Extension
  public static boolean near(final NtComplex a, int b) {
    return near(a.real, b) && near(a.imaginary, 0);
  }

  @Extension
  public static boolean near(final NtComplex a, long b) {
    return near(a.real, b) && near(a.imaginary, 0);
  }

  @Extension
  public static boolean near(final NtComplex a, double b) {
    return near(a.real, b) && near(a.imaginary, 0);
  }

  /** XOR */
  @Extension
  public static boolean xor(final boolean a, final boolean b) {
    if (a) {
      return !b;
    }
    return b;
  }

  @Extension
  public static @HiddenType(clazz = NtComplex.class) NtMaybe<NtComplex> conj(final @HiddenType(clazz = NtComplex.class) NtMaybe<NtComplex> x) {
    if (x.has()) {
      return new NtMaybe<>(conj(x.get()));
    }
    return x;
  }

  @Extension
  public static NtComplex conj(final NtComplex x) {
    return new NtComplex(x.real, -x.imaginary);
  }

  @Extension
  public static int abs(int x) {
    return Math.abs(x);
  }

  @Extension
  @UseName(name = "abs")
  public static @HiddenType(clazz = Integer.class) NtMaybe<Integer> abs_i(final @HiddenType(clazz = Integer.class) NtMaybe<Integer> x) {
    if (x.has()) {
      return new NtMaybe<>(Math.abs(x.get()));
    }
    return new NtMaybe<>();
  }

  @Extension
  public static long abs(long x) {
    return Math.abs(x);
  }

  @Extension
  @UseName(name = "abs")
  public static @HiddenType(clazz = Long.class) NtMaybe<Long> abs_l(final @HiddenType(clazz = Long.class) NtMaybe<Long> x) {
    if (x.has()) {
      return new NtMaybe<>(Math.abs(x.get()));
    }
    return new NtMaybe<>();
  }

  @Extension
  public static double abs(double x) {
    return Math.abs(x);
  }

  @Extension
  @UseName(name = "abs")
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> abs_d(final @HiddenType(clazz = Double.class) NtMaybe<Double> x) {
    if (x.has()) {
      return new NtMaybe<>(Math.abs(x.get()));
    }
    return new NtMaybe<>();
  }

  @Extension
  public static double abs(final NtComplex x) {
    return length(x);
  }

  @Extension
  public static double length(final NtComplex x) {
    return Math.sqrt(x.real * x.real + x.imaginary * x.imaginary);
  }

  @Extension
  @UseName(name = "abs")
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> abs_c(final @HiddenType(clazz = NtComplex.class) NtMaybe<NtComplex> x) {
    return length(x);
  }

  @Extension
  public static @HiddenType(clazz = Double.class) NtMaybe<Double> length(final @HiddenType(clazz = NtComplex.class) NtMaybe<NtComplex> x) {
    if (x.has()) {
      return new NtMaybe<>(length(x.get()));
    }
    return new NtMaybe<>();
  }

  public static boolean intersects(int a, int b, int c, int d) {
    return max(a, c) <= min(b, d);
  }

  public static boolean intersects(double a, double b, double c, double d) {
    return max(a, c) <= min(b, d);
  }

  public static boolean intersects(long a, long b, long c, long d) {
    return max(a, c) <= min(b, d);
  }

  @Extension
  public static boolean isTrue(@HiddenType(clazz = Boolean.class) NtMaybe<Boolean> x) {
    if (x.has()) {
      return x.get();
    }
    return false;
  }

  @Skip
  public static <T> boolean equality(NtMaybe<T> x, T y, BiFunction<T, T, Boolean> check) {
    if (x.has()) {
      return check.apply(x.get(), y);
    }
    return false;
  }

  @Extension
  public static int forceId(@HiddenType(clazz = Integer.class) NtMaybe<Integer> x) {
    if (x.has()) {
      return x.get();
    }
    return -1;
  }

  public static boolean dRangeII(double low, double x, double high) {
    return low <= x && x <= high;
  }

  public static boolean dRangeIE(double low, double x, double high) {
    return low <= x && x < high;
  }

  public static boolean dRangeEI(double low, double x, double high) {
    return low < x && x <= high;
  }

  public static boolean dRangeEE(double low, double x, double high) {
    return low < x && x < high;
  }
}
