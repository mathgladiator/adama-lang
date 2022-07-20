/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.json.JsonStreamReader;
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
  public void trivial() {
    final var val = new RxInt32(null, 42);
    final var lz = new RxLazy<>(null, () -> val.get());
    lz.__commit(null, null, null);
    lz.__revert();
  }
}
