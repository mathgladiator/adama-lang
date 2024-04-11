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

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.mocks.MockRecord;
import org.adamalang.runtime.mocks.MockRxChild;
import org.adamalang.runtime.mocks.MockRxParent;
import org.adamalang.runtime.natives.NtMaybe;
import org.junit.Assert;
import org.junit.Test;

public class RxMaybeTests {
  @Test
  public void memory() {
    final var parent = new MockRxParent();
    final var mi = new RxMaybe<>(parent, p -> new RxInt32(p, 42));
    Assert.assertEquals(64, mi.__memory());
    mi.make();
    Assert.assertEquals(112, mi.__memory());
  }

  @Test
  public void cost() {
    final var parent = new MockRxParent();
    final var mi = new RxMaybe<>(parent, p -> new RxInt32(p, 42));
    mi.__cost(542);
    Assert.assertEquals(542, parent.cost);
  }

  @Test
  public void commit_flow() {
    final var parent = new MockRxParent();
    final var mi = new RxMaybe<>(parent, p -> new RxInt32(p, 42));
    Assert.assertFalse(mi.has());
    commitCheck(mi, "", "");
    parent.assertDirtyCount(0);
    mi.make();
    parent.assertDirtyCount(1);
    commitCheck(mi, "\"v\":42", "\"v\":null");
    mi.delete();
    parent.assertDirtyCount(2);
    commitCheck(mi, "\"v\":null", "\"v\":42");
    commitCheck(mi, "", "");
    mi.make().set(50);
    mi.delete();
    parent.assertDirtyCount(3);
    commitCheck(mi, "\"v\":null", "");
    mi.make().set(50);
    parent.assertDirtyCount(4);
    commitCheck(mi, "\"v\":50", "\"v\":null");
    mi.make().set(5000);
    commitCheck(mi, "\"v\":5000", "\"v\":50");
    mi.__link();
  }

  @Test
  public void link_maybe() {
    final var parent = new MockRxParent();
    final var mi = new RxMaybe<>(parent, p -> new MockRecord(parent));
    mi.make();
    mi.__link();
  }

  private static void commitCheck(
      final RxMaybe<?, ?> mi, String expectedForward, String expectedReverse) {
    final var writer = new JsonStreamWriter();
    final var reverse = new JsonStreamWriter();
    mi.__commit("v", writer, reverse);
    Assert.assertEquals(expectedForward, writer.toString());
    Assert.assertEquals(expectedReverse, reverse.toString());
  }

  @Test
  public void alive_without_parent() {
    final var m = new RxMaybe<>(null, p -> new RxInt32(p, 1));
    Assert.assertTrue(m.__isAlive());
  }


  @Test
  public void killable_proxy() {
    final var m = new RxMaybe<>(null, p -> new RxMaybe<>(p, p2 -> new RxInt32(p2, 1)));
    m.make().make().set(100);
    m.__kill();
    commitCheck(m, "\"v\":100", "\"v\":null");
    m.delete();
  }

  @Test
  public void alive_with_parent() {
    MockRxParent parent = new MockRxParent();
    final var m = new RxMaybe<>(parent, p -> new RxInt32(p, 1));
    Assert.assertTrue(m.__isAlive());
    parent.alive = false;
    Assert.assertFalse(m.__isAlive());
  }

  @Test
  public void compare_a() {
    final var m1 = new RxMaybe<>(null, p -> new RxInt32(p, 1));
    final var m2 = new RxMaybe<>(null, p -> new RxInt32(p, 1));
    Assert.assertEquals(0, m1.compareValues(m2, RxInt32::compareTo));
    m2.make();
    Assert.assertEquals(1, m1.compareValues(m2, RxInt32::compareTo));
    m1.make();
    Assert.assertEquals(0, m1.compareValues(m2, RxInt32::compareTo));
  }

  @Test
  public void compare_b() {
    final var m1 = new RxMaybe<>(null, p -> new RxInt32(p, 1));
    final var m2 = new RxMaybe<>(null, p -> new RxInt32(p, 1));
    Assert.assertEquals(0, m1.compareValues(m2, RxInt32::compareTo));
    m1.make();
    Assert.assertEquals(-1, m1.compareValues(m2, RxInt32::compareTo));
    m2.make();
    Assert.assertEquals(0, m1.compareValues(m2, RxInt32::compareTo));
  }

  @Test
  public void compare_c() {
    final var m1 = new RxMaybe<>(null, p -> new RxInt32(p, 1));
    final var m2 = new RxMaybe<>(null, p -> new RxInt32(p, 2));
    m1.make();
    m2.make();
    Assert.assertEquals(-1, m1.compareValues(m2, RxInt32::compareTo));
  }

  @Test
  public void compare_d() {
    final var m1 = new RxMaybe<>(null, p -> new RxInt32(p, 2));
    final var m2 = new RxMaybe<>(null, p -> new RxInt32(p, 1));
    m1.make();
    m2.make();
    Assert.assertEquals(1, m1.compareValues(m2, RxInt32::compareTo));
  }

