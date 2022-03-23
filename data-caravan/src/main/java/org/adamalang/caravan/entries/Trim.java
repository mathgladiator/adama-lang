package org.adamalang.caravan.entries;

import io.netty.buffer.ByteBuf;
import org.adamalang.caravan.contracts.WALEntry;
import org.adamalang.caravan.index.Region;

import java.util.ArrayList;

public class Trim implements WALEntry {
  public final long id;
  public final ArrayList<Region> regions;

  public Trim(long id, ArrayList<Region> regions) {
    this.id = id;
    this.regions = regions;
  }

  public void write(ByteBuf buf) {
    // Type ID
    buf.writeByte(0x13);
    buf.writeLongLE(id);
    buf.writeIntLE(regions.size());
    for (Region region: regions) {
      buf.writeLongLE(region.position);
      buf.writeIntLE(region.size);
    }
  }

  public static Trim readAfterTypeId(ByteBuf buf) {
    long id = buf.readLongLE();
    int size = buf.readIntLE();
    ArrayList<Region> regions = new ArrayList<>();
    for (int k = 0; k < size; k++) {
      long pos = buf.readLongLE();
      int sz = buf.readIntLE();
      regions.add(new Region(pos, sz));
    }
    return new Trim(id, regions);
  }
}
