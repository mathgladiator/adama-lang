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
package org.adamalang.runtime.delta;

import org.adamalang.runtime.contracts.DeltaNode;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Supplier;

/** a map that will respect privacy and sends state to client only on changes */
public class DMap<TyIn, dTyOut extends DeltaNode> implements DeltaNode {
  // cache of all the items in the map
  private final HashMap<TyIn, dTyOut> cache;

  public DMap() {
    this.cache = new HashMap<>();
  }

  /** start walking items; the generated code in CodeGenDeltaClass is related */
  public DMap.Walk begin() {
    return new DMap.Walk();
  }

  /** the map is no longer visible (was made private) */
  public void hide(final PrivateLazyDeltaWriter writer) {
    if (cache.size() > 0) {
      cache.clear();
      writer.writeNull();
    }
  }

  @Override
  public void clear() {
    cache.clear();
  }

  /** memory usage */
  @Override
  public long __memory() {
    long memory = 40;
    for (Map.Entry<TyIn, dTyOut> entry : cache.entrySet()) {
      memory += 40 + entry.getValue().__memory();
    }
    return memory;
  }

  public class Walk {
    private final HashSet<TyIn> seen;

    private Walk() {
      this.seen = new HashSet<>();
    }

    /** a new element in the map */
    public dTyOut next(final TyIn key, final Supplier<dTyOut> maker) {
      seen.add(key);
      var value = cache.get(key);
      if (value == null) {
        value = maker.get();
        cache.put(key, value);
      }
      return value;
    }

    /** the map iteration is over */
    public void end(final PrivateLazyDeltaWriter parent) {
      final var cacheIt = cache.entrySet().iterator();
      while (cacheIt.hasNext()) {
        final var entry = cacheIt.next();
        if (!seen.contains(entry.getKey())) {
          cacheIt.remove();
          parent.planField("" + entry.getKey()).writeNull();
        }
      }
    }
  }
}
