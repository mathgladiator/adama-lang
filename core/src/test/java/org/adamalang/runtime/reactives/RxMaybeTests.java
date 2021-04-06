/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
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
  private static void commitCheck(final RxMaybe<RxInt32> mi, String expectedForward, String expectedReverse) {
    final var writer = new JsonStreamWriter();
    final var reverse = new JsonStreamWriter();
    mi.__commit("v", writer, reverse);
    Assert.assertEquals(expectedForward, writer.toString());
    Assert.assertEquals(expectedReverse, reverse.toString());
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
    parent.assertDirtyCount(5);
    commitCheck(mi, "\"v\":null", "");
    mi.make().set(50);
    parent.assertDirtyCount(7);
    commitCheck(mi, "\"v\":50", "\"v\":null");
    mi.make().set(5000);
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
    child.assertInvalidateCount(4);
    commitCheck(mi, "", "");
  }

  @Test
  public void revert_data_flow() {
    final var mi = new RxMaybe<>(null, p -> new RxInt32(p, 42));
    final var child = new MockRxChild();
    mi.__subscribe(child);
    Assert.assertFalse(mi.has());
    commitCheck(mi, "", "");
    mi.make();
    child.assertInvalidateCount(2);
    commitCheck(mi, "\"v\":42", "\"v\":null");
    mi.make().set(5000);
    child.assertInvalidateCount(4);
    Assert.assertTrue(mi.has());
    mi.__revert();
    child.assertInvalidateCount(6);
    commitCheck(mi, "", "");
  }

  @Test
  public void revert_delete_flow() {
    final var mi = new RxMaybe<>(null, p -> new RxInt32(p, 42));
    final var child = new MockRxChild();
    mi.__subscribe(child);
    Assert.assertFalse(mi.has());
    commitCheck(mi, "", "");
    mi.make();
    child.assertInvalidateCount(2);
    commitCheck(mi, "\"v\":42", "\"v\":null");
    mi.delete();
    child.assertInvalidateCount(3);
    Assert.assertFalse(mi.has());
    mi.__revert();
    Assert.assertTrue(mi.has());
    commitCheck(mi, "", "");
    child.assertInvalidateCount(4);
  }

  @Test
  @SuppressWarnings("unchecked")
  public void to_native() {
    final var m1 = new RxMaybe<>(null, p -> new RxInt32(p, 2));
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
