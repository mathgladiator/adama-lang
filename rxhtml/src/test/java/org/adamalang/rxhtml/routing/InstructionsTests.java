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

public class InstructionsTests {
  @Test
  public void root() {
    Instructions instructions = Instructions.parse("/");
    Assert.assertEquals("/", instructions.normalized);
    Assert.assertEquals("/", instructions.formula);
    Assert.assertEquals("['fixed','']", instructions.javascript);
  }
  @Test
  public void simple() {
    Instructions instructions = Instructions.parse("/xyz/abc");
    Assert.assertEquals("/xyz/abc", instructions.normalized);
    Assert.assertEquals("/xyz/abc", instructions.formula);
    Assert.assertEquals("['fixed','xyz','fixed','abc']", instructions.javascript);
  }
  @Test
  public void simple_introduce() {
    Instructions instructions = Instructions.parse("xyz/abc");
    Assert.assertEquals("/xyz/abc", instructions.normalized);
    Assert.assertEquals("/xyz/abc", instructions.formula);
    Assert.assertEquals("['fixed','xyz','fixed','abc']", instructions.javascript);
  }
  @Test
  public void var_number() {
    Instructions instructions = Instructions.parse("/xyz/$s:double");
    Assert.assertEquals("/xyz/$number", instructions.normalized);
    Assert.assertEquals("/xyz/{s}", instructions.formula);
    Assert.assertEquals("['fixed','xyz','number','s']", instructions.javascript);
  }
  @Test
  public void var_text() {
    Instructions instructions = Instructions.parse("/xyz/$s:str");
    Assert.assertEquals("/xyz/$text", instructions.normalized);
    Assert.assertEquals("/xyz/{s}", instructions.formula);
    Assert.assertEquals("['fixed','xyz','text','s']", instructions.javascript);
  }
  @Test
  public void var_text_is_default() {
    Instructions instructions = Instructions.parse("/xyz/$s");
    Assert.assertEquals("/xyz/$text", instructions.normalized);
    Assert.assertEquals("/xyz/{s}", instructions.formula);
    Assert.assertEquals("['fixed','xyz','text','s']", instructions.javascript);
  }
  @Test
  public void var_ignore_star() {
    Instructions instructions = Instructions.parse("/xyz/$s*/path");
    Assert.assertEquals("/xyz/$text/path", instructions.normalized);
    Assert.assertEquals("/xyz/{s}/path", instructions.formula);
    Assert.assertEquals("['fixed','xyz','text','s','fixed','path']", instructions.javascript);
  }
  @Test
  public void suffix() {
    Instructions instructions = Instructions.parse("/xyz/$s*");
    Assert.assertEquals("/xyz/$text", instructions.normalized);
    Assert.assertEquals("/xyz/{s}", instructions.formula);
    Assert.assertEquals("['fixed','xyz','suffix','s']", instructions.javascript);
  }
}