  @Test
  public void copy() {
    final var from = new RxMaybe<>(null, p -> new RxInt32(p, 42));
    final var to = new RxMaybe<>(null, p -> new RxInt32(p, 42));
    to.set(from.get());
    Assert.assertFalse(to.has());
    from.make();
    to.set(from.get());
    Assert.assertTrue(to.has());
    Assert.assertEquals(42, (int) to.get().get());
  }

  @Test
  public void dump1() {
    final var parent = new MockRxParent();
    final var mi = new RxMaybe<>(parent, p -> new RxInt32(p, 42));
    final var writer = new JsonStreamWriter();
    mi.__dump(writer);
    Assert.assertEquals("null", writer.toString());
  }

  @Test
  public void dump2() {
    final var parent = new MockRxParent();
    final var mi = new RxMaybe<>(parent, p -> new RxInt32(p, 42));
    mi.make();
    final var writer = new JsonStreamWriter();
    mi.__dump(writer);
    Assert.assertEquals("42", writer.toString());
  }

  @Test
  public void insert() {
    final var mb = new RxMaybe<>(null, parent -> new RxBoolean(parent, false));
    Assert.assertFalse(mb.has());
    mb.__insert(new JsonStreamReader("null"));
    Assert.assertFalse(mb.has());
    mb.__insert(new JsonStreamReader("true"));
    Assert.assertTrue(mb.has());
    Assert.assertTrue((boolean) mb.get().get());
    mb.__insert(new JsonStreamReader("false"));
    Assert.assertTrue(mb.has());
    Assert.assertFalse((boolean) mb.get().get());
    mb.__insert(new JsonStreamReader("null"));
    Assert.assertFalse(mb.has());
  }

  @Test
  public void patch() {
    final var mb = new RxMaybe<>(null, parent -> new RxBoolean(parent, false));
    Assert.assertFalse(mb.has());
    mb.__patch(new JsonStreamReader("null"));
    Assert.assertFalse(mb.has());
    mb.__patch(new JsonStreamReader("true"));
    Assert.assertTrue(mb.has());
    Assert.assertTrue((boolean) mb.get().get());
    mb.__patch(new JsonStreamReader("false"));
    Assert.assertTrue(mb.has());
    Assert.assertFalse((boolean) mb.get().get());
    mb.__patch(new JsonStreamReader("null"));
    Assert.assertFalse(mb.has());
  }

  @Test
  public void make() {
    final var parent = new MockRxParent();
    final var mi = new RxMaybe<>(parent, p -> new RxInt32(p, 42));
    Assert.assertFalse(mi.has());
    mi.make();
    Assert.assertTrue(mi.has());
  }

  @Test
  public void oddball() {
    final var record = new MockRecord(null);
    final var x = new RxMaybe<>(null, p -> record);
    Assert.assertFalse(x.get().has());
    x.make();
    Assert.assertTrue(x.get().has());
    Assert.assertTrue(record == x.get().get());
  }

  @Test
  public void revert_creation_flow() {
    final var mi = new RxMaybe<>(null, p -> new RxInt32(p, 42));
    final var child = new MockRxChild();
    mi.__subscribe(child);
    mi.make().set(50);
    mi.__revert();
    child.assertInvalidateCount(1);
    commitCheck(mi, "", "");
    mi.__lowerInvalid();
    mi.make().set(100);
    child.assertInvalidateCount(2);
  }

  @Test
  public void revert_data_flow() {
    final var mi = new RxMaybe<>(null, p -> new RxInt32(p, 42));
    final var child = new MockRxChild();
    mi.__subscribe(child);
    Assert.assertFalse(mi.has());
    commitCheck(mi, "", "");
    mi.make();
    child.assertInvalidateCount(1);
    commitCheck(mi, "\"v\":42", "\"v\":null");
    mi.make().set(5000);
    child.assertInvalidateCount(2);
    Assert.assertTrue(mi.has());
    mi.__revert();
    child.assertInvalidateCount(2);
    commitCheck(mi, "", "");
    mi.__raiseDirty();
    child.assertInvalidateCount(3);
  }

  @Test
  public void revert_delete_flow() {
    final var mi = new RxMaybe<>(null, p -> new RxInt32(p, 42));
    final var child = new MockRxChild();
    mi.__subscribe(child);
    Assert.assertFalse(mi.has());
    commitCheck(mi, "", "");
    mi.make();
    child.assertInvalidateCount(1);
    commitCheck(mi, "\"v\":42", "\"v\":null");
    mi.delete();
    child.assertInvalidateCount(2);
    Assert.assertFalse(mi.has());
    mi.__revert();
    Assert.assertTrue(mi.has());
    commitCheck(mi, "", "");
    child.assertInvalidateCount(2);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void to_native() {
    final var m1 = new RxMaybe<RxInt32, Integer>(null, p -> new RxInt32(p, 2));
    NtMaybe<Integer> n1 = m1.get();
    Assert.assertFalse(n1.has());
    m1.make();
    Assert.assertFalse(n1.has());
    n1 = m1.get();
    Assert.assertTrue(n1.has());
    Assert.assertEquals(2, (int) n1.get());
    Assert.assertTrue(m1.has());
    n1.delete();
    Assert.assertFalse(m1.has());
  }
}
