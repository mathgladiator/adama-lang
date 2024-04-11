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
  public void idx_value() {
    final var rt = new RxBoolean(null, true);
    final var rf = new RxBoolean(null, false);
    Assert.assertEquals(1, rt.getIndexValue());
    Assert.assertEquals(0, rf.getIndexValue());
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
    parent.assertDirtyCount(1);
    rx.set(true);
    parent.assertDirtyCount(1);
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
    invalidate.assertInvalidateCount(1);
    rx.__raiseDirty();
    rx.__revert();
    rx.__revert();
    rx.__revert();
    invalidate.assertInvalidateCount(2);
  }
}
