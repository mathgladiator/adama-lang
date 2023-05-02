/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
