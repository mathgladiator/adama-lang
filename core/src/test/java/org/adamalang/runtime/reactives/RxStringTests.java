/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.mocks.MockRxChild;
import org.adamalang.runtime.mocks.MockRxParent;
import org.junit.Assert;
import org.junit.Test;

public class RxStringTests {
  @Test
  public void compare() {
    final var a = new RxString(null, "a");
    final var b = new RxString(null, "b");
    Assert.assertEquals(-1, a.compareTo(b));
    Assert.assertEquals(1, b.compareTo(a));
  }
  @Test
  public void dump() {
    final var d = new RxString(null, "xyz");
    JsonStreamWriter writer = new JsonStreamWriter();
    d.__dump(writer);
    Assert.assertEquals("\"xyz\"", writer.toString());
  }
  @Test
  public void dirty_and_commit() {
    final var parent = new MockRxParent();
    final var s = new RxString(parent, "xxx");
    Assert.assertEquals("xxx", s.get());
    parent.assertDirtyCount(0);
    s.set("cake");
    parent.assertDirtyCount(1);
    s.set("cake");
    parent.assertDirtyCount(2);
    Assert.assertEquals("cake", s.get());
    final var writer = new JsonStreamWriter();
    s.__commit("v", writer);
    Assert.assertEquals("\"v\":\"cake\"", writer.toString());
    final var writerAgain = new JsonStreamWriter();
    s.__commit("v2", writerAgain);
    Assert.assertEquals("", writerAgain.toString());
  }

  @Test
  public void insert() {
    final var d = new RxString(null, "");
    Assert.assertFalse(d.has());
    d.__insert(new JsonStreamReader("\"x\""));
    Assert.assertEquals("x", d.get());
    Assert.assertTrue(d.has());
  }

  @Test
  public void invalidate_and_revert() {
    final var child = new MockRxChild();
    final var s = new RxString(null, "xyz");
    s.__subscribe(child);
    Assert.assertEquals("xyz", s.get());
    child.assertInvalidateCount(0);
    s.set("cake");
    child.assertInvalidateCount(1);
    s.set("cake");
    child.assertInvalidateCount(2);
    Assert.assertEquals("cake", s.get());
    s.__revert();
    Assert.assertEquals("xyz", s.get());
    child.assertInvalidateCount(3);
  }

  @Test
  public void ops() {
    final var parent = new MockRxParent();
    final var s = new RxString(parent, "a");
    s.opAddTo(0);
    parent.assertDirtyCount(1);
    s.opAddTo(true);
    parent.assertDirtyCount(2);
    s.opAddTo(0.0);
    parent.assertDirtyCount(3);
    s.opAddTo("b");
    parent.assertDirtyCount(4);
    Assert.assertEquals("a0true0.0b", s.get());
  }
}
