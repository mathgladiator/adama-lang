/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
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
