/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
