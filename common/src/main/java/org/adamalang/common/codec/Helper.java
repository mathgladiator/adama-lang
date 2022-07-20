/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.codec;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Helper {

  public static <T> T[] readArray(ByteBuf buf, Function<Integer, T[]> maker, Supplier<T> read) {
    int count = buf.readIntLE();
    if (count == 0) {
      return null;
    }
    T[] arr = maker.apply(count - 1);
    for (int k = 0; k < arr.length; k++) {
      arr[k] = read.get();
    }
    return arr;
  }

  public static <T> void writeArray(ByteBuf buf, T[] arr, Consumer<T> write) {
    if (arr == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(arr.length + 1);
    for (int k = 0; k < arr.length; k++) {
      write.accept(arr[k]);
    }
  }

  public static String[] readStringArray(ByteBuf buf) {
    int count = buf.readIntLE();
    if (count == 0) {
      return null;
    }
    String[] arr = new String[count - 1];
    for (int k = 0; k < arr.length; k++) {
      arr[k] = readString(buf);
    }
    return arr;
  }

  public static String readString(ByteBuf buf) {
    int count = buf.readIntLE();
    if (count == 0) {
      return null;
    }
    byte[] bytes = new byte[count - 1];
    buf.readBytes(bytes);
    return new String(bytes, StandardCharsets.UTF_8);
  }

  public static void writeStringArray(ByteBuf buf, String[] strs) {
    if (strs == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(strs.length + 1);
    for (int k = 0; k < strs.length; k++) {
      writeString(buf, strs[k]);
    }
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
