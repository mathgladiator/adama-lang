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
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.MultiIndexable;
import org.adamalang.runtime.contracts.RxChild;
import org.adamalang.runtime.contracts.RxKillable;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.reactives.tables.TablePubSub;

/** the base object for generated record types */
public abstract class RxRecordBase<Ty extends RxRecordBase> extends RxBase implements Comparable<Ty>, MultiIndexable, RxParent, RxChild, RxKillable {
  protected boolean __isDying;
  private boolean __alive;
  private TablePubSub pubsub;

  public RxRecordBase(final RxParent __owner) {
    super(__owner);
    this.__alive = true;
    this.__isDying = false;
    if (__owner instanceof RxTable) {
      this.pubsub = ((RxTable<?>) __owner).pubsub;
    } else {
      this.pubsub = null;
    }
  }

  public abstract Ty __link();

  public abstract void __deindex();

  private void __raiseDying() {
    if (!__isDying) {
      __isDying = true;
      if (pubsub != null) {
        pubsub.remove(__id());
      }
    }
  }

  public void __delete() {
    __raiseDying();
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

  public boolean __isDying() {
    return __isDying;
  }

  @Override
  public void __kill() {
    __raiseDying();
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

  @Override
  public void __cost(int cost) {
    if (__parent != null) {
      __parent.__cost(cost);
    }
  }

  public abstract void __reindex();

  public abstract void __setId(int __id, boolean __useForce);

  public abstract void __pumpIndexEvents(TablePubSub pubsub);

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

  public abstract Object __fieldOf(String name);
}
