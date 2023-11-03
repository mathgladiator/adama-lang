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
package org.adamalang.runtime.natives;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/** a simple map */
public class NtMap<TIn, TOut> implements Iterable<NtPair<TIn, TOut>> {
  public final TreeMap<TIn, TOut> storage;

  public NtMap() {
    this.storage = new TreeMap<>();
  }

  public NtMap(final NtMap<TIn, TOut> input) {
    this.storage = new TreeMap<>(input.storage);
  }

  @Override
  public Iterator<NtPair<TIn, TOut>> iterator() {
    Iterator<Map.Entry<TIn, TOut>> iterator = storage.entrySet().iterator();
    return new Iterator<>() {
      @Override
      public boolean hasNext() {
        return iterator.hasNext();
      }

      @Override
      public NtPair<TIn, TOut> next() {
        return pairOf(iterator.next());
      }
    };
  }

  public static <TIn, TOut> NtPair<TIn, TOut> pairOf(Map.Entry<TIn, TOut> entry) {
    return new NtPair<>(entry.getKey(), entry.getValue());
  }

  public NtMaybe<TOut> lookup(final TIn key) {
    final var data = storage.get(key);
    return new NtMaybe<>(data).withAssignChain(update -> {
      if (update == null) {
        storage.remove(key);
      } else {
        storage.put(key, update);
      }
    });
  }

  public Iterable<Map.Entry<TIn, TOut>> entries() {
    return storage.entrySet();
  }

  public TOut put(final TIn key, final TOut value) {
    return storage.put(key, value);
  }

  public void set(final NtMap<TIn, TOut> input) {
    this.storage.clear();
    this.storage.putAll(new TreeMap<>(input.storage));
  }

  public TOut removeDirect(TIn key) {
    return this.storage.remove(key);
  }

  public NtMaybe<TOut> remove(TIn key) {
    return new NtMaybe<>(this.storage.remove(key));
  }

  public TOut get(TIn key) {
    return this.storage.get(key);
  }

  public NtMap<TIn, TOut> insert(final NtMap<TIn, TOut> input) {
    this.storage.putAll(new TreeMap<>(input.storage));
    return this;
  }

  public int size() {
    return storage.size();
  }

  public NtMaybe<NtPair<TIn, TOut>> min() {
    if (storage.size() > 0) {
      return new NtMaybe<>(pairOf(storage.firstEntry()));
    } else {
      return new NtMaybe<>();
    }
  }

  public NtMaybe<NtPair<TIn, TOut>> max() {
    if (storage.size() > 0) {
      return new NtMaybe<>(pairOf(storage.lastEntry()));
    } else {
      return new NtMaybe<>();
    }
  }

  public boolean has(TIn key) {
    return this.storage.containsKey(key);
  }

  public void clear() {
    storage.clear();
  }
}
