/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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
    registry.resolve(config);
    Assert.assertTrue(registry.contains("xyz"));
    Assert.assertTrue(registry.find("nooop") == Service.FAILURE);
  }
}
