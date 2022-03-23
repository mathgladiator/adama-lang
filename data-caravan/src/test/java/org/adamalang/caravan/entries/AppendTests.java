package org.adamalang.caravan.entries;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class AppendTests {
  @Test
  public void flow() {
    Append append = new Append(123l, 500, "xyz".getBytes(StandardCharsets.UTF_8));
    ByteBuf buf = Unpooled.buffer();
    append.write(buf);
    Assert.assertEquals(0x42, buf.readByte());
    Append append2 = Append.readAfterTypeId(buf);
    Assert.assertEquals(123L, append2.id);
    Assert.assertEquals(500, append2.position);
    Assert.assertEquals("xyz", new String(append2.bytes, StandardCharsets.UTF_8));
  }
}
