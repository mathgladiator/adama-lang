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
package org.adamalang.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Assert;
import org.junit.Test;

public class HelperTests {
  @Test
  public void intarray() {
    ByteBuf buf = Unpooled.buffer();
    Helper.writeIntArray(buf, new int[]{1, 2, 4});
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
