/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.mocks.MockRxChild;
import org.adamalang.runtime.mocks.MockRxParent;
import org.junit.Assert;
import org.junit.Test;

public class RxMapTests {
  @Test
  public void memory() {
    final var m = map();
    Assert.assertEquals(168, m.__memory());
    m.getOrCreate(42).set(52);
    Assert.assertEquals(236, m.__memory());
    m.getOrCreate(123).set(52);
    Assert.assertEquals(304, m.__memory());
  }

  @Test
  public void memoryStr() {
    final var m = new RxMap<String, RxInt32>(
        new MockRxParent(),
        new RxMap.StringCodec<RxInt32>() {
          @Override
          public RxInt32 make(RxParent maker) {
            return new RxInt32(maker, 40);
          }
        });
    Assert.assertEquals(168, m.__memory());
    m.getOrCreate("42").set(52);
    Assert.assertEquals(240, m.__memory());
    m.getOrCreate("50").set(52);
    Assert.assertEquals(312, m.__memory());
  }

  private RxMap<Integer, RxInt32> map() {
    return map(new MockRxParent());
  }

  private RxMap<Integer, RxInt32> map(RxParent parent) {
    return new RxMap<Integer, RxInt32>(
        parent,
        new RxMap.IntegerCodec<RxInt32>() {
          @Override
          public RxInt32 make(RxParent maker) {
            return new RxInt32(maker, 0);
          }
        });
  }

  @Test
  public void dump_empty() {
    final var m = map();
    JsonStreamWriter writer = new JsonStreamWriter();
    m.__dump(writer);
    Assert.assertEquals("{}", writer.toString());
  }

  @Test
  public void alive_with_parent() {
    MockRxParent parent = new MockRxParent();
    final var m = map(parent);
    Assert.assertTrue(m.__isAlive());
    parent.alive = false;
    Assert.assertFalse(m.__isAlive());
  }

  @Test
  public void alive_without_parent() {
    final var m = map(null);
    Assert.assertTrue(m.__isAlive());
  }

  @Test
  public void killable_proxy() {
    final var m = new RxMap<Integer, RxMap<Integer, RxInt32>>(new MockRxParent(), new RxMap.IntegerCodec<RxMap<Integer, RxInt32>>() {
          @Override
          public RxMap<Integer, RxInt32> make(RxParent maker) {
            return map(maker);
          }
        });
    m.getOrCreate(42).getOrCreate(100).set(10000);
    {
      JsonStreamWriter redo = new JsonStreamWriter();
      JsonStreamWriter undo = new JsonStreamWriter();
      m.__commit("x", redo, undo);
      Assert.assertEquals("\"x\":{\"42\":{\"100\":10000}}", redo.toString());
    }
    m.__kill();
    m.remove(42);
    {
      JsonStreamWriter redo = new JsonStreamWriter();
      JsonStreamWriter undo = new JsonStreamWriter();
      m.__commit("x", redo, undo);
      Assert.assertEquals("\"x\":{\"42\":null}", redo.toString());
    }
  }

  @Test
  public void dump_singular() {
    final var m = map();
    m.getOrCreate(42).set(52);
    JsonStreamWriter writer = new JsonStreamWriter();
    m.__dump(writer);
    Assert.assertEquals("{\"42\":52}", writer.toString());
  }

  @Test
  public void dump_after_revert() {
    final var m = map();
    m.getOrCreate(42).set(52);
    m.__revert();
    JsonStreamWriter writer = new JsonStreamWriter();
    m.__dump(writer);
    Assert.assertEquals("{}", writer.toString());
  }

  @Test
  public void dump_after_create_then_remove() {
    final var m = map();
    m.getOrCreate(42).set(52);
    m.remove(42);
    JsonStreamWriter writer = new JsonStreamWriter();
    m.__dump(writer);
    Assert.assertEquals("{}", writer.toString());
  }

  @Test
  public void commit_seq() {
    final var m = map();
    m.getOrCreate(42).set(52);
    {
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      m.__commit("map", forward, reverse);
      Assert.assertEquals("\"map\":{\"42\":52}", forward.toString());
      Assert.assertEquals("\"map\":{\"42\":null}", reverse.toString());
    }
    m.getOrCreate(50).set(100);
    m.remove(42);
    {
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      m.__commit("map", forward, reverse);
      Assert.assertEquals("\"map\":{\"42\":null,\"50\":100}", forward.toString());
      Assert.assertEquals("\"map\":{\"42\":52,\"50\":null}", reverse.toString());
    }
    m.getOrCreate(50).set(17);
    {
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      m.__commit("map", forward, reverse);
      Assert.assertEquals("\"map\":{\"50\":17}", forward.toString());
      Assert.assertEquals("\"map\":{\"50\":100}", reverse.toString());
    }
  }

