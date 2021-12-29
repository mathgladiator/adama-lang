/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.RxChild;
import org.adamalang.runtime.contracts.RxParent;

/** the base object for generated record types */
public abstract class RxRecordBase<Ty extends RxRecordBase> extends RxBase implements Comparable<Ty>, RxParent, RxChild {
  private boolean __alive;
  protected boolean __isDying;

  public RxRecordBase(final RxParent __owner) {
    super(__owner);
    this.__alive = true;
    this.__isDying = false;
  }

  public abstract void __deindex();

  public void __delete() {
    __isDying = true;
    __raiseDirty();
  }

  public abstract String[] __getIndexColumns();
  public abstract int[] __getIndexValues();
  public abstract int __id();

  public boolean __isDying() {
    return __isDying;
  }

  public void __kill() {
    __isDying = true;
    __alive = false;
  }

  public abstract String __name();

  @Override
  public void __raiseDirty() {
    super.__raiseDirty();
  }

  @Override
  public boolean __raiseInvalid() {
    __invalidateSubscribers();
    return __alive;
  }

  public abstract void __reindex();
  public abstract void __setId(int __id, boolean __useForce);

  @Override
  public int compareTo(final Ty o) {
    // induce a default ordering, perhaps?
    return __id() - o.__id();
  }

  @Override
  public boolean equals(final Object o) {
    if (o instanceof RxRecordBase) { return __id() == ((RxRecordBase) o).__id(); }
    return false;
  }

  @Override
  public int hashCode() {
    return __id();
  }

  @Override
  public long __memory() {
    return super.__memory() + 2;
  }
}
