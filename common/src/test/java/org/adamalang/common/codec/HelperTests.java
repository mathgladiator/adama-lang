/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Assert;
import org.junit.Test;

public class HelperTests {
  @Test
  public void intarray() {
    ByteBuf buf = Unpooled.buffer();
    Helper.writeIntArray(buf, new int[] { 1, 2, 4});
    int[] arr = Helper.readIntArray(buf);
    Assert.assertEquals(1, arr[0]);
    Assert.assertEquals(2, arr[1]);
    Assert.assertEquals(4, arr[2]);
  }

  @Test
  public void intarray_null() {
    ByteBuf buf = Unpooled.buffer();
    Helper.writeIntArray(buf, null);
    Assert.assertNull(Helper.readIntArray(buf));
  }
}
