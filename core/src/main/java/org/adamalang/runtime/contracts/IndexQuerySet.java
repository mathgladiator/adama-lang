/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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

import org.adamalang.runtime.natives.NtMaybe;

/** generalizes the process of building a query set */
public abstract class IndexQuerySet {
  /**
   * intersect the set with the given index (via index datastrcture) and the given value.
   * INDEX_FIELD == VALUE
   */
  public abstract void intersect(int column, int value, LookupMode mode);

  public void intersect(int column, NtMaybe<Integer> value, LookupMode mode) {
    if (value.has()) {
      intersect(column, value.get(), mode);
    } else {
      // treat the null case as a 0
      intersect(column, 0, mode);
    }
  }

  /** within a branch, pick a primary key as the value */
  public abstract void primary(int value);

  /** push the result */
  public abstract void push();

  /** finish up the result */
  public abstract void finish();

  /** Method of executing the lookup */
  public static enum LookupMode {
    LessThan, LessThanOrEqual, Equals, GreaterThanOrEqual, GreaterThan
  }
}
