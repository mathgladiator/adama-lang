/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.contracts;

/** iterate where CLAUSE; this is the interface which defines how to select items from a list */
public interface WhereClause<T> {
  /**
   * compute the indices of the where clauses. This array is a linear associative map of the form
   * [col0, val0, col1, val1, ... colN, valN]
   */
  int[] getIndices();

  /**
   * does the where clause leverage the primary key (i.e. ID == VALUE). If not null, then return
   * VALUE
   */
  Integer getPrimaryKey();

  /**
   * the where clause is able to manipulate the index query set to exploit what it knows about the
   * expression
   */
  void scopeByIndicies(IndexQuerySet __set);

  /** evaluate the where clause precisely */
  boolean test(T item);
}
