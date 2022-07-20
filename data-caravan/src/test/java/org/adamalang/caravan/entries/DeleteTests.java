/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.caravan.entries;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Assert;
import org.junit.Test;

public class DeleteTests {
  @Test
  public void flow() {
    Delete delete = new Delete(123l);
    ByteBuf buf = Unpooled.buffer();
    delete.write(buf);
    Assert.assertEquals(0x66, buf.readByte());
    Delete delete2 = Delete.readAfterTypeId(buf);
    Assert.assertEquals(123L, delete2.id);
  }
}
