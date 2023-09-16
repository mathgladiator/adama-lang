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
package org.adamalang.common.codec;

import io.netty.buffer.ByteBuf;
import org.adamalang.common.codec.CodecCodeGenTests.TestClassA;
import org.adamalang.common.codec.CodecCodeGenTests.TestClassB;
import org.adamalang.common.net.ByteStream;

public class GeneratedCodecMe {

  public static void route(ByteBuf buf, HandlerX handler) {
    switch (buf.readIntLE()) {
      case 123:
        handler.handle(readBody_123(buf, new TestClassA()));
        return;
      case 42:
        handler.handle(readBody_42(buf, new TestClassA()));
        return;
      case 4242:
        handler.handle(readBody_4242(buf, new TestClassB()));
    }
  }

  private static TestClassA readBody_123(ByteBuf buf, TestClassA o) {
    o.x = buf.readIntLE();
    o.str = Helper.readString(buf);
    o.w = buf.readDoubleLE();
    o.sssshort = buf.readShortLE();
    o.bbb = buf.readBoolean();
    o.strarr = Helper.readStringArray(buf);
    return o;
  }

  private static TestClassA readBody_42(ByteBuf buf, TestClassA o) {
    o.x = buf.readIntLE();
    o.z = buf.readDoubleLE();
    o.w = buf.readDoubleLE();
    o.sssshort = buf.readShortLE();
    o.bbb = buf.readBoolean();
    o.strarr = Helper.readStringArray(buf);
    return o;
  }

  private static TestClassB readBody_4242(ByteBuf buf, TestClassB o) {
    o.x = buf.readIntLE();
    o.embed = read_TestClassA(buf);
    o.lng = buf.readLongLE();
    o.arr = Helper.readArray(buf, (n) -> new TestClassA[n], () -> read_TestClassA(buf));
    return o;
  }

  public static TestClassA read_TestClassA(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 123:
        return readBody_123(buf, new TestClassA());
      case 42:
        return readBody_42(buf, new TestClassA());
    }
    return null;
  }

  public static void route(ByteBuf buf, HandlerY handler) {
    if (buf.readIntLE() == 4242) {
      handler.handle(readBody_4242(buf, new TestClassB()));
    }
  }

  public static TestClassA readRegister_TestClassA(ByteBuf buf, TestClassA o) {
    switch (buf.readIntLE()) {
      case 123:
        return readBody_123(buf, o);
      case 42:
        return readBody_42(buf, o);
    }
    return null;
  }

  public static TestClassB read_TestClassB(ByteBuf buf) {
    if (buf.readIntLE() == 4242) {
      return readBody_4242(buf, new TestClassB());
    }
    return null;
  }

  public static void write(ByteBuf buf, TestClassB o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(4242);
    buf.writeIntLE(o.x);
    write(buf, o.embed);
    buf.writeLongLE(o.lng);
    Helper.writeArray(buf, o.arr, (item) -> write(buf, item));
  }

  public static void write(ByteBuf buf, TestClassA o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(123);
    buf.writeIntLE(o.x);
    Helper.writeString(buf, o.str);
    buf.writeDoubleLE(o.w);
    buf.writeShortLE(o.sssshort);
    buf.writeBoolean(o.bbb);
    Helper.writeStringArray(buf, o.strarr);
  }

  public interface HandlerX {
    void handle(TestClassA payload);

    void handle(TestClassB payload);
  }


  public interface HandlerY {
    void handle(TestClassB payload);
  }

  public static abstract class StreamX implements ByteStream {
    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 123:
          handle(readBody_123(buf, new TestClassA()));
          return;
        case 42:
          handle(readBody_42(buf, new TestClassA()));
          return;
        case 4242:
          handle(readBody_4242(buf, new TestClassB()));
      }
    }

    public abstract void handle(TestClassA payload);

    public abstract void handle(TestClassB payload);
  }

  public static abstract class StreamY implements ByteStream {
    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void next(ByteBuf buf) {
      if (buf.readIntLE() == 4242) {
        handle(readBody_4242(buf, new TestClassB()));
      }
    }

    public abstract void handle(TestClassB payload);
  }
}
