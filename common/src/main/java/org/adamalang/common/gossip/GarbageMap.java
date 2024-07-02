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
package org.adamalang.common.gossip;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/** represents a map that cleans things up based on time */
public class GarbageMap<T> implements Iterable<T> {

  private final LinkedHashMap<String, AgingItem> items;

  public GarbageMap(int maxSize) {
    this.items = new LinkedHashMap<>(maxSize, 0.75f, true) {
      @Override
      protected boolean removeEldestEntry(Map.Entry<String, AgingItem> eldest) {
        return this.size() > maxSize;
      }
    };
  }

  public Collection<String> keys() {
    return items.keySet();
  }

  @Override
  public Iterator<T> iterator() {
    Iterator<AgingItem> it = items.values().iterator();
    return new Iterator<T>() {
      @Override
      public boolean hasNext() {
        return it.hasNext();
      }

      @Override
      public T next() {
        return it.next().item;
      }
    };
  }

  public void put(String key, T value, long now) {
    items.put(key, new AgingItem(value, now));
  }

  public T get(String key) {
    AgingItem item = items.get(key);
    if (item != null) {
      return item.item;
    }
    return null;
  }

  public T remove(String key) {
    AgingItem item = items.remove(key);
    if (item != null) {
      return item.item;
    }
    return null;
  }

  public int size() {
    return items.size();
  }

  public int gc(long now) {
    int removals = 0;
    Iterator<Map.Entry<String, AgingItem>> it = items.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<String, AgingItem> entry = it.next();
      long age = now - entry.getValue().time;
      if (age > Constants.MILLISECONDS_TO_SIT_IN_GARBAGE_MAP) {
        it.remove();
        removals++;
      }
    }
    return removals;
  }

  private class AgingItem {
    public final T item;
    public final long time;

    public AgingItem(T item, long now) {
      this.item = item;
      this.time = now;
    }
  }
}
