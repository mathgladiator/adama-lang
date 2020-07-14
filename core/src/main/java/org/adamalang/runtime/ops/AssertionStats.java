/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.ops;

/** what happened during a run */
public class AssertionStats {
  public final int failures;
  public final int total;

  public AssertionStats(final int total, final int failures) {
    this.total = total;
    this.failures = failures;
  }
}
