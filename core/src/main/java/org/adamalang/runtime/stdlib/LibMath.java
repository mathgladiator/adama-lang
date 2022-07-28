/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtComplex;
import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.translator.reflect.Extension;
import org.adamalang.translator.reflect.HiddenType;

import java.util.function.BiFunction;

/** a very simple math library which extends some of the already exposed Math calls */
public class LibMath {
  public static double SQRT2 = 1.4142135623730950488016887242097;

  @Extension
  public static NtComplex sqrt(final double x) {
    if (x < 0) {
      return new NtComplex(0.0, Math.sqrt(-x));
    }
    return new NtComplex(Math.sqrt(x), 0.0);
  }

  @Extension
  public static double ceil(final double x) {
    return Math.ceil(x);
  }

  @Extension
  public static double floor(final double x) {
    return Math.floor(x);
  }

  @Extension
  public static double ceil(final double x, double precision) {
    return Math.ceil(x / precision) * precision;
  }

  @Extension
  public static double floor(final double x, double precision) {
    return Math.floor(x / precision) * precision;
  }

  @Extension
  public static @HiddenType(clazz=Double.class) NtMaybe<Double> ceil(final @HiddenType(clazz=Double.class) NtMaybe<Double> x) {
    if (x.has()) {
      return new NtMaybe<>(Math.ceil(x.get()));
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz=Double.class) NtMaybe<Double> floor(final @HiddenType(clazz=Double.class) NtMaybe<Double> x) {
    if (x.has()) {
      return new NtMaybe<>(Math.floor(x.get()));
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz=Double.class) NtMaybe<Double> ceil(final @HiddenType(clazz=Double.class) NtMaybe<Double> x, double precision) {
    if (x.has()) {
      return new NtMaybe<>(Math.ceil(x.get() / precision) * precision);
    }
    return new NtMaybe<>();
  }

  @Extension
  public static @HiddenType(clazz=Double.class) NtMaybe<Double> floor(final @HiddenType(clazz=Double.class) NtMaybe<Double> x, double precision) {
    if (x.has()) {
      return new NtMaybe<>(Math.floor(x.get() / precision) * precision);
    }
    return new NtMaybe<>();
  }

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

  @Extension
  public static boolean xor(final boolean a, final boolean b) {
    if (a) {
      return !b;
    }
    return b;
  }

  @Extension
  public static double round(final double x) {
    return (int) Math.round(x);
  }

  @Extension
  public static double round(final double x, double precision) {
    return Math.round(x / precision) * precision;
  }

  @Extension
  public static double roundTo(final double x, int numberOfDigits) {
    double shift = Math.pow(10, numberOfDigits);
    return Math.round(x * shift) / shift;
  }


  @Extension
  public static NtComplex conj(final NtComplex x) {
    return new NtComplex(x.real, -x.imaginary);
  }

  @Extension
  public static @HiddenType(clazz=NtComplex.class) NtMaybe<NtComplex> conj(final @HiddenType(clazz=NtComplex.class)NtMaybe<NtComplex> x) {
    if (x.has()) {
      return new NtMaybe<>(conj(x.get()));
    }
    return x;
  }

  @Extension
  public static double abs(final double x) {
    return Math.abs(x);
  }

  @Extension
  public static int abs(final int x) {
    return Math.abs(x);
  }

  @Extension
  public static long abs(final long x) {
    return Math.abs(x);
  }

  @Extension
  public static @HiddenType(clazz=Double.class) NtMaybe<Double> abs(final @HiddenType(clazz=Double.class) NtMaybe<Double> x) {
    if (x.has()) {
      return new NtMaybe<>(Math.abs(x.get()));
    }
    return new NtMaybe<>();
  }

  @Extension
  public static double length(final NtComplex x) {
    return Math.sqrt(x.real * x.real + x.imaginary * x.imaginary);
  }

  @Extension
  public static @HiddenType(clazz=Double.class) NtMaybe<Double> length(final @HiddenType(clazz=NtComplex.class) NtMaybe<NtComplex> x) {
    if (x.has()) {
      return new NtMaybe<>(length(x.get()));
    }
    return new NtMaybe<>();
  }

  public static boolean isTrue(@HiddenType(clazz = Boolean.class) NtMaybe<Boolean> x) {
    if (x.has()) {
      return x.get();
    }
    return false;
  }

  public static <T> boolean equality(NtMaybe<T> x, T y, BiFunction<T, T, Boolean> check) {
    if (x.has()) {
      return check.apply(x.get(), y);
    }
    return false;
  }
}
