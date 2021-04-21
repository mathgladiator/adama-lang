/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.index;

import org.adamalang.runtime.contracts.RxChild;
import org.adamalang.runtime.reactives.RxRecordBase;

/** an index value must respond to change, and this enables that indexing to
 * occur reactively to data changes */
public abstract class ReactiveIndexInvalidator<Ty extends RxRecordBase<Ty>> implements RxChild {
  private final ReactiveIndex<Ty> index;
  private Integer indexedAt;
  private final Ty item;

  public ReactiveIndexInvalidator(final ReactiveIndex<Ty> index, final Ty item) {
    this.index = index;
    this.item = item;
    this.indexedAt = null;
  }

  @Override
  public boolean __raiseInvalid() {
    if (indexedAt != null) {
      index.remove(indexedAt, item);
      indexedAt = null;
    }
    return true;
  }

  public void deindex() {
    if (indexedAt != null) {
      index.delete(indexedAt, item);
      indexedAt = null;
    } else {
      index.delete(item);
    }
  }

  public abstract int pullValue();

  public void reindex() {
    if (indexedAt == null) {
      indexedAt = pullValue();
      index.add(indexedAt, item);
    }
  }
}
