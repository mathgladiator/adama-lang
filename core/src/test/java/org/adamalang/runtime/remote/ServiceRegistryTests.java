/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.remote;

import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.natives.NtResult;
import org.adamalang.runtime.natives.NtToDynamic;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Function;

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

  class DumbXYZ implements Service {
    @Override
    public <T> NtResult<T> invoke(Caller caller, String method, RxCache cache, NtPrincipal agent, NtToDynamic request, Function<String, T> result) {
      return null;
    }
  }

  @Test
  public void nulldef() {
    ServiceRegistry.add("xyz", DumbXYZ.class, (x, y) -> new DumbXYZ());
    Assert.assertNull(ServiceRegistry.getLinkDefinition("xyz", 1, "{}", new HashSet<>(), (err) -> {}));
  }
}
