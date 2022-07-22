/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.caravan.index;

import io.netty.buffer.ByteBuf;

/** a very simple doubly-linked heap */
public interface Heap {
  /** how many bytes are available to allocate */
  long available();

  /** how many total bytes can this heap allocate */
  long max();

  /** ask the heap for a region of memory of the given size */
  Region ask(int size);

  /** free the given region */
  void free(Region region);

  /** take a snapshot of heap to the given byte buffer */
  void snapshot(ByteBuf buf);

  /** load the heap state from the given byte buffer */
  void load(ByteBuf buf);

  /** report on the heap */
  void report(Report report);
}
