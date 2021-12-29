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

public class RxInt64Tests {
  @Test
  public void memory() {
    final var l1 = new RxInt64(null, 1);
    final var l2 = new RxInt64(null, 2);
    Assert.assertEquals(56, l1.__memory());
    Assert.assertEquals(56, l2.__memory());
  }

  @Test
  public void compare() {
    final var l1 = new RxInt64(null, 1);
    final var l2 = new RxInt64(null, 2);
    Assert.assertEquals(-1, l1.compareTo(l2));
    Assert.assertEquals(1, l2.compareTo(l1));
  }

  @Test
  public void dirty_and_commit() {
    final var parent = new MockRxParent();
    final var l = new RxInt64(parent, 42);
    Assert.assertEquals(42, (long) l.get());
    l.set(50L);
    parent.assertDirtyCount(1);
    l.set(60);
    parent.assertDirtyCount(2);
    final var writer = new JsonStreamWriter();
    final var reverse = new JsonStreamWriter();
    l.__commit("v", writer, reverse);
    Assert.assertEquals("\"v\":\"60\"", writer.toString());
    Assert.assertEquals("\"v\":\"42\"", reverse.toString());
    final var writerAgain = new JsonStreamWriter();
    final var reverseAgain = new JsonStreamWriter();
    l.__commit("v2", writerAgain, reverseAgain);
    Assert.assertEquals("", writerAgain.toString());
    Assert.assertEquals("", reverseAgain.toString());
    l.set(12354124314124L);
    final var writerThird = new JsonStreamWriter();
    final var reverseThird = new JsonStreamWriter();
    l.__commit("v", writerThird, reverseThird);
    Assert.assertEquals("\"v\":\"12354124314124\"", writerThird.toString());
    Assert.assertEquals("\"v\":\"60\"", reverseThird.toString());
  }

  @Test
  public void dump() {
    final var d = new RxInt64(null, 424242L);
    final var writer = new JsonStreamWriter();
    d.__dump(writer);
    Assert.assertEquals("\"424242\"", writer.toString());
  }

  @Test
  public void insert1() {
    final var d = new RxInt64(null, 1);
    d.__insert(new JsonStreamReader("42"));
    Assert.assertEquals(42, (long) d.get());
  }

  @Test
  public void insert2() {
    final var d = new RxInt64(null, 1);
    d.__insert(new JsonStreamReader("\"42\""));
    Assert.assertEquals(42, (long) d.get());
  }

  @Test
  public void patch1() {
    final var d = new RxInt64(null, 1);
    d.__patch(new JsonStreamReader("42"));
    Assert.assertEquals(42, (long) d.get());
  }

  @Test
  public void patch2() {
    final var d = new RxInt64(null, 1);
    d.__patch(new JsonStreamReader("\"42\""));
    Assert.assertEquals(42, (long) d.get());
  }

  @Test
  public void invalidate_and_revert() {
    final var l = new RxInt64(null, 42);
    final var child = new MockRxChild();
    l.__subscribe(child);
    l.set(50);
    child.assertInvalidateCount(1);
    l.set(50);
    child.assertInvalidateCount(1);
    l.set(55);
    child.assertInvalidateCount(2);
    Assert.assertEquals(55, l.getIndexValue());
    l.__revert();
    child.assertInvalidateCount(3);
    Assert.assertEquals(42, (long) l.get());
    l.__revert();
    child.assertInvalidateCount(3);
    l.__cancelAllSubscriptions();
    l.set(100);
    child.assertInvalidateCount(3);
  }

  @Test
  public void ops() {
    final var l = new RxInt64(null, 1);
    l.bumpUpPre();
    Assert.assertEquals(2, (long) l.get());
    l.bumpUpPost();
    Assert.assertEquals(3, (long) l.get());
    l.bumpDownPre();
    Assert.assertEquals(2, (long) l.get());
    l.bumpDownPost();
    Assert.assertEquals(1, (long) l.get());
    l.opAddTo(10);
    Assert.assertEquals(11, (long) l.get());
    l.opMultBy(2);
    Assert.assertEquals(22, (long) l.get());
    l.opSubFrom(7);
    Assert.assertEquals(15, (long) l.get());
  }
}
