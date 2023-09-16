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
package org.adamalang.net.client.routing.cache;

import org.adamalang.common.SimpleExecutor;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

public class RoutingTableTargetTests {
  @Test
  public void flow() throws Exception {
    RoutingTableTarget router = new RoutingTableTarget(SimpleExecutor.NOW);
    router.integrate("t1", Collections.singleton("space"));
    router.integrate("t2", Collections.singleton("space"));
    router.integrate("t3", Collections.singleton("a"));

    for (int k = 0; k < 1000; k++) {
      Assert.assertEquals("t3", router.table.get("a", "x-" + k));
    }
    Assert.assertEquals("t1", router.table.get("space", "a"));
    Assert.assertEquals("t2", router.table.get("space", "b"));
    Assert.assertEquals("t1", router.table.get("space", "c"));
    Assert.assertEquals("t2", router.table.get("space", "d"));

    router.remove("t1");

    Assert.assertEquals("t2", router.table.get("space", "a"));
    Assert.assertEquals("t2", router.table.get("space", "b"));
    Assert.assertEquals("t2", router.table.get("space", "c"));
    Assert.assertEquals("t2", router.table.get("space", "d"));
  }
}
