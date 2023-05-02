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
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class RxPrincipalTests {
  private static final NtPrincipal A = new NtPrincipal("a", "d");
  private static final NtPrincipal B = new NtPrincipal("b", "c");
  private static final NtPrincipal CC = new NtPrincipal("c", "c");

  @Test
  public void memory() {
    final var a = new RxPrincipal(null, A);
    Assert.assertEquals(64, a.__memory());
  }

  @Test
  public void compare() {
    final var a = new RxPrincipal(null, A);
    final var b = new RxPrincipal(null, B);
    final var c = new RxPrincipal(null, CC);
    Assert.assertEquals(1, a.compareTo(b));
    Assert.assertEquals(-1, b.compareTo(a));
    Assert.assertEquals(1, a.compareTo(c));
    Assert.assertEquals(-1, b.compareTo(c));
    Assert.assertEquals(-1, c.compareTo(a));
    Assert.assertEquals(1, c.compareTo(b));
  }

  @Test
  public void dirty_and_commit() {
    final var parent = new MockRxParent();
    final var c = new RxPrincipal(parent, NtPrincipal.NO_ONE);
    parent.assertDirtyCount(0);
    c.set(A);
    parent.assertDirtyCount(1);
    c.set(A);
    parent.assertDirtyCount(1);
    final var writer = new JsonStreamWriter();
    final var reverse = new JsonStreamWriter();
    c.__commit("v", writer, reverse);
    Assert.assertEquals("\"v\":{\"agent\":\"a\",\"authority\":\"d\"}", writer.toString());
    Assert.assertEquals("\"v\":{\"agent\":\"?\",\"authority\":\"?\"}", reverse.toString());
    final var writerAgain = new JsonStreamWriter();
    final var reverseAgain = new JsonStreamWriter();
    c.__commit("v2", writerAgain, reverseAgain);
    Assert.assertEquals("", writerAgain.toString());
    Assert.assertEquals("", reverseAgain.toString());
  }

  @Test
  public void dump() {
    final var d = new RxPrincipal(null, A);
    final var writer = new JsonStreamWriter();
    d.__dump(writer);
    Assert.assertEquals("{\"agent\":\"a\",\"authority\":\"d\"}", writer.toString());
  }

  @Test
  public void hash() {
    Assert.assertEquals(2016, new RxPrincipal(null, NtPrincipal.NO_ONE).getIndexValue());
    Assert.assertEquals(3107, new RxPrincipal(null, A).getIndexValue());
    Assert.assertEquals(3137, new RxPrincipal(null, B).getIndexValue());
    Assert.assertEquals(3168, new RxPrincipal(null, CC).getIndexValue());
  }

  @Test
  public void insert() {
    final var c = new RxPrincipal(null, NtPrincipal.NO_ONE);
    c.__insert(new JsonStreamReader("{\"agent\":\"foo\",\"authority\":\"xyz\"}"));
    final var g = c.get();
    Assert.assertEquals("foo", g.agent);
    Assert.assertEquals("xyz", g.authority);
  }

  @Test
  public void patch() {
    final var c = new RxPrincipal(null, NtPrincipal.NO_ONE);
    c.__patch(new JsonStreamReader("{\"agent\":\"foo\",\"authority\":\"xyz\"}"));
    final var g = c.get();
    Assert.assertEquals("foo", g.agent);
    Assert.assertEquals("xyz", g.authority);
  }

  @Test
  public void invalidate_and_revert() {
    final var c = new RxPrincipal(null, NtPrincipal.NO_ONE);
    final var child = new MockRxChild();
    c.__subscribe(child);
    child.assertInvalidateCount(0);
    c.set(A);
    child.assertInvalidateCount(1);
    c.set(A);
    Assert.assertEquals(A, c.get());
    child.assertInvalidateCount(1);
    c.set(B);
    Assert.assertEquals(B, c.get());
    child.assertInvalidateCount(2);
    c.__revert();
    child.assertInvalidateCount(3);
    Assert.assertEquals(NtPrincipal.NO_ONE, c.get());
    c.__cancelAllSubscriptions();
    c.set(B);
    child.assertInvalidateCount(3);
  }
}
