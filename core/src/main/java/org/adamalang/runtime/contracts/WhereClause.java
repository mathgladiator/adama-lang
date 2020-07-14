/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.contracts;

/** iterate where CLAUSE; this is the interface which defines how to select items from a list */
public interface WhereClause<T> {
  /** compute the indices of the where clauses. This array is a linear associative map of the form [col0, val0, col1, val1, ... colN, valN] */
  public int[] getIndices();

  /** does the where clause leverage the primary key (i.e. ID == VALUE). If not null, then return VALUE */
  public Integer getPrimaryKey();

  /** the where clause is able to manipulate the index query set to exploit what it knows about the expression */
  public void scopeByIndicies(IndexQuerySet __set);

  /** evaluate the where clause precisely */
  public boolean test(T item);
}
