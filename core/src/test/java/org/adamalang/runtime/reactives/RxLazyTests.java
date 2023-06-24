/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.mocks.MockRxParent;
import org.junit.Assert;
import org.junit.Test;

public class RxLazyTests {
  @Test
  public void flow() {
    final var val = new RxInt32(null, 42);
    final var lz = new RxLazy<>(null, () -> val.get() * val.get());
    Assert.assertEquals(2, lz.getGeneration());
    final var lz2 = new RxLazy<>(null, () -> lz.get() / 2);
    val.__subscribe(lz);
    lz.__subscribe(lz2);
    val.set(4);
    Assert.assertEquals(3, lz.getGeneration());
    Assert.assertEquals(16, (int) lz.get());
    Assert.assertEquals(8, (int) lz2.get());
    val.set(6);
    Assert.assertEquals(4, lz.getGeneration());
    Assert.assertEquals(18, (int) lz2.get());
    Assert.assertEquals(36, (int) lz.get());
    Assert.assertEquals(18, (int) lz2.get());
    Assert.assertEquals(36, (int) lz.get());
    val.set(10);
    Assert.assertEquals(5, lz.getGeneration());
    Assert.assertEquals(50, (int) lz2.get());
    lz.__insert(new JsonStreamReader("{}"));
    lz.__dump(null);
    lz.__patch(new JsonStreamReader("{}"));
  }

  @Test
  public void alive_with_parent() {
    MockRxParent parent = new MockRxParent();
    final var val = new RxLazy(parent, () -> 123);
    Assert.assertTrue(val.__raiseInvalid());
    parent.alive = false;
    Assert.assertFalse(val.__raiseInvalid());
  }

  @Test
  public void alive_without_parent() {
    final var val = new RxLazy(null, () -> 123);
    Assert.assertTrue(val.__raiseInvalid());
  }

  @Test
  public void trivial() {
    final var val = new RxInt32(null, 42);
    final var lz = new RxLazy<>(null, () -> val.get());
    lz.__commit(null, null, null);
    lz.__revert();
  }
}
