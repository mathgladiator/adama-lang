/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
