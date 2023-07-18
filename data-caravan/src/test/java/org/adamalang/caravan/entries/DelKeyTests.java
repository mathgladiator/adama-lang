/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.caravan.entries;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.adamalang.runtime.data.Key;
import org.junit.Assert;
import org.junit.Test;

public class DelKeyTests {
  @Test
  public void flow() {
    DelKey mk1 = new DelKey(new Key("space", "key"));
    ByteBuf buf = Unpooled.buffer();
    mk1.write(buf);
    Assert.assertEquals(0x36, buf.readByte());
    DelKey mk2 = DelKey.readAfterTypeId(buf);
    Assert.assertEquals("space", mk2.of().space);
    Assert.assertEquals("key", mk2.of().key);
  }
}
