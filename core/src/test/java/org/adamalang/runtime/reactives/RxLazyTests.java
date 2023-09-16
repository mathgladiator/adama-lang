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
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.mocks.MockRxParent;
import org.junit.Assert;
import org.junit.Test;

public class RxLazyTests {
  @Test
  public void flow() {
    final var val = new RxInt32(null, 42);
    final var lz = new RxLazy<>(null, () -> val.get() * val.get());
    final var lz2 = new RxLazy<>(null, () -> lz.get() / 2);
    Assert.assertEquals(65522, lz.getGeneration());
    Assert.assertEquals(65522, lz2.getGeneration());
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
    Assert.assertEquals(-1900333, lz.getGeneration());
    Assert.assertEquals(-1900333, lz2.getGeneration());
  }

  @Test
  public void flow_with_commit() {
    final var val = new RxInt32(null, 42);
    final var lz = new RxLazy<>(null, () -> val.get() * val.get());
    final var lz2 = new RxLazy<>(null, () -> lz.get() / 2);
    Assert.assertEquals(65522, lz.getGeneration());
    Assert.assertEquals(65522, lz2.getGeneration());
    val.__subscribe(lz);
    lz.__subscribe(lz2);
    val.set(4);
    Assert.assertEquals(16, (int) lz.get());
    Assert.assertEquals(8, (int) lz2.get());
    JsonStreamWriter f = new JsonStreamWriter();
    JsonStreamWriter r = new JsonStreamWriter();
    val.__commit("val", f, r);
    lz.dropInvalid();
    lz2.dropInvalid();
    Assert.assertEquals("\"val\":4", f.toString());
    Assert.assertEquals("\"val\":42", r.toString());
    Assert.assertEquals(42333092, lz.getGeneration());
    Assert.assertEquals(42333092, lz2.getGeneration());
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
    Assert.assertEquals(-842352283, lz.getGeneration());
    Assert.assertEquals(-842352283, lz2.getGeneration());
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
