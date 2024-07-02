/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.common.net;

import org.junit.Assert;
import org.junit.Test;

public class ChannelCommonTests {

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

  public static class DemoChannel extends ChannelCommon {
    public DemoChannel() {
      super(1, null);
      streams.put(3, null);
    }
  }
}
