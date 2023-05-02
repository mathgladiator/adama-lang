/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.contracts;

/** generalizes the process of building a query set */
public interface IndexQuerySet {
  /**
   * intersect the set with the given index (via index datastrcture) and the given value.
   * INDEX_FIELD == VALUE
   */
  void intersect(int column, int value, LookupMode mode);

  /** Method of executing the lookup */
  enum LookupMode {
    LessThan, LessThanOrEqual, Equals, GreaterThanOrEqual, GreaterThan
  }
}
