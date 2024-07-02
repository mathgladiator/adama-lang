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

import org.junit.Assert;
import org.junit.Test;

public class CodecCodeGenTests {
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

  @Test
  public void problem_no_flow() {
    try {
      CodecCodeGen.assembleCodec("C", "C", NoFlow.class);
      Assert.fail();
    } catch (RuntimeException re) {
      Assert.assertEquals("NoFlow has no @Flow", re.getMessage());
    }
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

  @Test
  public void problem_no_order() {
    try {
      CodecCodeGen.assembleCodec("C", "C", NoOrder.class);
      Assert.fail();
    } catch (RuntimeException re) {
      Assert.assertEquals("NoOrder has field 'w' which has no order", re.getMessage());
    }
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

    @FieldOrder(8)
    public int[] intarr;
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

  public static class NoFlow {
  }

  @Flow("X")
  public static class NoType {
  }

  @TypeId(42)
  @Flow("X")
  public static class NoOrder {
    public double w;
  }

  @TypeId(42)
  @Flow("X")
  public static class DupeOrder {
    @FieldOrder(1)
    public double w;

    @FieldOrder(1)
    public double z;
  }

  @TypeId(42)
  @Flow("X")
  public static class BadType {
    @FieldOrder(1)
    public CodecCodeGenTests badType;
  }
}
