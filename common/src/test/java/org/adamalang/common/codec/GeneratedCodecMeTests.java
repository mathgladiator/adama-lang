package org.adamalang.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Assert;
import org.junit.Test;

public class GeneratedCodecMeTests {
  @Test
  public void flowA() {
    ByteBuf buf = Unpooled.buffer();
    {
      CodecCodeGenTests.TestClassA a = new CodecCodeGenTests.TestClassA();
      a.sssshort = 10000;
      a.str = "Howdy";
      a.x = 14000000;
      a.w = 1;
      a.z = 3.14;
      GeneratedCodecMe.write(buf, a);
    }
    {
      CodecCodeGenTests.TestClassA a = GeneratedCodecMe.read_TestClassA(buf);
      Assert.assertEquals(10000, a.sssshort);
      Assert.assertEquals("Howdy", a.str);
      Assert.assertEquals(14000000, a.x);
      Assert.assertEquals(1, a.w, 0.00001);
      // Z is dropped due to it being an old field
      Assert.assertEquals(0.0, a.z, 0.00001);
    }
  }

  @Test
  public void flowB() {
    ByteBuf buf = Unpooled.buffer();
    {
      CodecCodeGenTests.TestClassB b = new CodecCodeGenTests.TestClassB();
      b.x = 40;
      b.embed = new CodecCodeGenTests.TestClassA();
      b.embed.sssshort = 10000;
      b.embed.str = "Howdy";
      b.embed.x = 14000000;
      b.embed.w = 1;
      b.embed.z = 3.14;
      GeneratedCodecMe.write(buf, b);
    }
    {
      CodecCodeGenTests.TestClassB b = GeneratedCodecMe.read_TestClassB(buf);
      Assert.assertEquals(40, b.x);
      CodecCodeGenTests.TestClassA a = b.embed;
      Assert.assertEquals(10000, a.sssshort);
      Assert.assertEquals("Howdy", a.str);
      Assert.assertEquals(14000000, a.x);
      Assert.assertEquals(1, a.w, 0.00001);
      // Z is dropped due to it being an old field
      Assert.assertEquals(0.0, a.z, 0.00001);
    }
  }

  @Test
  public void null_str() {
    ByteBuf buf = Unpooled.buffer();
    {
      CodecCodeGenTests.TestClassB b = new CodecCodeGenTests.TestClassB();
      b.x = 40;
      b.embed = new CodecCodeGenTests.TestClassA();
      b.embed.sssshort = 10000;
      b.embed.str = null;
      b.embed.x = 14000000;
      b.embed.w = 1;
      b.embed.z = 3.14;
      GeneratedCodecMe.write(buf, b);
    }
    {
      CodecCodeGenTests.TestClassB b = GeneratedCodecMe.read_TestClassB(buf);
      Assert.assertEquals(40, b.x);
      CodecCodeGenTests.TestClassA a = b.embed;
      Assert.assertEquals(10000, a.sssshort);
      Assert.assertNull(a.str);
      Assert.assertEquals(14000000, a.x);
      Assert.assertEquals(1, a.w, 0.00001);
      // Z is dropped due to it being an old field
      Assert.assertEquals(0.0, a.z, 0.00001);
    }
  }

  @Test
  public void null_object() {
    ByteBuf buf = Unpooled.buffer();
    {
      CodecCodeGenTests.TestClassB b = new CodecCodeGenTests.TestClassB();
      b.x = 40;
      b.embed = null;
      GeneratedCodecMe.write(buf, b);
    }
    {
      CodecCodeGenTests.TestClassB b = GeneratedCodecMe.read_TestClassB(buf);
      Assert.assertEquals(40, b.x);
      Assert.assertNull(b.embed);
    }
  }
}
