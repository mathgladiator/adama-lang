/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.caravan.entries;

import io.netty.buffer.ByteBuf;
import org.adamalang.caravan.contracts.WALEntry;
import org.adamalang.caravan.index.Heap;
import org.adamalang.caravan.index.Index;

public class OrganizationSnapshot implements WALEntry {
  private final Heap heap;
  private final Index index;

  public OrganizationSnapshot(Heap heap, Index index) {
    this.heap = heap;
    this.index = index;
  }

  public static void populateAfterTypeId(ByteBuf buf, Heap heap, Index index) {
    heap.load(buf);
    index.load(buf);
  }

  @Override
  public void write(ByteBuf buf) {
    buf.writeByte(0x55);
    heap.snapshot(buf);
    index.snapshot(buf);
  }
}
