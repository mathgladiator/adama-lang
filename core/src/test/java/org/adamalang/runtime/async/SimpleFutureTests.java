/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.async;

import org.adamalang.runtime.exceptions.ComputeBlockedException;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

public class SimpleFutureTests {
  @Test
  public void has_value() {
    final var sf = new SimpleFuture<>("chan", NtClient.NO_ONE, "cake");
    Assert.assertTrue(sf.exists());
    Assert.assertEquals("cake", sf.await());
  }

  @Test
  public void no_value() {
    final var sf = new SimpleFuture<String>("chan", NtClient.NO_ONE, null);
    Assert.assertFalse(sf.exists());
    try {
      sf.await();
      Assert.fail();
    } catch (final ComputeBlockedException cbe) {}
  }
}
