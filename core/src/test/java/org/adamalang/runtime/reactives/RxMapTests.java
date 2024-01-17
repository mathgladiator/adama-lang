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
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.mocks.MockRxChild;
import org.adamalang.runtime.mocks.MockRxParent;
import org.adamalang.runtime.natives.NtPrincipal;
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

  @Test
  public void memoryPrincipal() {
    final var m = new RxMap<NtPrincipal, RxPrincipal>(
        new MockRxParent(),
        new RxMap.PrincipalCodec<RxPrincipal>() {
          @Override
          public RxPrincipal make(RxParent maker) {
            return new RxPrincipal(maker, new NtPrincipal("a", "a"));
          }
        });
    Assert.assertEquals(168, m.__memory());
    m.getOrCreate(new NtPrincipal("a", "b")).set(new NtPrincipal("b", "c"));
    Assert.assertEquals(252, m.__memory());
    JsonStreamWriter forward = new JsonStreamWriter();
    JsonStreamWriter reverse = new JsonStreamWriter();
    m.__commit("name", forward, reverse);
    Assert.assertEquals("\"name\":{\"a/b\":{\"agent\":\"b\",\"authority\":\"c\"}}", forward.toString());
    Assert.assertEquals("\"name\":{\"a/b\":null}", reverse.toString());
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
  public void cost_report() {
    MockRxParent parent = new MockRxParent();
    map(parent).__cost(423);
    Assert.assertEquals(423, parent.cost);
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
  public void map_min() {
    final var m = map();
    m.getOrCreate(1).set(10);
    m.getOrCreate(2).set(15);
    m.getOrCreate(3).set(20);
    m.getOrCreate(4).set(30);
    Assert.assertEquals(1, (int) m.min().get().key);
    Assert.assertEquals(10, (int) m.min().get().value.get());
  }

  @Test
  public void map_max() {
    final var m = map();
    m.getOrCreate(1).set(10);
    m.getOrCreate(2).set(15);
    m.getOrCreate(3).set(20);
    m.getOrCreate(4).set(30);
    Assert.assertEquals(4, (int) m.max().get().key);
    Assert.assertEquals(30, (int) m.max().get().value.get());
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
    child.assertInvalidateCount(1);
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
    child.assertInvalidateCount(2);
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
