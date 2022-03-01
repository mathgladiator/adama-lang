package org.adamalang.common.net;

import org.junit.Assert;
import org.junit.Test;

public class ChannelCommonTests {

  public static class DemoChannel extends ChannelCommon {
    public DemoChannel() {
      super(1);
      streams.put(3, null);
    }
  }

  @Test
  public void edges() {
    DemoChannel channel = new DemoChannel();
    Assert.assertEquals(1, channel.makeId());
    Assert.assertEquals(5, channel.makeId());
    Assert.assertEquals(7, channel.makeId());
    for (int k = 0; k < 2097152 - 7; k++) {
      channel.makeId();
    }
    Assert.assertEquals(4194299, channel.makeId());
    Assert.assertEquals(4194301, channel.makeId());
    Assert.assertEquals(4194303, channel.makeId());
    Assert.assertEquals(1, channel.makeId());
    Assert.assertEquals(5, channel.makeId());
  }
}
