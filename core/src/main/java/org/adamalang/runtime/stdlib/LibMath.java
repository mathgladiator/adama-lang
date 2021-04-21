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

/** a very simple math library which extends some of the already exposed Math
 * calls */
public class LibMath {
  public static double SQRT2 = 1.4142135623730950488016887242097;

  public static int ceil(final double x) {
    return (int) Math.ceil(x);
  }

  public static int floor(final double x) {
    return (int) Math.floor(x);
  }

  public static boolean near(final double a, final double b) {
    final var diff = Math.abs(a - b);
    return diff < 0.0000001;
  }

  public static int round(final double x) {
    return (int) Math.round(x);
  }
}
