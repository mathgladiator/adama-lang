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
