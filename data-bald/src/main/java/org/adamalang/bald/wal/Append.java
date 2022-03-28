/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
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
