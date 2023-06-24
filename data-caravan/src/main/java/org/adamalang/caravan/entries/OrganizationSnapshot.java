/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
