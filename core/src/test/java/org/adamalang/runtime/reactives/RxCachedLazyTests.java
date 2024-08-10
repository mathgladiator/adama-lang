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
import org.adamalang.runtime.mocks.MockRecord;
import org.adamalang.runtime.mocks.MockRxParent;
import org.junit.Assert;
import org.junit.Test;

public class RxCachedLazyTests {
  @Test
  public void flow() {
    final RxInt64 time = new RxInt64(null, 0L);
    final var val = new RxInt32(null, 42);
    final var clz = new RxCachedLazy<>(null, () -> val.get() * val.get(), null, 1, time);
    Assert.assertTrue(clz.alive());
    clz.__commit(null, null, null);
    clz.__revert();
    clz.__insert(new JsonStreamReader("{}"));
    clz.__patch(new JsonStreamReader("{}"));
    clz.__dump(new JsonStreamWriter());
    Assert.assertTrue(clz.__raiseInvalid());
    Assert.assertEquals(1764, (int) clz.get());
    val.set(100);
    Assert.assertEquals(1764, (int) clz.get());
    Assert.assertEquals(115579045, clz.getGeneration());
    clz.__settle(null);
    Assert.assertEquals(1764, (int) clz.get());
    Assert.assertEquals(115579045, clz.getGeneration());
    time.set(100000);
    clz.__settle(null);
    Assert.assertEquals(10000, (int) clz.get());
    Assert.assertEquals(716450039, clz.getGeneration());
  }

  @Test
  public void mirror_parent_life() {
    final RxInt64 time = new RxInt64(null, 0L);
    final var val = new RxInt32(null, 42);
    MockRxParent p = new MockRxParent();
    final var clz = new RxCachedLazy<>(p, () -> val.get() * val.get(), null, 1, time);
    Assert.assertTrue(clz.alive());
    p.alive = false;
    Assert.assertFalse(clz.alive());
  }

  @Test
  public void flow_parent_and_perf() {
    MockRxParent parent = new MockRxParent();
    final RxInt64 time = new RxInt64(null, 0L);
    final var val = new RxInt32(null, 42);
    final var clz = new RxCachedLazy<>(parent, () -> val.get() * val.get(), () -> (() -> {}), 1, time);
    Assert.assertTrue(clz.alive());
    Assert.assertTrue(clz.__raiseInvalid());
    clz.__commit(null, null, null);
    clz.__revert();
    clz.__insert(new JsonStreamReader("{}"));
    clz.__patch(new JsonStreamReader("{}"));
    clz.__dump(new JsonStreamWriter());
    parent.alive = false;
    Assert.assertFalse(clz.alive());
    Assert.assertFalse(clz.__raiseInvalid());
    Assert.assertEquals(1764, (int) clz.get());
    val.set(100);
    Assert.assertEquals(1764, (int) clz.get());
    Assert.assertEquals(115579045, clz.getGeneration());
    time.set(100000);
    clz.__settle(null);
    Assert.assertEquals(10000, (int) clz.get());
    Assert.assertEquals(716450039, clz.getGeneration());
  }

  @Test
  public void init_record() {
    MockRecord record = new MockRecord(new MockRxParent());
    record.id = 123;
    final RxInt64 time = new RxInt64(null, 0L);
    final var val = new RxInt32(null, 42);
    final var clz = new RxCachedLazy<>(record, () -> val.get() * val.get(), () -> (() -> {}), 1, time);
    Assert.assertEquals(8059084, clz.getGeneration());
    Assert.assertEquals(1764, (int) clz.get());
    Assert.assertEquals(-241734643, clz.getGeneration());
  }
}
