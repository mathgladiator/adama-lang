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
package org.adamalang.rxhtml.codegen;

import org.junit.Assert;
import org.junit.Test;

public class VariablePoolTests {
  @Test
  public void flow() {
    VariablePool pool = new VariablePool();
    String a = pool.ask();
    Assert.assertEquals("a", a);
    String b = pool.ask();
    Assert.assertEquals("b", b);
    String c = pool.ask();
    Assert.assertEquals("c", c);
    pool.give(a);
    Assert.assertEquals("a", pool.ask());
    while (!pool.ask().equals("_if")) {
    }
    while (!pool.ask().equals("_do")) {
    }
  }
}
