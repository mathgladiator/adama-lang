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

import java.util.TreeMap;

public class TableTests {
  @Test
  public void flow() {
    Table table = new Table();
    table.add(Instructions.parse("/xyz/$n:number/$t:text/$z*"), new Target(100, null, null, null));
    TreeMap<String, String> captures = new TreeMap<>();
    Target target = table.route("/xyz/123/hi/there/joe", captures);
    Assert.assertEquals(100, target.status);
    Assert.assertEquals("123", captures.get("n"));
    Assert.assertEquals("hi", captures.get("t"));
    Assert.assertEquals("there/joe", captures.get("z"));
    Assert.assertEquals(340, table.measure());
  }
}
