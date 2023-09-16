/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
import org.adamalang.runtime.natives.NtComplex;
import org.junit.Assert;
import org.junit.Test;

public class RxComplexTests {
  @Test
  public void memory() {
    final var d = new RxComplex(null, new NtComplex(1, 2));
    Assert.assertEquals(88, d.__memory());
  }

  @Test
  public void math() {
    final var parent = new MockRxParent();
    final var d = new RxComplex(parent, new NtComplex(1, 2));
    d.set(0.0);
    d.opAddTo(1);
    d.opAddTo(2L);
    d.opAddTo(3.14);
    d.opAddTo(new NtComplex(10.0, 10.0));
    Assert.assertTrue(Math.abs(d.get().real - 16.14) < 0.001);
    Assert.assertTrue(Math.abs(d.get().imaginary - 10.0) < 0.001);
    d.opMultBy(1.5);
    Assert.assertTrue(Math.abs(d.get().real - 24.21) < 0.001);
    Assert.assertTrue(Math.abs(d.get().imaginary - 15.0) < 0.001);
    d.opMultBy(new NtComplex(0.0, 1.0));
    Assert.assertTrue(Math.abs(d.get().real - -15) < 0.001);
    Assert.assertTrue(Math.abs(d.get().imaginary - 24.21) < 0.001);
    d.opMultBy(new NtComplex(1.0, 0.0));
    Assert.assertTrue(Math.abs(d.get().real - -15) < 0.001);
    Assert.assertTrue(Math.abs(d.get().imaginary - 24.21) < 0.001);
    d.opSubFrom(new NtComplex(1, 1));
    Assert.assertTrue(Math.abs(d.get().real - -16) < 0.001);
    Assert.assertTrue(Math.abs(d.get().imaginary - 23.21) < 0.001);
  }

  @Test
  public void dirty_and_commit() {
    final var parent = new MockRxParent();
    final var d = new RxComplex(parent, new NtComplex(1, 2));
    Assert.assertEquals(new NtComplex(1, 2), d.get());
    d.set(new NtComplex(3, 4));
    parent.assertDirtyCount(1);
    d.set(new NtComplex(4, 5));
    parent.assertDirtyCount(1);
    final var writer = new JsonStreamWriter();
    final var reverse = new JsonStreamWriter();
    d.__commit("v", writer, reverse);
    Assert.assertEquals("\"v\":{\"r\":4.0,\"i\":5.0}", writer.toString());
    Assert.assertEquals("\"v\":{\"r\":1.0,\"i\":2.0}", reverse.toString());
    final var writerAgain = new JsonStreamWriter();
    final var reverseAgain = new JsonStreamWriter();
    d.__commit("v2", writerAgain, reverseAgain);
    Assert.assertEquals("", writerAgain.toString());
    Assert.assertEquals("", reverseAgain.toString());
  }

  @Test
  public void dump() {
    final var d = new RxComplex(null, new NtComplex(1, 2));
    final var writer = new JsonStreamWriter();
    d.__dump(writer);
    Assert.assertEquals("{\"r\":1.0,\"i\":2.0}", writer.toString());
  }

  @Test
  public void insert() {
    final var d = new RxComplex(null, new NtComplex(1, 2));
    d.__insert(new JsonStreamReader("{\"r\":3,\"i\":4}"));
    Assert.assertEquals(new NtComplex(3, 4), d.get());
    final var writer = new JsonStreamWriter();
    final var reverse = new JsonStreamWriter();
    d.__commit("v", writer, reverse);
    Assert.assertEquals("", writer.toString());
    Assert.assertEquals("", reverse.toString());
  }

  @Test
  public void patch() {
    final var d = new RxComplex(null, new NtComplex(1, 2));
    d.__patch(new JsonStreamReader("{\"r\":3,\"i\":4}"));
    Assert.assertEquals(new NtComplex(3, 4), d.get());
    final var writer = new JsonStreamWriter();
    final var reverse = new JsonStreamWriter();
    d.__commit("v", writer, reverse);
    Assert.assertEquals("\"v\":{\"r\":3.0,\"i\":4.0}", writer.toString());
    Assert.assertEquals("\"v\":{\"r\":1.0,\"i\":2.0}", reverse.toString());
  }

  @Test
  public void invalidate_and_revert() {
    final var d = new RxComplex(null, new NtComplex(1, 2));
    final var child = new MockRxChild();
    d.__subscribe(child);
    d.set(new NtComplex(3, 4));
    Assert.assertEquals(new NtComplex(3, 4), d.get());
    child.assertInvalidateCount(1);
    d.set(new NtComplex(4, 5));
    child.assertInvalidateCount(1);
    d.set(new NtComplex(5, 6));
    child.assertInvalidateCount(1);
    d.__revert();
    child.assertInvalidateCount(2);
    Assert.assertEquals(new NtComplex(1, 2), d.get());
    d.__revert();
    child.assertInvalidateCount(2);
    d.__cancelAllSubscriptions();
    d.set(new NtComplex(7, 8));
    child.assertInvalidateCount(2);
  }
}
