/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.contracts;

/** generalizes the process of building a query set */
public interface IndexQuerySet {
  /** intersect the set with the given index (via index datastrcture) and the
   * given value. INDEX_FIELD == VALUE */
  public void intersect(int column, int value);
}
