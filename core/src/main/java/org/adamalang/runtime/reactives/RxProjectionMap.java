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

import org.adamalang.runtime.contracts.RxChild;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.natives.NtMaybe;
import org.adamalang.runtime.reactives.maps.MapGuardTarget;
import org.adamalang.runtime.reactives.maps.MapPubSub;
import org.adamalang.runtime.reactives.tables.TableSubscription;

import java.util.HashMap;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;

/** A projection of a field out of a table into a map */
public class RxProjectionMap<RowTy extends RxRecordBase<RowTy>, RangeTy extends RxBase> extends RxNerfedBase implements RxParent, RxChild, MapGuardTarget {
  private final RxTable<RowTy> table;
  private final HashMap<Integer, RowTy> cache;
  private final Function<RowTy, RangeTy> extract;
  private final MapPubSub<Integer> pubsub;
  private final Stack<RxMapGuard> guardsInflight;
  private RxMapGuard activeGuard;

  protected RxProjectionMap(RxParent __parent, RxTable<RowTy> table, Function<RowTy, RangeTy> extract) {
    super(__parent);
    this.table = table;
    this.cache = new HashMap<>();
    this.extract = extract;
    this.pubsub = new MapPubSub<>(this);
    this.guardsInflight = new Stack<>();
    this.activeGuard = null;
    // subscribe to the table
    table.pubsub.subscribe(new TableSubscription() {
      @Override
      public boolean alive() {
        return __isAlive();
      }

      @Override
      public boolean primary(int primaryKey) {
        invalidate(primaryKey);
        return false;
      }

      @Override
      public void index(int index, int value) {
      }
    });
  }

  private void forward(int key) {
    pubsub.changed(key);
  }

  private void invalidate(int primaryKey) {
    if (cache.containsKey(primaryKey)) {
      if (!table.has(primaryKey)) {
        // deletions must propagate
        cache.remove(primaryKey);
        forward(primaryKey);
      }
      return;
    } else {
      RowTy row = table.getById(primaryKey);
      if (row != null) {
        RangeTy item = extract.apply(row);
        item.__subscribe(() -> {
          forward(primaryKey);
          return RxProjectionMap.this.__isAlive();
        });
        cache.put(primaryKey, row);
        forward(primaryKey);
      }
    }
  }

  public NtMaybe<RowTy> lookup(Integer key) {
    if (activeGuard != null) {
      activeGuard.readKey(key);
    }
    if (__parent != null) {
      __parent.__cost(4);
    }
    return new NtMaybe<>(cache.get(key));
  }

  public void __subscribe(RxMapGuard<Integer> guard) {
    pubsub.subscribe(guard);
  }

  @Override
  public void __settle(Set<Integer> viewers) {
    pubsub.settle();
    pubsub.gc();
  }

  @Override
  public boolean __raiseInvalid() {
    return __isAlive();
  }

  @Override
  public boolean __isAlive() {
    if (__parent != null) {
      return __parent.__isAlive();
    }
    return true;
  }

  @Override
  public void __cost(int cost) {
    if (__parent != null) {
      __parent.__cost(cost);
    }
  }

  @Override
  public void __invalidateUp() {
    if (__parent != null) {
      __parent.__invalidateUp();
    }
  }

  @Override
  public void pushGuard(RxMapGuard guard) {
    guardsInflight.push(guard);
    activeGuard = guard;
  }

  @Override
  public void popGuard() {
    guardsInflight.pop();
    if (guardsInflight.empty()) {
      activeGuard = null;
    } else {
      activeGuard = guardsInflight.peek();
    }
  }

  public long memory() {
    return 1024 + 128 * cache.size();
  }
}
