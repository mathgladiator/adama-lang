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
import org.adamalang.runtime.data.Key;

import java.nio.charset.StandardCharsets;

public class DelKey implements WALEntry<DelKey> {
  public final byte[] space;
  public final byte[] key;

  public DelKey(Key key) {
    this.space = key.space.getBytes(StandardCharsets.UTF_8);
    this.key = key.key.getBytes(StandardCharsets.UTF_8);
  }

  private DelKey(byte[] space, byte[] key) {
    this.space = space;
    this.key = key;
  }

  @Override
  public void write(ByteBuf buf) {
    buf.writeByte(0x36);
    buf.writeIntLE(space.length);
    buf.writeBytes(space);
    buf.writeIntLE(key.length);
    buf.writeBytes(key);
  }

  public static DelKey readAfterTypeId(ByteBuf buf) {
    int sizeSpace = buf.readIntLE();
    byte[] bytesSpace = new byte[sizeSpace];
    buf.readBytes(bytesSpace);
    int sizeKey = buf.readIntLE();
    byte[] bytesKey = new byte[sizeKey];
    buf.readBytes(bytesKey);
    return new DelKey(bytesSpace, bytesKey);
  }

  public Key of() {
    return new Key(new String(this.space, StandardCharsets.UTF_8), new String(this.key, StandardCharsets.UTF_8));
  }
}
