package org.adamalang.common.codec;

import org.junit.Assert;
import org.junit.Test;

public class CodecCodeGenTests {
  @TypeId(123)
  @PriorTypeId(42)
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
  }

  @TypeId(4242)
  public static class TestClassB {

    @FieldOrder(1)
    public int x;

    @FieldOrder(2)
    public TestClassA embed;
  }


  @Test
  public void flow() {
    String java = CodecCodeGen.assembleCodec("org.adamalang.common.codec", "GeneratedCodecMe", TestClassA.class, TestClassB.class);
    System.err.println(java);
  }

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
