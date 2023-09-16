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
package org.adamalang.caravan.index;

import org.adamalang.caravan.entries.DelKey;
import org.adamalang.caravan.entries.MapKey;
import org.adamalang.runtime.data.Key;
import org.junit.Assert;
import org.junit.Test;

public class KeyMapTests {
  @Test
  public void flow() {
    KeyMap km = new KeyMap();
    {
      Key k1 = new Key("space", "key");
      MapKey mk1 = km.inventAndApply(k1);
      Assert.assertEquals(1, mk1.id);
      Assert.assertEquals(1, (int) km.get(k1));
      Assert.assertNull(km.inventAndApply(k1));
    }
    km.apply(new DelKey(new Key("space", "del")));
    km.apply(new DelKey(new Key("space", "key")));
    km.apply(new MapKey(new Key("s", "k"), 42));
    {
      Key k1 = new Key("space", "key");
      MapKey mk1 = km.inventAndApply(k1);
      Assert.assertEquals(43, mk1.id);
      Assert.assertEquals(43, (int) km.get(k1));
    }
    Assert.assertEquals(42, (int) km.get(new Key("s", "k")));
  }
}
