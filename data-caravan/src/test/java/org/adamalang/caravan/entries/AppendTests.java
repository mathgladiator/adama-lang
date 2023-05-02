/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.caravan.entries;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class AppendTests {
  @Test
  public void flow() {
    Append append = new Append(123l, 500, "xyz".getBytes(StandardCharsets.UTF_8), 712, 10248L);
    ByteBuf buf = Unpooled.buffer();
    append.write(buf);
    Assert.assertEquals(0x42, buf.readByte());
    Append append2 = Append.readAfterTypeId(buf);
    Assert.assertEquals(123L, append2.id);
    Assert.assertEquals(500, append2.position);
    Assert.assertEquals("xyz", new String(append2.bytes, StandardCharsets.UTF_8));
    Assert.assertEquals(712, append2.seq);
    Assert.assertEquals(10248L, append2.assetBytes);
  }
}
