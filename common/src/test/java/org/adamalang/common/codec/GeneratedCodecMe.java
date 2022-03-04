/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.common.codec;

import io.netty.buffer.ByteBuf;
import org.adamalang.common.codec.Helper;
import org.adamalang.common.net.ByteStream;
import org.adamalang.common.codec.CodecCodeGenTests.TestClassA;
import org.adamalang.common.codec.CodecCodeGenTests.TestClassB;

public class GeneratedCodecMe {

  public static abstract class StreamX implements ByteStream {
    public abstract void handle(TestClassA payload);

    public abstract void handle(TestClassB payload);

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
          return;
      }
    }
  }

  public static interface HandlerX {
    public void handle(TestClassA payload);
    public void handle(TestClassB payload);
  }

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
        return;
    }
  }

  public static abstract class StreamY implements ByteStream {
    public abstract void handle(TestClassB payload);

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
        case 4242:
          handle(readBody_4242(buf, new TestClassB()));
          return;
      }
    }
  }

  public static interface HandlerY {
    public void handle(TestClassB payload);
  }

  public static void route(ByteBuf buf, HandlerY handler) {
    switch (buf.readIntLE()) {
      case 4242:
        handler.handle(readBody_4242(buf, new TestClassB()));
        return;
    }
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

  public static TestClassA readRegister_TestClassA(ByteBuf buf, TestClassA o) {
    switch (buf.readIntLE()) {
      case 123:
        return readBody_123(buf, o);
      case 42:
        return readBody_42(buf, o);
    }
    return null;
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

  public static TestClassB read_TestClassB(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 4242:
        return readBody_4242(buf, new TestClassB());
    }
    return null;
  }

  public static TestClassB readRegister_TestClassB(ByteBuf buf, TestClassB o) {
    switch (buf.readIntLE()) {
      case 4242:
        return readBody_4242(buf, o);
    }
    return null;
  }

  private static TestClassB readBody_4242(ByteBuf buf, TestClassB o) {
    o.x = buf.readIntLE();
    o.embed = read_TestClassA(buf);
    o.lng = buf.readLongLE();
    return o;
  }

  public static void write(ByteBuf buf, TestClassA o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(123);
    buf.writeIntLE(o.x);
    Helper.writeString(buf, o.str);;
    buf.writeDoubleLE(o.w);
    buf.writeShortLE(o.sssshort);
    buf.writeBoolean(o.bbb);
    Helper.writeStringArray(buf, o.strarr);;
  }

  public static void write(ByteBuf buf, TestClassB o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(4242);
    buf.writeIntLE(o.x);
    write(buf, o.embed);;
    buf.writeLongLE(o.lng);
  }
}