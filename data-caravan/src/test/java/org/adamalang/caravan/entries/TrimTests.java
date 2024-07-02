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
    Assert.assertEquals(3, trim2.maxSize);
  }
}
