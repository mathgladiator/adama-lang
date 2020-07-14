/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.natives;

import java.util.LinkedHashMap;

/** a simple map */
public class NtMap<TIn, TOut> {
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

  public void set(final NtMap<TIn, TOut> input) {
    this.storage.clear();
    this.storage.putAll(new LinkedHashMap<>(input.storage));
  }

  public int size() {
    return storage.size();
  }
}
