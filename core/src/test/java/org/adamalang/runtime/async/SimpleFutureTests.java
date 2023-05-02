/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.async;

import org.adamalang.runtime.exceptions.ComputeBlockedException;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class SimpleFutureTests {
  @Test
  public void has_value() {
    final var sf = new SimpleFuture<>("chan", NtPrincipal.NO_ONE, "cake");
    Assert.assertTrue(sf.exists());
    Assert.assertEquals("cake", sf.await());
  }

  @Test
  public void no_value() {
    final var sf = new SimpleFuture<String>("chan", NtPrincipal.NO_ONE, null);
    Assert.assertFalse(sf.exists());
    try {
      sf.await();
      Assert.fail();
    } catch (final ComputeBlockedException cbe) {
    }
  }
}
