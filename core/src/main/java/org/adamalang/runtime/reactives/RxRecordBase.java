/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.RxChild;
import org.adamalang.runtime.contracts.RxKillable;
import org.adamalang.runtime.contracts.RxParent;

/** the base object for generated record types */
public abstract class RxRecordBase<Ty extends RxRecordBase> extends RxBase implements Comparable<Ty>, RxParent, RxChild, RxKillable {
  protected boolean __isDying;
  private boolean __alive;

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

  @Override
  public void __raiseDirty() {
    super.__raiseDirty();
  }

  @Override
  public long __memory() {
    return super.__memory() + 2;
  }

  public abstract String[] __getIndexColumns();

  public abstract int[] __getIndexValues();

  public boolean __isDying() {
    return __isDying;
  }

  @Override
  public void __kill() {
    __isDying = true;
    __alive = false;
    __killFields();
  }

  public abstract void __killFields();

  public abstract String __name();

  @Override
  public boolean __raiseInvalid() {
    __invalidateSubscribers();
    return __alive;
  }

  @Override
  public boolean __isAlive() {
    if (__parent != null) {
      if (!__parent.__isAlive()) {
        return false;
      }
    }
    return __alive;
  }

  public abstract void __reindex();

  public abstract void __setId(int __id, boolean __useForce);

  @Override
  public int compareTo(final Ty o) {
    // induce a default ordering, perhaps?
    return __id() - o.__id();
  }

  public abstract int __id();

  @Override
  public int hashCode() {
    return __id();
  }

  @Override
  public boolean equals(final Object o) {
    if (o instanceof RxRecordBase) {
      return __id() == ((RxRecordBase) o).__id();
    }
    return false;
  }
}
