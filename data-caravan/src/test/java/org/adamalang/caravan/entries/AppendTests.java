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
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class AppendTests {
  @Test
  public void flow() {
    Append append = new Append(123l, 500, "xyz".getBytes(StandardCharsets.UTF_8), 712, 10248L);
    ByteBuf buf = Unpooled.buffer();
    append.write(buf);
    Assert.assertEquals(0x42, buf.readByte());
    Append append2 = Append.readAfterTypeId(buf);
    Assert.assertEquals(123L, append2.id);
    Assert.assertEquals(500, append2.position);
    Assert.assertEquals("xyz", new String(append2.bytes, StandardCharsets.UTF_8));
    Assert.assertEquals(712, append2.seq);
    Assert.assertEquals(10248L, append2.assetBytes);
  }
}
