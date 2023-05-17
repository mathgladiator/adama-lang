/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.remote;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class ServiceRegistryTests {
  @Test
  public void flow() {
    ServiceRegistry registry = new ServiceRegistry();
    Assert.assertFalse(registry.contains("xyz"));
    HashMap<String, HashMap<String, Object>> config = new HashMap<>();
    config.put("xyz", new HashMap<>());
    registry.resolve("space", config);
    Assert.assertTrue(registry.contains("xyz"));
    Assert.assertTrue(registry.find("nooop") == Service.FAILURE);
    Assert.assertFalse(ServiceRegistry.NOT_READY.contains("x"));
    ServiceRegistry.NOT_READY.resolve("x", null);
  }

  @Test
  public void static_reg() {
    ServiceRegistry.add("xyx", ServiceRegistryTests.class, null);
    Assert.assertNull(ServiceRegistry.getLinkDefinition("xyz", 12, null, null, null));
  }
}
