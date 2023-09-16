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
import org.junit.Assert;
import org.junit.Test;

public class RxStringTests {
  @Test
  public void memory() {
    final var a = new RxString(null, "a");
    final var b = new RxString(null, "b");
    Assert.assertEquals(60, a.__memory());
    Assert.assertEquals(60, b.__memory());
  }

  @Test
  public void idx_value() {
    final var a = new RxString(null, "a");
    Assert.assertEquals("a".hashCode(), a.getIndexValue());
  }

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
    parent.assertDirtyCount(1);
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
  public void insert() {
    final var d = new RxString(null, "");
    Assert.assertFalse(d.has());
    d.__insert(new JsonStreamReader("\"x\""));
    Assert.assertEquals("x", d.get());
    Assert.assertTrue(d.has());
  }

  @Test
  public void patch() {
    final var d = new RxString(null, "");
    Assert.assertFalse(d.has());
    d.__patch(new JsonStreamReader("\"x\""));
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
    child.assertInvalidateCount(1);
    Assert.assertEquals("cake", s.get());
    s.__revert();
    Assert.assertEquals("xyz", s.get());
    child.assertInvalidateCount(2);
  }

  @Test
  public void ops() {
    final var parent = new MockRxParent();
    final var s = new RxString(parent, "a");
    s.opAddTo(0);
    parent.assertDirtyCount(1);
    s.opAddTo(true);
    parent.assertDirtyCount(1);
    s.opAddTo(0.0);
    parent.assertDirtyCount(1);
    s.opAddTo("b");
    parent.assertDirtyCount(1);
    Assert.assertEquals("a0true0.0b", s.get());
  }
}
