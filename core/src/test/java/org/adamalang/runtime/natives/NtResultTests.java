/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.natives;

import org.adamalang.runtime.exceptions.ComputeBlockedException;
import org.junit.Assert;
import org.junit.Test;

public class NtResultTests {
  @Test
  public void good() {
    NtResult<Integer> result = new NtResult<>(123, false, 0, null);
    Assert.assertEquals(123, (int) result.get());
    Assert.assertEquals(123, (int) result.as_maybe().get());
    Assert.assertTrue(result.finished());
    Assert.assertTrue(result.has());
    Assert.assertFalse(result.failed());
    Assert.assertEquals("OK", result.message());
    Assert.assertEquals(0, result.code());
    Assert.assertEquals(123, (int) result.await().get());
  }

  @Test
  public void inprogress() {
    NtResult<Integer> result = new NtResult<>(null, false, 0, null);
    Assert.assertFalse(result.as_maybe().has());
    Assert.assertFalse(result.finished());
    Assert.assertFalse(result.failed());
    Assert.assertFalse(result.has());
    Assert.assertEquals("waiting...", result.message());
    Assert.assertEquals(0, result.code());
    try {
      result.await();
      Assert.fail();
    } catch (ComputeBlockedException cbe) {

    }
  }

  @Test
  public void bad() {
    NtResult<Integer> result = new NtResult<>(null, true, 500, "Failure");
    Assert.assertFalse(result.as_maybe().has());
    Assert.assertTrue(result.finished());
    Assert.assertTrue(result.failed());
    Assert.assertFalse(result.has());
    Assert.assertEquals("Failure", result.message());
    Assert.assertEquals(500, result.code());
    Assert.assertFalse(result.await().has());
  }
}
