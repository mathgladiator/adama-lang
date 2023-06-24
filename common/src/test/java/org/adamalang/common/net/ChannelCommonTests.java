/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common.net;

import org.junit.Assert;
import org.junit.Test;

public class ChannelCommonTests {

  public static class DemoChannel extends ChannelCommon {
    public DemoChannel() {
      super(1, null);
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
