/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
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
      a.bbb = true;
      a.strarr = new String[] { "A", "B", "C" };
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
      Assert.assertTrue(a.bbb);
      Assert.assertEquals(3, a.strarr.length);
      Assert.assertEquals("A", a.strarr[0]);
      Assert.assertEquals("B", a.strarr[1]);
      Assert.assertEquals("C", a.strarr[2]);
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
      b.embed.bbb = false;
      b.embed.strarr = null;
      b.arr = new CodecCodeGenTests.TestClassA[] { b.embed };
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
      Assert.assertFalse(a.bbb);
      Assert.assertNull(a.strarr);
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
      b.lng = -124;
      GeneratedCodecMe.write(buf, b);
    }
    {
      CodecCodeGenTests.TestClassB b = GeneratedCodecMe.read_TestClassB(buf);
      Assert.assertEquals(40, b.x);
      Assert.assertNull(b.embed);
      Assert.assertEquals(-124, b.lng);
    }
  }
}
