package org.adamalang.caravan.entries;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.adamalang.runtime.data.Key;
import org.junit.Assert;
import org.junit.Test;

public class MapKeyTests {
  @Test
  public void flow() {
    MapKey mk1 = new MapKey(new Key("space", "key"), 42);
    ByteBuf buf = Unpooled.buffer();
    mk1.write(buf);
    Assert.assertEquals(0x30, buf.readByte());
    MapKey mk2 = MapKey.readAfterTypeId(buf);
    Assert.assertEquals("space", mk2.of().space);
    Assert.assertEquals("key", mk2.of().key);
    Assert.assertEquals(42, mk2.id);
  }
}
