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
package org.adamalang.runtime.index;

import org.adamalang.runtime.contracts.RxChild;
import org.adamalang.runtime.reactives.RxRecordBase;

/**
 * an index value must respond to change, and this enables that indexing to occur reactively to data
 * changes.
 */
public abstract class ReactiveIndexInvalidator<Ty extends RxRecordBase<Ty>> implements RxChild {
  private final ReactiveIndex<Ty> index;
  private final Ty item;
  private Integer indexedAt;

  public ReactiveIndexInvalidator(final ReactiveIndex<Ty> index, final Ty item) {
    this.index = index;
    this.item = item;
    this.indexedAt = null;
  }

  /** a change happened, so remove from the index */
  @Override
  public boolean __raiseInvalid() {
    if (indexedAt != null) {
      index.remove(indexedAt, item);
      indexedAt = null;
    }
    return true;
  }

  /** index the item by it's given value */
  public void reindex() {
    if (indexedAt == null) {
      indexedAt = pullValue();
      index.add(indexedAt, item);
    }
  }

  /** pull the value to index on */
  public abstract int pullValue();

  /** remove from all index */
  public void deindex() {
    if (indexedAt != null) {
      index.delete(indexedAt, item);
      indexedAt = null;
    } else {
      index.delete(item);
    }
  }
}
