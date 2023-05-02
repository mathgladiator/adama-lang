/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.mocks.MockRxChild;
import org.adamalang.runtime.mocks.MockRxParent;
import org.junit.Assert;
import org.junit.Test;

public class RxFastStringTests {
  @Test
  public void memory() {
    final var a = new RxFastString(null, "a");
    final var b = new RxFastString(null, "bison");
    Assert.assertEquals(60, a.__memory());
    Assert.assertEquals(76, b.__memory());
  }

  @Test
  public void compare1() {
    final var a = new RxFastString(null, "a");
    final var b = new RxFastString(null, "b");
    Assert.assertEquals(-1, a.compareTo(b));
    Assert.assertEquals(1, b.compareTo(a));
  }

  @Test
  public void compare2() {
    final var a = new RxString(null, "a");
    final var b = new RxFastString(null, "b");
    Assert.assertEquals(-1, a.compareTo(b));
    Assert.assertEquals(1, b.compareTo(a));
  }

  @Test
  public void compare3() {
    final var a = new RxFastString(null, "a");
    final var b = new RxString(null, "b");
    Assert.assertEquals(-1, a.compareTo(b));
    Assert.assertEquals(1, b.compareTo(a));
  }

  @Test
  public void dirty_and_commit() {
    final var parent = new MockRxParent();
    final var s = new RxFastString(parent, "xxx");
    Assert.assertEquals("xxx", s.get());
    parent.assertDirtyCount(0);
    s.set("cake");
    parent.assertDirtyCount(1);
    s.set("cake");
    parent.assertDirtyCount(2);
    Assert.assertEquals("cake", s.get());
    final var writer = new JsonStreamWriter();
    final var reverse = new JsonStreamWriter();
    s.__commit("v", writer, reverse);
    Assert.assertEquals("\"v\":\"cake\"", writer.toString());
    Assert.assertEquals("\"v\":\"xxx\"", reverse.toString());
    final var writerAgain = new JsonStreamWriter();
    final var reverseAgain = new JsonStreamWriter();
    s.__commit("v2", writerAgain, reverseAgain);
    Assert.assertEquals("", writerAgain.toString());
    Assert.assertEquals("", reverseAgain.toString());
  }

  @Test
  public void dump() {
    final var d = new RxFastString(null, "xyz");
    final var writer = new JsonStreamWriter();
    d.__dump(writer);
    Assert.assertEquals("\"xyz\"", writer.toString());
  }

  @Test
  public void invalidate_and_revert() {
    final var child = new MockRxChild();
    final var s = new RxFastString(null, "xyz");
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
    final var s = new RxFastString(parent, "a");
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
