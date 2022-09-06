/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtDynamic;
import org.junit.Assert;
import org.junit.Test;

public class LibDynamicTests {
  @Test
  public void coverage() {
    Assert.assertEquals("{}", LibDynamic.toDynamic("{}").get().json);
    Assert.assertFalse(LibDynamic.toDynamic("x").has());
    Assert.assertEquals("here", LibDynamic.str(new NtDynamic("{\"x\":\"here\"}"), "x").get());
    Assert.assertEquals("123", LibDynamic.str(new NtDynamic("{\"x\":123}"), "x").get());
    Assert.assertEquals("123.4", LibDynamic.str(new NtDynamic("{\"x\":123.4}"), "x").get());
    Assert.assertFalse(LibDynamic.str(new NtDynamic("{}"), "x").has());
  }
}
