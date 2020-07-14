/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.contracts;

/** the child of a reactive expression/data */
public interface RxChild {
  /** return true if still alive */
  public boolean __raiseInvalid();
}
