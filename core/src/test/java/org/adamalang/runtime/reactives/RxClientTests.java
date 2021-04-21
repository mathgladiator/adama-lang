/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.mocks.MockRxChild;
import org.adamalang.runtime.mocks.MockRxParent;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

public class RxClientTests {
  private static final NtClient A = new NtClient("a", "d");
  private static final NtClient B = new NtClient("b", "c");
  private static final NtClient CC = new NtClient("c", "c");

  @Test
  public void compare() {
    final var a = new RxClient(null, A);
    final var b = new RxClient(null, B);
    final var c = new RxClient(null, CC);
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
    final var c = new RxClient(parent, NtClient.NO_ONE);
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
    final var d = new RxClient(null, A);
    final var writer = new JsonStreamWriter();
    d.__dump(writer);
    Assert.assertEquals("{\"agent\":\"a\",\"authority\":\"d\"}", writer.toString());
  }

  @Test
  public void hash() {
    Assert.assertEquals(2016, new RxClient(null, NtClient.NO_ONE).getIndexValue());
    Assert.assertEquals(3107, new RxClient(null, A).getIndexValue());
    Assert.assertEquals(3137, new RxClient(null, B).getIndexValue());
    Assert.assertEquals(3168, new RxClient(null, CC).getIndexValue());
  }

  @Test
  public void insert() {
    final var c = new RxClient(null, NtClient.NO_ONE);
    c.__insert(new JsonStreamReader("{\"agent\":\"foo\",\"authority\":\"xyz\"}"));
    final var g = c.get();
    Assert.assertEquals("foo", g.agent);
    Assert.assertEquals("xyz", g.authority);
  }

  @Test
  public void patch() {
    final var c = new RxClient(null, NtClient.NO_ONE);
    c.__patch(new JsonStreamReader("{\"agent\":\"foo\",\"authority\":\"xyz\"}"));
    final var g = c.get();
    Assert.assertEquals("foo", g.agent);
    Assert.assertEquals("xyz", g.authority);
  }

  @Test
  public void invalidate_and_revert() {
    final var c = new RxClient(null, NtClient.NO_ONE);
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
    Assert.assertEquals(NtClient.NO_ONE, c.get());
    c.__cancelAllSubscriptions();
    c.set(B);
    child.assertInvalidateCount(3);
  }
}
