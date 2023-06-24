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

public class Trim implements WALEntry {
  public final long id;
  public final int maxSize;

  public Trim(long id, int maxSize) {
    this.id = id;
    this.maxSize = maxSize;
  }

  public static Trim readAfterTypeId(ByteBuf buf) {
    long id = buf.readLongLE();
    int maxSize = buf.readIntLE();
    return new Trim(id, maxSize);
  }

  public void write(ByteBuf buf) {
    // Type ID
    buf.writeByte(0x13);
    buf.writeLongLE(id);
    buf.writeIntLE(maxSize);
  }
}
