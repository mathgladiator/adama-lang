/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.stdlib;

/** a basic string library */
public class LibString {
  public static int compare(final String a, final String b) {
    if (a == null && b == null) { return 0; }
    if (a == null && b != null) { return -1; }
    if (a != null && b == null) { return 1; }
    return a.compareTo(b);
  }

  public static boolean equality(final String a, final String b) {
    if (a == null && b == null) { return true; }
    if (a == null || b == null) { return false; }
    return a.equals(b);
  }

  public static String multiply(final String input, final int count) {
    final var sb = new StringBuilder();
    for (var k = 0; k < count; k++) {
      sb.append(input);
    }
    return sb.toString();
  }

  public static String of(final boolean x) {
    return String.valueOf(x);
  }

  public static String of(final double x) {
    return String.valueOf(x);
  }

  public static String of(final int x) {
    return String.valueOf(x);
  }

  public static String of(final long x) {
    return String.valueOf(x);
  }

  public static String reverse(final String x) {
    final var sb = new StringBuilder();
    sb.append(x);
    return sb.reverse().toString();
  }
}
