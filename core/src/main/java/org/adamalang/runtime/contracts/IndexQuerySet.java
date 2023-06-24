/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