  @Test
  public void revert_seq() {
    final var m = map();
    JsonStreamReader reader = new JsonStreamReader("{\"42\":123,\"50\":100,\"100\":null}");
    m.__insert(reader);
    { // revert insertion
      Assert.assertEquals(2, m.size());
      m.getOrCreate(1000).set(24);
      Assert.assertEquals(3, m.size());
      Assert.assertTrue(m.lookup(1000).has());
      m.__revert();
      Assert.assertFalse(m.lookup(1000).has());
    }
    { // revert change
      m.getOrCreate(42).set(24);
      Assert.assertEquals(24, (int) m.lookup(42).get().get());
      m.__revert();
      Assert.assertEquals(123, (int) m.lookup(42).get().get());
    }
    { // revert delete
      Assert.assertEquals(2, m.size());
      m.remove(42);
      Assert.assertEquals(1, m.size());
      Assert.assertFalse(m.lookup(42).has());
      m.__revert();
      Assert.assertTrue(m.lookup(42).has());
      Assert.assertEquals(2, m.size());
    }
  }

  @Test
  public void seq() {
    final var m = map();
    MockRxChild child = new MockRxChild();
    m.__subscribe(child);
    m.getOrCreate(42).set(52);
    {
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      m.__commit("map", forward, reverse);
      Assert.assertEquals("\"map\":{\"42\":52}", forward.toString());
      Assert.assertEquals("\"map\":{\"42\":null}", reverse.toString());
    }
    child.assertInvalidateCount(3);
  }

  @Test
  public void insert() {
    final var m = map();
    m.getOrCreate(100).set(50);
    JsonStreamReader reader = new JsonStreamReader("{\"42\":123,\"50\":100,\"100\":null}");
    m.__insert(reader);
    {
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      m.__commit("map", forward, reverse);
      Assert.assertEquals("\"map\":{}", forward.toString());
      Assert.assertEquals("\"map\":{}", reverse.toString());
    }
    JsonStreamWriter dump = new JsonStreamWriter();
    m.__dump(dump);
    Assert.assertEquals("{\"42\":123,\"50\":100}", dump.toString());
    Assert.assertTrue(m.lookup(42).has());
    Assert.assertFalse(m.lookup(1000).has());
  }

  @Test
  public void patch() {
    final var m = map();
    m.getOrCreate(100).set(50);
    JsonStreamReader reader = new JsonStreamReader("{\"42\":123,\"50\":100,\"100\":null}");
    m.__patch(reader);
    {
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      m.__commit("map", forward, reverse);
      Assert.assertEquals("\"map\":{\"42\":123,\"50\":100}", forward.toString());
      Assert.assertEquals("\"map\":{\"42\":null,\"50\":null}", reverse.toString());
    }
    JsonStreamWriter dump = new JsonStreamWriter();
    m.__dump(dump);
    Assert.assertEquals("{\"42\":123,\"50\":100}", dump.toString());
    Assert.assertTrue(m.lookup(42).has());
    Assert.assertFalse(m.lookup(1000).has());
  }

  @Test
  public void lookup() {
    final var m = map();
    m.getOrCreate(100).set(50);
    JsonStreamReader reader = new JsonStreamReader("{\"42\":123,\"50\":100,\"100\":null}");
    m.__insert(reader);
    Assert.assertTrue(m.lookup(42).has());
    Assert.assertTrue(m.lookup(50).has());
    Assert.assertFalse(m.lookup(1000).has());
    Assert.assertFalse(m.lookup(100).has());
    m.iterator();
  }

  @Test
  public void resurrect() {
    final var m = map();
    MockRxChild child = new MockRxChild();
    m.__subscribe(child);
    m.getOrCreate(42).set(52);
    {
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      m.__commit("map", forward, reverse);
      Assert.assertEquals("\"map\":{\"42\":52}", forward.toString());
      Assert.assertEquals("\"map\":{\"42\":null}", reverse.toString());
    }
    m.remove(42);
    Assert.assertEquals(52, (int) m.getOrCreate(42).get());
    {
      JsonStreamWriter forward = new JsonStreamWriter();
      JsonStreamWriter reverse = new JsonStreamWriter();
      m.__commit("map", forward, reverse);
      Assert.assertEquals("\"map\":{}", forward.toString());
      Assert.assertEquals("\"map\":{}", reverse.toString());
    }
    child.assertInvalidateCount(5);
    m.iterator();
  }

  @Test
  public void codec() {
    Assert.assertEquals(
        123,
        (int)
            new RxMap.IntegerCodec<RxInt32>() {
              @Override
              public RxInt32 make(RxParent maker) {
                return null;
              }
            }.fromStr("123"));
    Assert.assertEquals(
        "123",
        new RxMap.IntegerCodec<RxInt32>() {
          @Override
          public RxInt32 make(RxParent maker) {
            return null;
          }
        }.toStr(123));
    Assert.assertEquals(
        123L,
        (long)
            new RxMap.LongCodec<RxInt32>() {
              @Override
              public RxInt32 make(RxParent maker) {
                return null;
              }
            }.fromStr("123"));
    Assert.assertEquals(
        "123",
        new RxMap.LongCodec<RxInt32>() {
          @Override
          public RxInt32 make(RxParent maker) {
            return null;
          }
        }.toStr(123L));
    Assert.assertEquals(
        "123",
        new RxMap.StringCodec<RxInt32>() {
          @Override
          public RxInt32 make(RxParent maker) {
            return null;
          }
        }.fromStr("123"));
    Assert.assertEquals(
        "123",
        new RxMap.StringCodec<RxInt32>() {
          @Override
          public RxInt32 make(RxParent maker) {
            return null;
          }
        }.toStr("123"));
  }
}
