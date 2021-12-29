/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.async;

import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

public class OutstandingFutureTests {
  @Test
  public void flow() {
    final var fut = new OutstandingFuture(1, "ch", NtClient.NO_ONE);
    Assert.assertFalse(fut.test("ch", NtClient.NO_ONE));
    Assert.assertTrue(fut.outstanding());
    fut.take();
    Assert.assertFalse(fut.outstanding());
    fut.reset();
    Assert.assertFalse(fut.outstanding());
    Assert.assertTrue(fut.test("ch", NtClient.NO_ONE));
    Assert.assertFalse(fut.test("ch2", NtClient.NO_ONE));
    Assert.assertTrue(fut.outstanding());
    Assert.assertFalse(fut.test("ch", NtClient.NO_ONE));
    Assert.assertFalse(fut.test("ch2", NtClient.NO_ONE));
  }
}
