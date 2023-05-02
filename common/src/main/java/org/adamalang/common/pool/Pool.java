/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.pool;

import java.util.Iterator;
import java.util.LinkedList;

/** a pool is a simple list wrapper where we count the inflight items; consumers of the pool are expected to return the item back to the pool */
public class Pool<S> {
  private final LinkedList<S> queue;
  private int size;

  public Pool() {
    this.queue = new LinkedList<>();
    this.size = 0;
  }

  /** increase the size of the pool */
  public void bumpUp() {
    size++;
  }

  /** decrease the size of the pool */
  public void bumpDown() {
    size--;
  }

  /** @return the size of the pool */
  public int size() {
    return size;
  }

  /** add an item back into the pool as available */
  public void add(S item) {
    queue.add(item);
  }

  public Iterator<S> iterator() {
    return queue.iterator();
  }

  /** remove the next item from the queue if available */
  public S next() {
    if (queue.size() > 0) {
      return queue.removeFirst();
    }
    return null;
  }
}
