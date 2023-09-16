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

import org.adamalang.net.client.routing.cache.SpaceState;
import org.junit.Assert;
import org.junit.Test;

import java.util.TreeSet;

public class SpaceStateTests {
  @Test
  public void flow() {
    SpaceState state = new SpaceState();
    state.add("x");
    Assert.assertTrue(state.list().contains("x"));
    Assert.assertTrue(state.subtract("x"));
    Assert.assertNull(state.pick("key"));
    state.add("y");
    state.add("z");
    state.add("t");
    TreeSet<String> targets = state.list();
    Assert.assertTrue(targets.contains("y"));
    Assert.assertTrue(targets.contains("z"));
    Assert.assertTrue(targets.contains("t"));
    Assert.assertFalse(targets.contains("x"));
  }
}
