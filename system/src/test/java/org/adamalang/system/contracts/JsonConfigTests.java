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
