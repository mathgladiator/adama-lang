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
  protected boolean __invalid;

  protected RxBase(final RxParent __parent) {
    this.__parent = __parent;
    __subscribers = null;
    __notifying = false;
    __invalid = false;
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

  public void __reportRx(String name, JsonStreamWriter __writer) {
    __writer.writeObjectFieldIntro(name);
    __writer.writeInteger(__getSubscriberCount());
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
    __invalid = false;
  }

  /** lower the dirtiness based on a revert; will invalidate subscribers */
  public void __lowerDirtyRevert() {
    __dirty = false;
    __invalidateSubscribers();
    __invalid = false;
  }

  public void __lowerInvalid() {
    __invalid = false;
  }

  /** tell all subscribers that they need to recompute */
  protected void __invalidateSubscribers() {
    if (__invalid) {
      return;
    }
    __invalid = true;
    if (__parent != null) {
      __parent.__invalidateUp();
    }
    if (__subscribers != null && !__notifying) {
      __notifying = true;
      try {
        final var it = __subscribers.iterator();
        while (it.hasNext()) {
          if (!it.next().__raiseInvalid()) {
            it.remove();
          }
        }
      } finally {
        __notifying = false;
      }
    }
  }

  /** inform the object that it is dirty, which in turn will notify the parents */
  public void __raiseDirty() {
    if (__dirty) {
      return;
    }
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
