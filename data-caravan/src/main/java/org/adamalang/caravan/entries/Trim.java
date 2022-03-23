package org.adamalang.caravan.entries;

import io.netty.buffer.ByteBuf;
import org.adamalang.caravan.contracts.WALEntry;
import org.adamalang.caravan.index.Region;

import java.util.ArrayList;

public class Trim implements WALEntry {
  public final long id;
  public final int count;
  public Trim(long id, int count) {
    this.id = id;
    this.count = count;
  }

  public void write(ByteBuf buf) {
    // Type ID
    buf.writeByte(0x13);
    buf.writeLongLE(id);
    buf.writeIntLE(count);
  }

  public static Trim readAfterTypeId(ByteBuf buf) {
    long id = buf.readLongLE();
    int count = buf.readIntLE();
    return new Trim(id, count);
  }
}
