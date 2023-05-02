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

import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class OutstandingFutureTests {
  @Test
  public void flow() {
    final var fut = new OutstandingFuture(1, "ch", NtPrincipal.NO_ONE);
    Assert.assertFalse(fut.test("ch", NtPrincipal.NO_ONE));
    Assert.assertTrue(fut.outstanding());
    fut.take();
    Assert.assertFalse(fut.outstanding());
    fut.reset();
    Assert.assertFalse(fut.outstanding());
    Assert.assertTrue(fut.test("ch", NtPrincipal.NO_ONE));
    Assert.assertFalse(fut.test("ch2", NtPrincipal.NO_ONE));
    Assert.assertTrue(fut.outstanding());
    Assert.assertFalse(fut.test("ch", NtPrincipal.NO_ONE));
    Assert.assertFalse(fut.test("ch2", NtPrincipal.NO_ONE));
  }
}
