/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.stdlib;

import org.adamalang.translator.reflect.Extension;

/** a very simple math library which extends some of the already exposed Math
 * calls */
public class LibMath {
  public static double SQRT2 = 1.4142135623730950488016887242097;

  @Extension
  public static int ceil(final double x) {
    return (int) Math.ceil(x);
  }

  @Extension
  public static int floor(final double x) {
    return (int) Math.floor(x);
  }

  @Extension
  public static boolean near(final double a, final double b) {
    final var diff = Math.abs(a - b);
    return diff < 0.0000001;
  }

  @Extension
  public static int round(final double x) {
    return (int) Math.round(x);
  }

  @Extension
  public static double roundTo(final double x, int numberOfDigits) {
    double shift = Math.pow(10, numberOfDigits);
    return Math.round(x * shift) / shift;
  }
}
