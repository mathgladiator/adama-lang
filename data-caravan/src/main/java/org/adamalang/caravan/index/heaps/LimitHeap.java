/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.caravan.index.heaps;

import io.netty.buffer.ByteBuf;
import org.adamalang.caravan.index.Heap;
import org.adamalang.caravan.index.Region;

/** a passthrough heap which ensures everything allocated from this heap is under a specific size */
public class LimitHeap implements Heap {

  private final Heap parent;
  private final int sizeLimit;

  public LimitHeap(Heap parent, int sizeLimit) {
    this.parent = parent;
    this.sizeLimit = sizeLimit;
  }

  @Override
  public long available() {
    return parent.available();
  }

  @Override
  public long max() {
    return parent.max();
  }

  @Override
  public Region ask(int size) {
    if (size > sizeLimit) {
      return null;
    }
    return parent.ask(size);
  }

  @Override
  public void free(Region region) {
    parent.free(region);
  }

  @Override
  public void snapshot(ByteBuf buf) {
    parent.snapshot(buf);
  }

  @Override
  public void load(ByteBuf buf) {
    parent.load(buf);
  }

  @Override
  public String toString() {
    return parent.toString();
  }
}
