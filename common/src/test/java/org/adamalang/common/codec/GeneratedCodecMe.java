package org.adamalang.common.codec;

import io.netty.buffer.ByteBuf;

import org.adamalang.common.codec.Helper;

import org.adamalang.common.codec.CodecCodeGenTests.TestClassA;
import org.adamalang.common.codec.CodecCodeGenTests.TestClassB;

public class GeneratedCodecMe {
  public static interface Handler {
    public void handle(TestClassA payload);
    public void handle(TestClassB payload);
  }

  public static void route(ByteBuf buf, Handler handler) {
    switch (buf.readIntLE()) {
      case 123:
        handler.handle(readBody_123(buf));
        return;
      case 42:
        handler.handle(readBody_42(buf));
        return;
      case 4242:
        handler.handle(readBody_4242(buf));
        return;
    }
  }

  public static TestClassA read_TestClassA(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 123:
        return readBody_123(buf);
      case 42:
        return readBody_42(buf);
    }
    return null;
  }

  private static TestClassA readBody_123(ByteBuf buf) {
    TestClassA o_123 = new TestClassA();
    o_123.x = buf.readIntLE();
    o_123.str = Helper.readString(buf);
    o_123.w = buf.readDoubleLE();
    o_123.sssshort = buf.readShortLE();
    return o_123;
  }

  private static TestClassA readBody_42(ByteBuf buf) {
    TestClassA o_42 = new TestClassA();
    o_42.x = buf.readIntLE();
    o_42.z = buf.readDoubleLE();
    o_42.w = buf.readDoubleLE();
    o_42.sssshort = buf.readShortLE();
    return o_42;
  }

  public static TestClassB read_TestClassB(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 4242:
        return readBody_4242(buf);
    }
    return null;
  }

  private static TestClassB readBody_4242(ByteBuf buf) {
    TestClassB o_4242 = new TestClassB();
    o_4242.x = buf.readIntLE();
    o_4242.embed = read_TestClassA(buf);
    return o_4242;
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
  }

  public static void write(ByteBuf buf, TestClassB o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(4242);
    buf.writeIntLE(o.x);
    write(buf, o.embed);;
  }
}