/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.natives;

import org.junit.Assert;
import org.junit.Test;

public class NtPairTests {
  @Test
  public void coverage() {
    NtPair<Integer, Integer> p1 = new NtPair<>(1, 2);
    NtPair<Integer, Integer> p2 = new NtPair<>(p1);
    Assert.assertEquals(1, (int) p1.key);
    Assert.assertEquals(2, (int) p1.value);
    Assert.assertEquals(1, (int) p2.key);
    Assert.assertEquals(2, (int) p2.value);
  }
}
