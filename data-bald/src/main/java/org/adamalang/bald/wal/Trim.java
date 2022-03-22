package org.adamalang.bald.wal;

import io.netty.buffer.ByteBuf;
import org.adamalang.bald.contracts.WALEntry;
import org.adamalang.bald.organization.Heap;

import java.util.ArrayList;

public class Trim implements WALEntry {
  private final long id;
  private final int count;
  private final ArrayList<Heap.Region> regions;
  private final boolean delete;

  public Trim(long id, int count, ArrayList<Heap.Region> regions, boolean delete) {
    this.id = id;
    this.count = count;
    this.regions = regions;
    this.delete = delete;
  }

  public void write(ByteBuf buf) {
    // Type ID
    buf.writeByte(0x13);
    buf.writeLongLE(id);
    buf.writeIntLE(count);
    buf.writeIntLE(regions.size());
    buf.writeBoolean(delete);
    for (Heap.Region region: regions) {
      buf.writeLongLE(region.position);
      buf.writeIntLE(region.size);
    }
  }

  public static Trim readAfterTypeId(ByteBuf buf) {
    long id = buf.readLongLE();
    int count = buf.readIntLE();
    int size = buf.readIntLE();
    boolean delete = buf.readBoolean();
    ArrayList<Heap.Region> regions = new ArrayList<>();
    for (int k = 0; k < size; k++) {
      long pos = buf.readLongLE();
      int sz = buf.readIntLE();
      regions.add(new Heap.Region(pos, sz));
    }
    return new Trim(id, count, regions, delete);
  }
}
