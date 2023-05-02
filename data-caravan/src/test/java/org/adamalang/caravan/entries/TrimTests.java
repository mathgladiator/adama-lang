/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.caravan.entries;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Assert;
import org.junit.Test;

public class TrimTests {
  @Test
  public void flow1() {
    Trim trim = new Trim(4, 3);
    ByteBuf buf = Unpooled.buffer();
    trim.write(buf);
    Assert.assertEquals(0x13, buf.readByte());
    Trim trim2 = Trim.readAfterTypeId(buf);
    Assert.assertEquals(4L, trim2.id);
    Assert.assertEquals(3, trim2.maxSize);
  }
}
