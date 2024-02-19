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
import org.adamalang.runtime.mocks.MockRecord;
import org.adamalang.runtime.mocks.MockRxParent;
import org.junit.Assert;
import org.junit.Test;

import java.util.TreeSet;

public class RxLazyTests {
  @Test
  public void flow() {
    final var val = new RxInt32(null, 42);
    final var lz = new RxLazy<>(null, () -> val.get() * val.get(), null);
    final var lz2 = new RxLazy<>(null, () -> lz.get() / 2, null);
    Assert.assertEquals(1, lz.getGeneration());
    Assert.assertEquals(1, lz2.getGeneration());
    val.__subscribe(lz);
    lz.__subscribe(lz2);
    val.set(4);
    Assert.assertEquals(16, (int) lz.get());
    Assert.assertEquals(8, (int) lz2.get());
    val.set(6);
    Assert.assertEquals(18, (int) lz2.get());
    Assert.assertEquals(36, (int) lz.get());
    Assert.assertEquals(18, (int) lz2.get());
    Assert.assertEquals(36, (int) lz.get());
    val.set(10);
    Assert.assertEquals(50, (int) lz2.get());
    lz.__insert(new JsonStreamReader("{}"));
    lz.__dump(null);
    lz.__patch(new JsonStreamReader("{}"));
    Assert.assertEquals(1, lz.getGeneration());
    Assert.assertEquals(1, lz2.getGeneration());
    lz.__forceSettle();
  }

  @Test
  public void flow_with_commit() {
    final var val = new RxInt32(null, 42);
    final var lz = new RxLazy<>(null, () -> val.get() * val.get(), null);
    final var lz2 = new RxLazy<>(null, () -> lz.get() / 2, null);
    Assert.assertEquals(1, lz.getGeneration());
    Assert.assertEquals(1, lz2.getGeneration());
    val.__subscribe(lz);
    lz.__subscribe(lz2);
    val.set(4);
    Assert.assertEquals(16, (int) lz.get());
    Assert.assertEquals(8, (int) lz2.get());
    JsonStreamWriter f = new JsonStreamWriter();
    JsonStreamWriter r = new JsonStreamWriter();
    val.__commit("val", f, r);
    lz.__settle(null);
    lz2.__settle(null);
    Assert.assertEquals("\"val\":4", f.toString());
    Assert.assertEquals("\"val\":42", r.toString());
    Assert.assertEquals(65522, lz.getGeneration());
    Assert.assertEquals(65522, lz2.getGeneration());
    val.set(6);
    Assert.assertEquals(18, (int) lz2.get());
    Assert.assertEquals(36, (int) lz.get());
    Assert.assertEquals(18, (int) lz2.get());
    Assert.assertEquals(36, (int) lz.get());
    val.set(10);
    lz.__settle(null);
    lz2.__settle(null);
    Assert.assertEquals(50, (int) lz2.get());
    lz2.__raiseInvalid();
    Assert.assertEquals(50, (int) lz2.get());
    lz.__insert(new JsonStreamReader("{}"));
    lz.__dump(null);
    lz.__patch(new JsonStreamReader("{}"));
    Assert.assertEquals(-1900333, lz.getGeneration());
    Assert.assertEquals(-1900333, lz2.getGeneration());
    lz.__forceSettle();
    Assert.assertEquals(-1900333, lz.getGeneration());
    lz.__raiseInvalid();
    lz.__forceSettle();
    Assert.assertEquals(42333092, lz.getGeneration());
  }

  @Test
  public void alive_with_parent() {
    MockRxParent parent = new MockRxParent();
    final var val = new RxLazy(parent, () -> 123, null);
    Assert.assertTrue(val.__raiseInvalid());
    parent.alive = false;
    Assert.assertFalse(val.__raiseInvalid());
  }

  @Test
  public void chase_id() {
    MockRecord root = new MockRecord(null) {
      @Override
      public int __id() {
        return 1000;
      }
    };
    MockRecord proxy = new MockRecord(root);
    final var lz = new RxLazy(proxy, () -> 123, null);
    Assert.assertEquals(65521001, lz.getGeneration());
  }

  @Test
  public void alive_without_parent() {
    final var val = new RxLazy(null, () -> 123, null);
    Assert.assertTrue(val.__raiseInvalid());
  }

  @Test
  public void trivial() {
    final var val = new RxInt32(null, 42);
    final var lz = new RxLazy<>(null, () -> val.get(), null);
    lz.__commit(null, null, null);
    lz.__revert();
  }
}
