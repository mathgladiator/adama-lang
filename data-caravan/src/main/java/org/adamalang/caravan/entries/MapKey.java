package org.adamalang.caravan.entries;

import io.netty.buffer.ByteBuf;
import org.adamalang.caravan.contracts.WALEntry;
import org.adamalang.runtime.data.Key;

import java.nio.charset.StandardCharsets;

public class MapKey implements WALEntry<MapKey>  {
  public final byte[] space;
  public final byte[] key;
  public final int id;

  public MapKey(Key key, int id) {
    this.space = key.space.getBytes(StandardCharsets.UTF_8);
    this.key = key.key.getBytes(StandardCharsets.UTF_8);
    this.id = id;
  }

  private MapKey(byte[] space, byte[] key, int id) {
    this.space = space;
    this.key = key;
    this.id = id;
  }

  @Override
  public void write(ByteBuf buf) {
    buf.writeByte(0x30);
    buf.writeIntLE(space.length);
    buf.writeBytes(space);
    buf.writeIntLE(key.length);
    buf.writeBytes(key);
    buf.writeIntLE(id);
  }

  public static MapKey readAfterTypeId(ByteBuf buf) {
    int sizeSpace = buf.readIntLE();
    byte[] bytesSpace = new byte[sizeSpace];
    buf.readBytes(bytesSpace);
    int sizeKey = buf.readIntLE();
    byte[] bytesKey = new byte[sizeKey];
    buf.readBytes(bytesKey);
    int id = buf.readIntLE();
    return new MapKey(bytesSpace, bytesKey, id);
  }

  public Key of() {
    return new Key(new String(this.space, StandardCharsets.UTF_8), new String(this.key, StandardCharsets.UTF_8));
  }
}
