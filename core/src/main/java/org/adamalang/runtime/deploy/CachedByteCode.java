/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
package org.adamalang.runtime.deploy;

import org.adamalang.ErrorCodes;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionSupplier;
import org.adamalang.common.cache.Measurable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/** a cached artifact of compiled java byte code */
public class CachedByteCode implements Measurable {
  public final String spaceName;
  public final String className;
  public final String reflection;
  public final Map<String, byte[]> classBytes;
  private final long measure;

  public CachedByteCode(String spaceName, String className, String reflection, Map<String, byte[]> classBytes) {
    this.spaceName = spaceName;
    this.className = className;
    this.reflection = reflection;
    this.classBytes = classBytes;
    long _measure = 0;
    _measure += spaceName.length() + 32;
    _measure += className.length() + 32;
    _measure += reflection.length() + 32;
    for (Map.Entry<String, byte[]> entry : classBytes.entrySet()) {
      _measure += entry.getKey().length() + 32;
      _measure += entry.getValue().length;
    }
    this.measure = _measure;
  }

  /** pack up the byte code */
  public byte[] pack() throws ErrorCodeException {
    try {
      ByteArrayOutputStream memory = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(memory);
      output.writeInt(0x42);
      {
        byte[] w = spaceName.getBytes(StandardCharsets.UTF_8);
        output.writeInt(w.length);
        output.write(w);
      }
      {
        byte[] w = className.getBytes(StandardCharsets.UTF_8);
        output.writeInt(w.length);
        output.write(w);
      }
      {
        byte[] w = reflection.getBytes(StandardCharsets.UTF_8);
        output.writeInt(w.length);
        output.write(w);
      }
      output.writeInt(classBytes.size());
      for (Map.Entry<String, byte[]> clazz : classBytes.entrySet()) {
        byte[] w = clazz.getKey().getBytes(StandardCharsets.UTF_8);
        output.writeInt(w.length);
        output.write(w);
        output.writeInt(clazz.getValue().length);
        output.write(clazz.getValue());
      }
      output.flush();
      return memory.toByteArray();
    } catch (Exception ex) {
      throw new ErrorCodeException(ErrorCodes.CACHED_BYTE_CODE_FAILED_PACK, ex);
    }
  }

  /** unpack byte code */
  public static CachedByteCode unpack(byte[] packed) throws ErrorCodeException {
    try {
      DataInputStream input = new DataInputStream(new ByteArrayInputStream(packed));
      if (input.readInt() != 0x42) {
        return null;
      }
      ExceptionSupplier<String> str = () -> {
        int w = input.readInt();
        byte[] b = new byte[w];
        input.readFully(b);
        return new String(b, StandardCharsets.UTF_8);
      };
      ExceptionSupplier<byte[]> bytes = () -> {
        int w = input.readInt();
        byte[] b = new byte[w];
        input.readFully(b);
        return b;
      };
      String spaceName = str.get();
      String className = str.get();
      String reflection = str.get();
      HashMap<String, byte[]> classBytes = new HashMap<>();
      int n = input.readInt();
      for (int k = 0; k < n; k ++) {
        String key = str.get();
        byte[] value = bytes.get();
        classBytes.put(key, value);
      }
      return new CachedByteCode(spaceName, className, reflection, classBytes);
    } catch (Exception ex) {
      throw new ErrorCodeException(ErrorCodes.CACHED_BYTE_CODE_FAILED_UNPACK, ex);
    }
  }

  @Override
  public long measure() {
    return measure;
  }
}
