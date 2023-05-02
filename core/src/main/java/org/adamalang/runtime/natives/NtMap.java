/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
}
