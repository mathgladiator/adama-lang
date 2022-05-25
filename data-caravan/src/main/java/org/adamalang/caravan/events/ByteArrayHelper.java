package org.adamalang.caravan.events;

import io.netty.buffer.ByteBuf;

public class ByteArrayHelper {
  public static byte[] convert(ByteBuf buf) {
    byte[] memory = new byte[buf.writerIndex()];
    while (buf.isReadable()) {
      buf.readBytes(memory, buf.readerIndex(), memory.length - buf.readerIndex());
    }
    return memory;
  }
}
