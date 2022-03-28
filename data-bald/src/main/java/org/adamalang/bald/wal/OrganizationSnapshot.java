/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.bald.wal;

import io.netty.buffer.ByteBuf;
import org.adamalang.bald.contracts.WALEntry;
import org.adamalang.bald.organization.Heap;
import org.adamalang.bald.organization.Index;

public class OrganizationSnapshot implements WALEntry {
  private final Heap heap;
  private final Index index;

  public OrganizationSnapshot(Heap heap, Index index) {
    this.heap = heap;
    this.index = index;
  }

  @Override
  public void write(ByteBuf buf) {
    buf.writeByte(0x99);
    heap.snapshot(buf);
    index.snapshot(buf);
  }
}
