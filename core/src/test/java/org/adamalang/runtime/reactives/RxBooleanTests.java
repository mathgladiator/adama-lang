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

public class RxBooleanTests {
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
    rx.__commit("v", writer);
    Assert.assertEquals("\"v\":true", writer.toString());
    final var writerAgain = new JsonStreamWriter();
    rx.__commit("v2", writerAgain);
    Assert.assertEquals("", writerAgain.toString());
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
