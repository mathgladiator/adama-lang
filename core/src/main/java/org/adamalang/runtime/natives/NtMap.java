/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.natives;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/** a simple map */
public class NtMap<TIn, TOut> implements Iterable<Map.Entry<TIn, TOut>> {
  public final LinkedHashMap<TIn, TOut> storage;

  public NtMap() {
    this.storage = new LinkedHashMap<>();
  }

  public NtMap(final NtMap<TIn, TOut> input) {
    this.storage = new LinkedHashMap<>(input.storage);
  }

  public NtMap<TIn, TOut> insert(final NtMap<TIn, TOut> input) {
    this.storage.putAll(new LinkedHashMap<>(input.storage));
    return this;
  }

  @Override
  public Iterator<Map.Entry<TIn, TOut>> iterator() {
    return storage.entrySet().iterator();
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
    this.storage.putAll(new LinkedHashMap<>(input.storage));
  }

  public int size() {
    return storage.size();
  }
}
