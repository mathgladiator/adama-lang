/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.contracts;

/** time is not a function, so we make it a function */
public interface TimeSource {
  public static final TimeSource REAL_TIME = System::currentTimeMillis;

  /** get the current time */
  public long nowMilliseconds();
}
