/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.cache;

/** a cache entry has the core item, a measure of the size, and a timestamp */
public class CacheEntry<R> {
  public final R item;
  public long timestamp;

  public CacheEntry(R item, long timestamp) {
    this.item = item;
    this.timestamp = timestamp;
  }
}
