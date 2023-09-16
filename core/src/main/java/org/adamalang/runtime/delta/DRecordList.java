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
package org.adamalang.runtime.delta;

import org.adamalang.runtime.contracts.DeltaNode;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;

import java.util.*;
import java.util.function.Supplier;

/** a list of records that will respect privacy and sends state to client only on changes */
public class DRecordList<dRecordTy extends DeltaNode> implements DeltaNode {
  public final HashMap<Integer, dRecordTy> cache;
  public final ArrayList<Integer> order;

  public DRecordList() {
    this.order = new ArrayList<>();
    this.cache = new HashMap<>();
  }

  @Override
  public void clear() {
    this.order.clear();
    this.cache.clear();
  }

  /** memory usage */
  @Override
  public long __memory() {
    long memory = order.size() * 32L;
    for (Map.Entry<Integer, dRecordTy> entry : cache.entrySet()) {
      memory += 40 + entry.getValue().__memory();
    }
    return memory;
  }

  /** start walking the records */
  public Walk begin() {
    return new Walk();
  }

  /** get the cached item */
  public dRecordTy getPrior(final int id, final Supplier<dRecordTy> maker) {
    var prior = cache.get(id);
    if (prior == null) {
      prior = maker.get();
      cache.put(id, prior);
    }
    return prior;
  }

  /** the list of records is no longer visible (was made private) */
  public void hide(final PrivateLazyDeltaWriter writer) {
    if (cache.size() > 0) {
      order.clear();
      cache.clear();
      writer.writeNull();
    }
  }

  public class Walk {
    private final ArrayList<Integer> newOrdering;
    private final Iterator<Integer> oldOrderingIt;
    private final HashSet<Integer> seen;
    private boolean orderingUnchanged;

    private Walk() {
      newOrdering = new ArrayList<>();
      orderingUnchanged = true;
      oldOrderingIt = order.iterator();
      seen = new HashSet<>();
    }

    /** we completely walked over the map */
    public void end(final PrivateLazyDeltaWriter parent) {
      // we didn't invalidate the ordering
      if (orderingUnchanged) {
        // let's make sure the ordering respected the size
        orderingUnchanged = newOrdering.size() == order.size();
      }
      // the ordering did not change, so let's send a new ordering differential
      if (!orderingUnchanged) {
        final var orderingField = parent.planField("@o");
        final var array = orderingField.planArray();
        array.manifest();
        final var keyToOldPosition = new HashMap<Integer, Integer>();
        // let's record the hold positions of the keys and their index
        for (var k = 0; k < order.size(); k++) {
          keyToOldPosition.put(order.get(k), k);
        }
        // let's walk the new ordering
        for (var k = 0; k < newOrdering.size(); k++) {
          final int newOrderKey = newOrdering.get(k);
          final var oldPosition = keyToOldPosition.get(newOrderKey);
          if (oldPosition != null) {
            // the new key has a key within the old array, cool
            var good = true;
            var top = k;
            int trackPosition = oldPosition;
            // let's see how much of the new ordering starting at k is a sub ordering of the old
            for (var j = k + 1; good && j < newOrdering.size(); j++) {
              final int testOrderKey = newOrdering.get(j);
              final var testOldPosition = keyToOldPosition.get(testOrderKey);
              if (testOldPosition == null || testOldPosition.intValue() != trackPosition + 1) {
                good = false;
              } else {
                top = j;
                trackPosition++;
              }
            }
            if (top - k < 2) {
              // too not enough overlap, write the new key and move on
              array.writeInt(newOrderKey);
            } else {
              // ok, now write a range and skip the items
              final var rangeArr = array.planArray();
              rangeArr.writeInt(oldPosition);
              rangeArr.writeInt(oldPosition + (top - k));
              rangeArr.end();
              k = top;
            }
          } else {
            // just directly write the new key and move on
            array.writeInt(newOrderKey);
          }
        }
        array.end();
        order.clear();
        order.addAll(newOrdering);
      }
      final var cacheIt = cache.entrySet().iterator();
      while (cacheIt.hasNext()) {
        final var entry = cacheIt.next();
        if (!seen.contains(entry.getKey())) {
          cacheIt.remove();
          parent.planField(entry.getKey()).writeNull();
        }
      }
    }

    /** a new id shows up */
    public void next(final int id) {
      seen.add(id);
      newOrdering.add(id);
      if (orderingUnchanged) {
        if (oldOrderingIt.hasNext()) {
          if (id != oldOrderingIt.next()) {
            orderingUnchanged = false;
          }
        }
      }
    }
  }
}
