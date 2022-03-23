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
