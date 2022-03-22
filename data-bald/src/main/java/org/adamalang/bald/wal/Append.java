package org.adamalang.bald.wal;

import io.netty.buffer.ByteBuf;
import org.adamalang.bald.contracts.WALEntry;

public class Append implements WALEntry {
  public final long id;
  public final long position;
  public final int size;
  public final byte[] bytes;

  public Append(long id, long position, byte[] bytes) {
    this.id = id;
    this.position = position;
    this.size = bytes.length;
    this.bytes = bytes;
  }

  public void write(ByteBuf buf) {
    buf.writeByte(0x42);
    buf.writeLongLE(id);
    buf.writeLongLE(position);
    buf.writeIntLE(size);
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
