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

public class Delete implements WALEntry<Delete> {
  public final long id;

  public Delete(long id) {
    this.id = id;
  }

  public static Delete readAfterTypeId(ByteBuf buf) {
    return new Delete(buf.readLongLE());
  }

  public void write(ByteBuf buf) {
    buf.writeByte(0x66);
    buf.writeLongLE(id);
  }
}
