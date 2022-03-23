package org.adamalang.caravan.entries;

import io.netty.buffer.ByteBuf;
import org.adamalang.caravan.contracts.WALEntry;

public class Append implements WALEntry<Append> {
  public final long id;
  public final long position;
  public final byte[] bytes;

  public Append(long id, long position, byte[] bytes) {
    this.id = id;
    this.position = position;
    this.bytes = bytes;
  }

  public void write(ByteBuf buf) {
    buf.writeByte(0x42);
    buf.writeLongLE(id);
    buf.writeLongLE(position);
    buf.writeIntLE(bytes.length);
    buf.writeBytes(bytes);
  }

  public static Append readAfterTypeId(ByteBuf buf) {
    long id = buf.readLongLE();
    long position = buf.readLongLE();
    int size = buf.readIntLE();
    byte[] bytes = new byte[size];
    buf.readBytes(bytes);
    return new Append(id, position, bytes);
  }
}
