package org.adamalang.caravan.entries;

import io.netty.buffer.ByteBuf;
import org.adamalang.caravan.contracts.WALEntry;

public class Delete implements WALEntry<Delete> {
  public final long id;

  public Delete(long id) {
    this.id = id;
  }

  public void write(ByteBuf buf) {
    buf.writeByte(0x66);
    buf.writeLongLE(id);
  }

  public static Delete readAfterTypeId(ByteBuf buf) {
    return new Delete(buf.readLongLE());
  }
}
