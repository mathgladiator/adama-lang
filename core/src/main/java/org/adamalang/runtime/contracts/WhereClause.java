/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
