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
package org.adamalang.runtime.natives;

import org.adamalang.runtime.mocks.MockCanGetAndSet;
import org.adamalang.runtime.stdlib.LibMath;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class NtMaybeTests {
  @Test
  public void flow() {
    final var maybe = new NtMaybe<Integer>();
    Assert.assertFalse(maybe.has());
    maybe.set(123);
    Assert.assertTrue(maybe.has());
    Assert.assertEquals(123, (int) maybe.get());
    final var copy = new NtMaybe<>(maybe);
    Assert.assertTrue(copy.has());
    Assert.assertEquals(123, (int) copy.get());
    Assert.assertTrue(maybe == maybe.resolve());
  }

  @Test
  public void set_chain() {
    final var cgs = new MockCanGetAndSet<Integer>();
    final var maybe = new NtMaybe<Integer>().withAssignChain(cgs);
    Assert.assertFalse(maybe.has());
    maybe.set(123);
    Assert.assertEquals(123, (int) cgs.get());
    maybe.set(new NtMaybe<>(52));
    Assert.assertEquals(52, (int) cgs.get());
  }

  @Test
  public void sort() {
    final var ints = new ArrayList<NtMaybe<Integer>>();
    ints.add(new NtMaybe<>());
    ints.add(new NtMaybe<>(10000));
    ints.add(new NtMaybe<>());
    ints.add(new NtMaybe<>(123));
    ints.add(new NtMaybe<>());
    ints.add(new NtMaybe<>(1000));
    ints.add(new NtMaybe<>());
    ints.add(new NtMaybe<>(12));
    ints.add(new NtMaybe<>());
    ints.add(new NtMaybe<>());
    ints.add(new NtMaybe<>());
    ints.sort((a, b) -> a.compareValues(b, Integer::compareTo));
    Assert.assertEquals(12, (int) ints.get(0).get());
    Assert.assertEquals(123, (int) ints.get(1).get());
    Assert.assertEquals(1000, (int) ints.get(2).get());
    Assert.assertEquals(10000, (int) ints.get(3).get());
    Assert.assertEquals(null, ints.get(4).get());
  }

  @Test
  public void via_set() {
    final var maybe = new NtMaybe<>(123);
    final var copy = new NtMaybe<Integer>();
    copy.set(maybe);
    Assert.assertEquals(123, (int) copy.get());
    copy.delete();
    Assert.assertFalse(copy.has());
    Assert.assertTrue(maybe.has());
    Assert.assertEquals(123, (int) maybe.get());
  }

  @Test
  public void with_value() {
    final var maybe = new NtMaybe<>(123);
    final var copy = new NtMaybe<>(maybe);
    Assert.assertTrue(copy.has());
    Assert.assertEquals(123, (int) copy.get());
    copy.delete();
    Assert.assertFalse(copy.has());
    Assert.assertTrue(maybe.has());
    Assert.assertEquals(123, (int) maybe.get());
  }

  @Test
  public void delete_chain() {
    AtomicBoolean called = new AtomicBoolean(false);
    final var maybe = new NtMaybe<>(123);
    maybe.withDeleteChain(() -> {
      called.set(true);
    });
    Assert.assertEquals(123, (int) maybe.getOrDefaultTo(42));
    maybe.delete();
    Assert.assertTrue(called.get());
    Assert.assertEquals(42, (int) maybe.getOrDefaultTo(42));
  }

  @Test
  public void unpack() {
    final var maybe = new NtMaybe<>(123);
    Assert.assertEquals(123 * 123, (int) ((maybe.unpack((x) -> x * x).get())));
    Assert.assertEquals("123", maybe.toString());
    maybe.delete();
    Assert.assertFalse(maybe.unpack((x) -> x * x).has());
    Assert.assertEquals("", maybe.toString());
  }

  @Test
  public void unpackTransfer() {
    final var maybe = new NtMaybe<>(123);
    Assert.assertEquals(123 * 123, (int) ((maybe.unpackTransfer((x) -> new NtMaybe<>(x * x)).get())));
    Assert.assertEquals("123", maybe.toString());
    maybe.delete();
    Assert.assertFalse(maybe.unpackTransfer((x) -> new NtMaybe<>(x * x)).has());
    Assert.assertEquals("", maybe.toString());
  }

  @Test
  public void regression() {
    NtMaybe<Integer> i871 = new NtMaybe<>(871);
    Assert.assertTrue(LibMath.equality(i871, 871, (__x, __y) -> __x.intValue() == __y));
  }
}
