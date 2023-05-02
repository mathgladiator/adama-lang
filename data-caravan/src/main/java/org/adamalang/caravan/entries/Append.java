/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.caravan.entries;

import io.netty.buffer.ByteBuf;
import org.adamalang.caravan.contracts.WALEntry;

public class Append implements WALEntry<Append> {
  public final long id;
  public final long position;
  public final byte[] bytes;
  public final int seq;
  public final long assetBytes;

  public Append(long id, long position, byte[] bytes, int seq, long assetBytes) {
    this.id = id;
    this.position = position;
    this.bytes = bytes;
    this.seq = seq;
    this.assetBytes = assetBytes;
  }

  public static Append readAfterTypeId(ByteBuf buf) {
    long id = buf.readLongLE();
    long position = buf.readLongLE();
    int size = buf.readIntLE();
    byte[] bytes = new byte[size];
    buf.readBytes(bytes);
    int seq = buf.readIntLE();
    long assetBytes = buf.readLongLE();
    return new Append(id, position, bytes, seq, assetBytes);
  }

  public void write(ByteBuf buf) {
    buf.writeByte(0x42);
    buf.writeLongLE(id);
    buf.writeLongLE(position);
    buf.writeIntLE(bytes.length);
    buf.writeBytes(bytes);
    buf.writeIntLE(seq);
    buf.writeLongLE(assetBytes);
  }
}
