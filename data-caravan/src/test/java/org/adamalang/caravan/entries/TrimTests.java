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
    Assert.assertEquals(3, trim2.count);
  }
}
