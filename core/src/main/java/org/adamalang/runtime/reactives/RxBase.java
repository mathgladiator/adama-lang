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
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;

import java.util.ArrayList;

/** the base class of any reactive object */
public abstract class RxBase {
  protected final RxParent __parent;
  private boolean __dirty;
  private ArrayList<RxChild> __subscribers;
  private boolean __notifying;

  protected RxBase(final RxParent __parent) {
    this.__parent = __parent;
    __subscribers = null;
    __notifying = false;
  }

  /** disconnect all subscriptions */
  public void __cancelAllSubscriptions() {
    __subscribers = null;
  }

  /** commit the changes to the object, and emit a delta */
  public abstract void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta);

  /** take a dump of the data */
  public abstract void __dump(JsonStreamWriter writer);

  /** how many children are subscribed to this item */
  public int __getSubscriberCount() {
    if (__subscribers != null) {
      return __subscribers.size();
    }
    return 0;
  }

  /** initialize data & merge data in */
  public abstract void __insert(JsonStreamReader reader);

  /** patch the data */
  public abstract void __patch(JsonStreamReader reader);

  /** is the data dirty within this item */
  public boolean __isDirty() {
    return __dirty;
  }

  /** lower the dirtiness based on a commit */
  public void __lowerDirtyCommit() {
    __dirty = false;
  }

  /** lower the dirtiness based on a revert; will invalidate subscribers */
  public void __lowerDirtyRevert() {
    __dirty = false;
    __invalidateSubscribers();
  }

  /** tell all subscribers that they need to recompute */
  protected void __invalidateSubscribers() {
    if (__subscribers != null && !__notifying) {
      __notifying = true;
      final var it = __subscribers.iterator();
      while (it.hasNext()) {
        if (!it.next().__raiseInvalid()) {
          it.remove();
        }
      }
    }
    __notifying = false;
  }

  /** inform the object that it is dirty, which in turn will notify the parents */
  public void __raiseDirty() {
    __dirty = true;
    if (__parent != null) {
      __parent.__raiseDirty();
    }
    __invalidateSubscribers();
  }

  /** rollback state */
  public abstract void __revert();

  /** subscribe a child to the state of this object */
  public void __subscribe(final RxChild link) {
    if (__subscribers == null) {
      __subscribers = new ArrayList<>();
    }
    __subscribers.add(link);
  }

  /** return the rough # of bytes */
  public long __memory() {
    return 40L;
  }
}
