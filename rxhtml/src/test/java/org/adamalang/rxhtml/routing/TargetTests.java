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
package org.adamalang.rxhtml.routing;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

public class TargetTests {
  @Test
  public void flow_null() {
    Target target = new Target(100, null, null, null);
    Assert.assertEquals(64, target.memory());
  }

  @Test
  public void flow_good() {
    Target target = new Target(100, new TreeMap<>(), "xyz".getBytes(StandardCharsets.UTF_8), null);
    Assert.assertEquals(131, target.memory());
  }
}
