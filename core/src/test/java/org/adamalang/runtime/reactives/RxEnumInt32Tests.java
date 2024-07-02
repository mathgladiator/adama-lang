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
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.json.JsonStreamReader;
import org.junit.Assert;
import org.junit.Test;

public class RxEnumInt32Tests {
  @Test
  public void flow() {
    final var iv = new RxEnumInt32(null, 1, (v) -> {
      if (v < 3) {
        return v;
      }
      return 3;
    });
    iv.__insert(new JsonStreamReader("45"));
    Assert.assertEquals(3, (int) iv.get());
    iv.set(100);
    Assert.assertEquals(3, (int) iv.get());
    iv.__patch(new JsonStreamReader("45"));
    Assert.assertEquals(3, (int) iv.get());
    iv.forceSet(1000);
    Assert.assertEquals(3, (int) iv.get());
  }
}
