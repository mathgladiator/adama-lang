/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
    child.assertInvalidateCount(1);
    c.__revert();
    child.assertInvalidateCount(1);
    Assert.assertEquals(NtPrincipal.NO_ONE, c.get());
    c.__raiseDirty();
    c.__revert();
    c.__revert();
    c.__revert();
    child.assertInvalidateCount(2);
  }
}
