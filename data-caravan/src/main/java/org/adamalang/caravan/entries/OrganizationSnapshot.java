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

  @Override
  public void write(ByteBuf buf) {
    buf.writeByte(0x55);
    heap.snapshot(buf);
    index.snapshot(buf);
  }

  public static void populateAfterTypeId(ByteBuf buf, Heap heap, Index index) {
    heap.load(buf);
    index.load(buf);
  }
}
