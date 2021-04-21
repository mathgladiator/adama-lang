/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.contracts;

/** generalizes the process of building a query set */
public interface IndexQuerySet {
  /** intersect the set with the given index (via index datastrcture) and the
   * given value. INDEX_FIELD == VALUE */
  public void intersect(int column, int value);
}
