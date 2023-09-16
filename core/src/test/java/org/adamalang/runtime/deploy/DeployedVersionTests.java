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
package org.adamalang.runtime.deploy;

import org.adamalang.runtime.json.JsonStreamReader;
import org.junit.Assert;
import org.junit.Test;

public class DeployedVersionTests {
  @Test
  public void upgrade() {
    DeployedVersion justString = new DeployedVersion(new JsonStreamReader("\"main\""));
    Assert.assertEquals("main", justString.main);
    justString.hashCode();
  }

  @Test
  public void flow1() {
    DeployedVersion v = new DeployedVersion(new JsonStreamReader("{\"main\":\"xyz\",\"junk\":true,\"includes\":{\"x\":\"y\"}}"));
    Assert.assertEquals("xyz", v.main);
    Assert.assertTrue(v.includes.containsKey("x"));
    Assert.assertEquals("y", v.includes.get("x"));
    v.hashCode();
  }

  @Test
  public void flow2() {
    DeployedVersion v = new DeployedVersion(new JsonStreamReader("{\"main\":\"xyz\",\"junk\":true,\"includes\":{\"x\":\"y\"}}"));
    Assert.assertEquals("xyz", v.main);
    Assert.assertTrue(v.includes.containsKey("x"));
    Assert.assertEquals("y", v.includes.get("x"));
    v.hashCode();
  }
}
