/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.mocks.MockRxChild;
import org.adamalang.runtime.mocks.MockRxParent;
import org.junit.Assert;
import org.junit.Test;

public class RxBooleanTests {
  @Test
  public void memory() {
    final var rt = new RxBoolean(null, true);
    Assert.assertEquals(42, rt.__memory());
  }

  @Test
  public void compare() {
    final var rt = new RxBoolean(null, true);
    final var rf = new RxBoolean(null, false);
    Assert.assertEquals(1, rt.compareTo(rf));
    Assert.assertEquals(-1, rf.compareTo(rt));
  }

  @Test
  public void dirty_and_commit() {
    final var parent = new MockRxParent();
    final var rx = new RxBoolean(parent, false);
    Assert.assertEquals(false, rx.get());
    parent.assertDirtyCount(0);
    rx.set(true);
    parent.assertDirtyCount(1);
    Assert.assertEquals(true, rx.get());
    Assert.assertEquals(0, rx.compareTo(rx));
    rx.set(true);
    parent.assertDirtyCount(1);
    rx.set(true);
    parent.assertDirtyCount(1);
    rx.set(false);
    parent.assertDirtyCount(2);
    rx.set(true);
    parent.assertDirtyCount(3);
    final var writer = new JsonStreamWriter();
    final var reverse = new JsonStreamWriter();
    rx.__commit("v", writer, reverse);
    Assert.assertEquals("\"v\":true", writer.toString());
    Assert.assertEquals("\"v\":false", reverse.toString());
    final var writerAgain = new JsonStreamWriter();
    final var reverseAgain = new JsonStreamWriter();
    rx.__commit("v2", writerAgain, reverseAgain);
    Assert.assertEquals("", writerAgain.toString());
    Assert.assertEquals("", reverseAgain.toString());
  }

  @Test
  public void dump() {
    final var d = new RxBoolean(null, false);
    JsonStreamWriter writer = new JsonStreamWriter();
    d.__dump(writer);
    Assert.assertEquals("false", writer.toString());
  }

  @Test
  public void insert() {
    final var d = new RxBoolean(null, false);
    d.__insert(new JsonStreamReader("true"));
    Assert.assertTrue(d.get());
    d.__insert(new JsonStreamReader("false"));
    Assert.assertFalse(d.get());
    d.__insert(new JsonStreamReader("true"));
    Assert.assertTrue(d.get());
  }

  @Test
  public void patch() {
    final var d = new RxBoolean(null, false);
    d.__patch(new JsonStreamReader("true"));
    Assert.assertTrue(d.get());
    d.__patch(new JsonStreamReader("false"));
    Assert.assertFalse(d.get());
    d.__patch(new JsonStreamReader("true"));
    Assert.assertTrue(d.get());
  }

  @Test
  public void invalidate_and_revert() {
    final var parent = new MockRxParent();
    final var rx = new RxBoolean(parent, false);
    final var invalidate = new MockRxChild();
    rx.__subscribe(invalidate);
    invalidate.assertInvalidateCount(0);
    rx.set(true);
    invalidate.assertInvalidateCount(1);
    rx.set(true);
    invalidate.assertInvalidateCount(1);
    rx.__revert();
    invalidate.assertInvalidateCount(2);
    rx.__revert();
    invalidate.assertInvalidateCount(2);
    rx.__cancelAllSubscriptions();
    rx.set(true);
    invalidate.assertInvalidateCount(2);
  }
}
