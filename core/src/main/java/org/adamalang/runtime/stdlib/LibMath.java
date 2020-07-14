/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.stdlib;

/** a very simple math library which extends some of the already exposed Math calls */
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
