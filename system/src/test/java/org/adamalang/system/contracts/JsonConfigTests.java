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
package org.adamalang.system.contracts;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.runtime.sys.ServiceHeatEstimator;
import org.junit.Assert;
import org.junit.Test;

public class JsonConfigTests {
  @Test
  public void get_string() {
    ObjectNode node = Json.newJsonObject();
    node.put("xyz", "abc");
    JsonConfig config = new JsonConfig(node);
    Assert.assertEquals("abc", config.get_string("xyz", "x"));
    Assert.assertEquals("x", config.get_string("z", "x"));
    try {
      Assert.assertEquals("x", config.get_string("z", null));
      Assert.fail();
    } catch (RuntimeException re) {
      Assert.assertEquals("expected an 'z' within the config", re.getMessage());
    }
    try {
      config.get_string("identity", null);
      Assert.fail();
    } catch (JsonConfig.BadException be) {
    }
  }

  @Test
  public void get_int() {
    ObjectNode node = Json.newJsonObject();
    node.put("xyz", 1);
    node.put("abc", "1");
    JsonConfig config = new JsonConfig(node);
    Assert.assertEquals(1, config.get_int("xyz", 42));
    Assert.assertEquals(42, config.get_int("abc", 42));
  }

  @Test
  public void get_heat() {
    ObjectNode node = Json.newJsonObject();
    node.put("xyz", 1);
    node.put("abc", "1");
    node.put("x-cpu-m", 223);
    JsonConfig config = new JsonConfig(node);
    ServiceHeatEstimator.HeatVector hv = config.get_heat("x", 1000, 50, 2, 5);
    Assert.assertEquals(223000000, hv.cpu);
  }

  @Test
  public void get_or_create_child() {
    ObjectNode node = Json.newJsonObject();
    node.putObject("child").put("x", 1);
    JsonConfig config = new JsonConfig(node);
    Assert.assertTrue(config.get_or_create_child("child").has("x"));
    Assert.assertNotNull(config.get_or_create_child("nope"));
  }

  @Test
  public void get_str_list() {
    ObjectNode node = Json.newJsonObject();
    node.putArray("yo").add("1");
    JsonConfig config = new JsonConfig(node);
    Assert.assertEquals(1, config.get_str_list("yo").size());
    Assert.assertEquals(0, config.get_str_list("nope").size());
  }
}
