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
