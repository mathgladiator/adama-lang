/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.natives;

import org.adamalang.runtime.mocks.MockCanGetAndSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

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
}
