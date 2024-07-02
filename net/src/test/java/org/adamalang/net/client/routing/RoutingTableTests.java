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
package org.adamalang.net.client.routing;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

public class RoutingTableTests {
  @Test
  public void flow() {
    RoutingTable table = new RoutingTable();
    Assert.assertNull(table.random());
    Assert.assertEquals(0, table.targetsFor("space").size());
    table.integrate("t1", Collections.singleton("space"));
    Assert.assertEquals("t1", table.random());
    Assert.assertEquals("t1", table.get("space", "key"));
    Assert.assertEquals(1, table.targetsFor("space").size());
    table.remove("t1");
    Assert.assertNull(table.random());
    Assert.assertEquals(0, table.targetsFor("space").size());
    table.integrate("t1", Collections.singleton("space"));
    Assert.assertEquals(1, table.targetsFor("space").size());
    table.integrate("t1", Collections.emptyList());
    Assert.assertEquals(0, table.targetsFor("space").size());
    table.integrate("t1", Collections.singleton("space"));
    table.integrate("t2", Collections.singleton("space"));
    table.integrate("t3", Collections.singleton("space"));
    Assert.assertEquals(3, table.targetsFor("space").size());
    Assert.assertEquals("t2", table.get("space", "key1"));
    Assert.assertEquals("t1", table.get("space", "key2"));
    Assert.assertEquals("t1", table.get("space", "key3"));
    Assert.assertEquals("t1", table.get("space", "key4"));
    Assert.assertEquals("t3", table.get("space", "key5"));
    Assert.assertEquals("t1", table.get("space", "key6"));
    Assert.assertEquals("t2", table.get("space", "key7"));
    Assert.assertEquals("t3", table.get("space", "key8"));
    table.integrate("t1", Collections.emptyList());
    table.integrate("t2", Collections.emptyList());
    table.integrate("t3", Collections.emptyList());

    Assert.assertEquals("t3", table.pick("booom"));
    Assert.assertEquals("t1", table.pick("booooom"));
    Assert.assertEquals("t2", table.pick("boooooooooooooom chaka"));
  }
}
