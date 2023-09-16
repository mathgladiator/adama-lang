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
package org.adamalang.runtime.sys.capacity;

import org.junit.Assert;
import org.junit.Test;

public class CapacityInstanceTests {
  @Test
  public void trivial() {
    CapacityInstance ci = new CapacityInstance("space", "region", "machine", true);
    Assert.assertEquals("space", ci.space);
    Assert.assertEquals("region", ci.region);
    Assert.assertEquals("machine", ci.machine);
    Assert.assertTrue(ci.override);
    Assert.assertEquals(ci, ci);
    Assert.assertEquals(1172816919, ci.hashCode());
    Assert.assertEquals(0, ci.compareTo(ci));
    Assert.assertFalse(ci.equals(""));
    Assert.assertFalse(ci.equals(null));
    Assert.assertTrue(ci.equals(new CapacityInstance("space", "region", "machine", true)));
  }

  @Test
  public void ordering() {
    {
      CapacityInstance A = new CapacityInstance("space", "regionA", "machine", true);
      CapacityInstance B = new CapacityInstance("space", "regionB", "machine", true);
      Assert.assertEquals(-1, A.compareTo(B));
      Assert.assertEquals(1, B.compareTo(A));
    }

    {
      CapacityInstance A = new CapacityInstance("space", "region", "machineA", true);
      CapacityInstance B = new CapacityInstance("space", "region", "machineB", true);
      Assert.assertEquals(-1, A.compareTo(B));
      Assert.assertEquals(1, B.compareTo(A));
    }

    {
      CapacityInstance A = new CapacityInstance("spaceA", "region", "machine", true);
      CapacityInstance B = new CapacityInstance("spaceB", "region", "machine", true);
      Assert.assertEquals(-1, A.compareTo(B));
      Assert.assertEquals(1, B.compareTo(A));
    }

  }
}
