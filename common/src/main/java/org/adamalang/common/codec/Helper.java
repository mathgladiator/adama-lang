/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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


  public static int[] readIntArray(ByteBuf buf) {
    int count = buf.readIntLE();
    if (count == 0) {
      return null;
    }
    int[] arr = new int[count - 1];
    for (int k = 0; k < arr.length; k++) {
      arr[k] = buf.readIntLE();
    }
    return arr;
  }

  public static void writeIntArray(ByteBuf buf, int[] nums) {
    if (nums == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(nums.length + 1);
    for (int k = 0; k < nums.length; k++) {
      buf.writeIntLE(nums[k]);
    }
  }
}
