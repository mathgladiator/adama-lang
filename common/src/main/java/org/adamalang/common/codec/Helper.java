package org.adamalang.common.codec;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public class Helper {

  public static String readString(ByteBuf buf) {
    int count = buf.readIntLE();
    if (count == 0) {
       return null;
    }
    byte[] bytes = new byte[count - 1];
    buf.readBytes(bytes);
    return new String(bytes, StandardCharsets.UTF_8);
  }

  public static void writeString(ByteBuf buf, String str) {
    if (str == null) {
      buf.writeIntLE(0);
      return;
    }
    byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
    buf.writeIntLE(bytes.length + 1);
    buf.writeBytes(bytes);
  }
}
