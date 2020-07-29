/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.RxChild;
import org.adamalang.runtime.contracts.RxParent;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** the base object for generated record types */
public abstract class RxRecordBase<Ty extends RxRecordBase> extends RxBase implements Comparable<Ty>, RxParent, RxChild {
  private boolean __alive;
  protected boolean __isDying;
  private ObjectNode cachedObjectNode;

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
    cachedObjectNode = null;
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

  public ObjectNode getCachedObjectNode() {
    return cachedObjectNode;
  }

  @Override
  public int hashCode() {
    return __id();
  }

  public void setCachedObjectNode(final ObjectNode objectNode) {
    this.cachedObjectNode = objectNode;
  }
}
