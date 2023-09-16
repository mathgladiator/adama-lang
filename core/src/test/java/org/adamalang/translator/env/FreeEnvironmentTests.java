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
package org.adamalang.translator.env;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class FreeEnvironmentTests {
  @Test
  public void simple() {
    FreeEnvironment environment = FreeEnvironment.root();
    environment.define("x");
    environment.require("x");
    environment.require("y");
    Assert.assertEquals("free:y", environment.toString());
  }

  @Test
  public void record() {
    HashMap<String, String> translate = new HashMap<>();
    translate.put("z", "R::u");
    FreeEnvironment environment = FreeEnvironment.record(translate).push();
    environment.define("x");
    environment.require("x");
    environment.require("z");
    Assert.assertEquals("free:R::u", environment.toString());
  }

  @Test
  public void simple_scope() {
    FreeEnvironment environment = FreeEnvironment.root();
    environment.define("x");
    environment.require("x");
    FreeEnvironment child = environment.push();
    child.define("y");
    environment.require("y");
    Assert.assertEquals("free:y", environment.toString());
  }

  @Test
  public void record_scope() {
    HashMap<String, String> translate = new HashMap<>();
    translate.put("z", "R::u");
    FreeEnvironment environment = FreeEnvironment.record(translate).push();
    environment.define("x");
    environment.require("x");
    environment.require("z");
    Assert.assertEquals("free:R::u", environment.toString());
  }
}
