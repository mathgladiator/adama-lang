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

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.mocks.MockRxChild;
import org.adamalang.runtime.mocks.MockRxParent;
import org.junit.Assert;
import org.junit.Test;

public class RxDoubleTests {
  @Test
  public void memory() {
    final var d1 = new RxDouble(null, 1);
    Assert.assertEquals(56, d1.__memory());
  }

  @Test
  public void compare() {
    final var d1 = new RxDouble(null, 1);
    final var d2 = new RxDouble(null, 2);
    Assert.assertEquals(-1, d1.compareTo(d2));
    Assert.assertEquals(1, d2.compareTo(d1));
  }

  @Test
  public void dirty_and_commit() {
    final var parent = new MockRxParent();
    final var d = new RxDouble(parent, 42);
    Assert.assertEquals(42, d.get(), 0.1);
    d.set(50);
    parent.assertDirtyCount(1);
    d.set(6.28);
    parent.assertDirtyCount(2);
    final var writer = new JsonStreamWriter();
    final var reverse = new JsonStreamWriter();
    d.__commit("v", writer, reverse);
    Assert.assertEquals("\"v\":6.28", writer.toString());
    Assert.assertEquals("\"v\":42.0", reverse.toString());
    final var writerAgain = new JsonStreamWriter();
    final var reverseAgain = new JsonStreamWriter();
    d.__commit("v2", writerAgain, reverseAgain);
    Assert.assertEquals("", writerAgain.toString());
    Assert.assertEquals("", reverseAgain.toString());
  }

  @Test
  public void dump() {
    final var d = new RxDouble(null, 1232.5);
    final var writer = new JsonStreamWriter();
    d.__dump(writer);
    Assert.assertEquals("1232.5", writer.toString());
  }

  @Test
  public void insert() {
    final var d = new RxDouble(null, 1);
    d.__insert(new JsonStreamReader("42"));
    Assert.assertEquals(42, d.get(), 0.1);
  }

  @Test
  public void patch() {
    final var d = new RxDouble(null, 1);
    d.__patch(new JsonStreamReader("42"));
    Assert.assertEquals(42, d.get(), 0.1);
  }

  @Test
  public void invalidate_and_revert() {
    final var d = new RxDouble(null, 42);
    final var child = new MockRxChild();
    d.__subscribe(child);
    d.set(50);
    child.assertInvalidateCount(1);
    final Double dFity = 50.0;
    d.set(dFity);
    child.assertInvalidateCount(2);
    d.set(55.0);
    child.assertInvalidateCount(3);
    d.__revert();
    child.assertInvalidateCount(4);
    Assert.assertEquals(42, d.get(), 0.1);
    d.__revert();
    child.assertInvalidateCount(4);
    d.__cancelAllSubscriptions();
    d.set(100);
    child.assertInvalidateCount(4);
  }

  @Test
  public void ops() {
    final var d = new RxDouble(null, 1);
    d.bumpUpPre();
    Assert.assertEquals(2, d.get(), 0.1);
    d.bumpUpPost();
    Assert.assertEquals(3, d.get(), 0.1);
    d.bumpDownPre();
    Assert.assertEquals(2, d.get(), 0.1);
    d.bumpDownPost();
    Assert.assertEquals(1, d.get(), 0.1);
    d.opAddTo(10);
    Assert.assertEquals(11, d.get(), 0.1);
    d.opMultBy(2);
    Assert.assertEquals(22, d.get(), 0.1);
    d.opAddTo(-(7));
    Assert.assertEquals(15, d.get(), 0.1);
  }
}
