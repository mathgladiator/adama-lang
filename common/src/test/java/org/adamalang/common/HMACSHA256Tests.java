/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common;

import org.junit.Assert;
import org.junit.Test;

public class HMACSHA256Tests {
  @Test
  public void flow() {
    byte[] key = "AWS4BlahBlahBlah".getBytes();
    Assert.assertEquals("d26f8c02690af9c24c06c78f7e45dbd6c3467bb2db9be10b2507caad52ea9780", Hex.of(HMACSHA256.of(key, "OK")));
    try {
      HMACSHA256.of("".getBytes(), "OK");
      Assert.fail();
    } catch (IllegalArgumentException iae) {

    }
  }
}
