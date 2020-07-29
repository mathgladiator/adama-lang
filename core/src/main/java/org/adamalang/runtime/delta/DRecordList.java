/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.delta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Supplier;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;

public class DRecordList<dRecordTy> {
  public class Walk {
    private final ArrayList<Integer> newOrdering;
    private final Iterator<Integer> oldOrderingIt;
    private boolean orderingUnchanged;
    private final HashSet<Integer> seen;

    private Walk() {
      newOrdering = new ArrayList<>();
      orderingUnchanged = true;
      oldOrderingIt = order.iterator();
      seen = new HashSet<>();
    }

    public void end(final PrivateLazyDeltaWriter parent) {
      final var orderingField = parent.planField("@o");
      if (orderingUnchanged) {
        orderingUnchanged = newOrdering.size() == order.size();
      }
      if (!orderingUnchanged) {
        final var array = orderingField.planArray();
        array.manifest();
        final var keyToOldPosition = new HashMap<Integer, Integer>();
        for (var k = 0; k < order.size(); k++) {
          keyToOldPosition.put(order.get(k), k);
        }
        for (var k = 0; k < newOrdering.size(); k++) {
          final int newOrderKey = newOrdering.get(k);
          final var oldPosition = keyToOldPosition.get(newOrderKey);
          if (oldPosition != null) {
            var good = true;
            var top = k;
            int trackPosition = oldPosition;
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
              array.writeFastString("" + newOrderKey);
            } else {
              final var rangeObj = array.planObject();
              final var rangeArr = rangeObj.planField("@r").planArray();
              rangeArr.writeInt(k);
              rangeArr.writeInt(top);
              rangeArr.end();
              rangeObj.end();
              k = top;
            }
          } else {
            array.writeFastString("" + newOrderKey);
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

  public final HashMap<Integer, dRecordTy> cache;
  public final ArrayList<Integer> order;

  public DRecordList() {
    this.order = new ArrayList<>();
    this.cache = new HashMap<>();
  }

  public Walk begin() {
    return new Walk();
  }

  public dRecordTy getPrior(final int id, final Supplier<dRecordTy> maker) {
    var prior = cache.get(id);
    if (prior == null) {
      prior = maker.get();
      cache.put(id, prior);
    }
    return prior;
  }

  public void hide(final PrivateLazyDeltaWriter writer) {
    if (cache.size() > 0) {
      order.clear();
      cache.clear();
      writer.writeNull();
    }
  }
}