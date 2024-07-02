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
package org.adamalang.rxhtml.template;

import org.junit.Assert;
import org.junit.Test;

public class StatePathTests {
  @Test
  public void simple() {
    StatePath sp = StatePath.resolve("simple", "S");
    Assert.assertEquals("S", sp.command);
    Assert.assertEquals("simple", sp.name);
    Assert.assertTrue(sp.simple);
  }

  @Test
  public void view_simple() {
    StatePath sp = StatePath.resolve("view:name", "S");
    Assert.assertEquals("$.pV(S)", sp.command);
    Assert.assertEquals("name", sp.name);
    Assert.assertFalse(sp.simple);
  }

  @Test
  public void dive() {
    StatePath sp = StatePath.resolve("path/simple", "S");
    Assert.assertEquals("$.pI(S,'path')", sp.command);
    Assert.assertEquals("simple", sp.name);
    Assert.assertFalse(sp.simple);
  }

  @Test
  public void multidive() {
    StatePath sp = StatePath.resolve("path1/path2/name", "S");
    Assert.assertEquals("$.pI($.pI(S,'path1'),'path2')", sp.command);
    Assert.assertEquals("name", sp.name);
    Assert.assertFalse(sp.simple);
  }

  @Test
  public void multidive_with_dots1() {
    StatePath sp = StatePath.resolve("path1.path2/name", "S");
    Assert.assertEquals("$.pI($.pI(S,'path1'),'path2')", sp.command);
    Assert.assertEquals("name", sp.name);
    Assert.assertFalse(sp.simple);
  }

  @Test
  public void multidive_with_dots2() {
    StatePath sp = StatePath.resolve("path1.path2.name", "S");
    Assert.assertEquals("$.pI($.pI(S,'path1'),'path2')", sp.command);
    Assert.assertEquals("name", sp.name);
    Assert.assertFalse(sp.simple);
  }

  @Test
  public void multidive_with_dots3() {
    StatePath sp = StatePath.resolve("path1/path2.name", "S");
    Assert.assertEquals("$.pI($.pI(S,'path1'),'path2')", sp.command);
    Assert.assertEquals("name", sp.name);
    Assert.assertFalse(sp.simple);
  }

  @Test
  public void root() {
    StatePath sp = StatePath.resolve("/name", "S");
    Assert.assertEquals("$.pR(S)", sp.command);
    Assert.assertEquals("name", sp.name);
    Assert.assertFalse(sp.simple);
  }

  @Test
  public void root_down_up() {
    StatePath sp = StatePath.resolve("/down/../name", "S");
    Assert.assertEquals("$.pU($.pI($.pR(S),'down'))", sp.command);
    Assert.assertEquals("name", sp.name);
    Assert.assertFalse(sp.simple);
  }

  @Test
  public void dive_up() {
    StatePath sp = StatePath.resolve("path1/../name", "S");
    Assert.assertEquals("$.pU($.pI(S,'path1'))", sp.command);
    Assert.assertEquals("name", sp.name);
    Assert.assertFalse(sp.simple);
  }
}
