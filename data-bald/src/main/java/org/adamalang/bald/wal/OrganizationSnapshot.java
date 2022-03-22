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
