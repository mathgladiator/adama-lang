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

import org.junit.Assert;
import org.junit.Test;

public class CodecCodeGenTests {
  @TypeId(123)
  @PriorTypeId(42)
  @Flow("X")
  @MakeReadRegister
  public static class TestClassA {

    @FieldOrder(1)
    public int x;

    @FieldOrder(2)
    @FieldNew
    public String str;

    @FieldOrder(3)
    @FieldOld
    public double z;

    @FieldOrder(4)
    public double w;

    @FieldOrder(5)
    public short sssshort;

    @FieldOrder(6)
    public boolean bbb;

    @FieldOrder(7)
    public String[] strarr;
  }

  @TypeId(4242)
  @Flow("X|Y")
  public static class TestClassB {

    @FieldOrder(1)
    public int x;

    @FieldOrder(2)
    public TestClassA embed;

    @FieldOrder(3)
    public long lng;

    @FieldOrder(4)
    public TestClassA[] arr;
  }


  @Test
  public void flow() {
    String java = CodecCodeGen.assembleCodec("org.adamalang.common.codec", "GeneratedCodecMe", TestClassA.class, TestClassB.class);
    System.err.println(java);
  }

  @Test
  public void dupe() {
    try {
      CodecCodeGen.assembleCodec("org.adamalang.common.codec", "GeneratedCodecMe", TestClassA.class, TestClassA.class);
      Assert.fail();
    } catch (RuntimeException re) {
      Assert.assertEquals("Duplicate type:123", re.getMessage());
    }
  }

  public static class NoFlow {
  }

  @Test
  public void problem_no_flow() {
    try {
      CodecCodeGen.assembleCodec("C", "C", NoFlow.class);
      Assert.fail();
    } catch (RuntimeException re) {
      Assert.assertEquals("NoFlow has no @Flow", re.getMessage());
    }
  }

  @Flow("X")
  public static class NoType {
  }

  @Test
  public void problem_no_type() {
    try {
      CodecCodeGen.assembleCodec("C", "C", NoType.class);
      Assert.fail();
    } catch (RuntimeException re) {
      Assert.assertEquals("class org.adamalang.common.codec.CodecCodeGenTests$NoType has no @TypeId", re.getMessage());
    }
  }

  @TypeId(42)
  @Flow("X")
  public static class NoOrder {
    public double w;
  }

  @Test
  public void problem_no_order() {
    try {
      CodecCodeGen.assembleCodec("C", "C", NoOrder.class);
      Assert.fail();
    } catch (RuntimeException re) {
      Assert.assertEquals("NoOrder has field 'w' which has no order", re.getMessage());
    }
  }


  @TypeId(42)
  @Flow("X")
  public static class DupeOrder {
    @FieldOrder(1)
    public double w;

    @FieldOrder(1)
    public double z;
  }

  @Test
  public void problem_dupe_order() {
    try {
      CodecCodeGen.assembleCodec("C", "C", DupeOrder.class);
      Assert.fail();
    } catch (RuntimeException re) {
      Assert.assertEquals("DupeOrder has two or more fields with order '1'", re.getMessage());
    }
  }

  @TypeId(42)
  @Flow("X")
  public static class BadType {
    @FieldOrder(1)
    public CodecCodeGenTests badType;
  }

  @Test
  public void problem_bad_type() {
    try {
      CodecCodeGen.write(BadType.class.getFields()[0], "");
      Assert.fail();
    } catch (RuntimeException re) {
      Assert.assertEquals("badType has a type we don't know about.. yet", re.getMessage());
    }
    try {
      CodecCodeGen.readerOf(BadType.class.getFields()[0]);
      Assert.fail();
    } catch (RuntimeException re) {
      Assert.assertEquals("badType has a type we don't know about.. yet", re.getMessage());
    }
  }
}
