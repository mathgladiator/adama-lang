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
package org.adamalang.runtime.remote;

import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.natives.NtResult;
import org.adamalang.runtime.natives.NtToDynamic;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.function.Function;

public class ServiceRegistryTests {
  @Test
  public void flow() {
    ServiceRegistry registry = new ServiceRegistry();
    Assert.assertFalse(registry.contains("xyz"));
    HashMap<String, HashMap<String, Object>> config = new HashMap<>();
    config.put("xyz", new HashMap<>());
    registry.resolve("space", config, new TreeMap<>());
    Assert.assertTrue(registry.contains("xyz"));
    Assert.assertTrue(registry.find("nooop") == Service.FAILURE);
    Assert.assertFalse(ServiceRegistry.NOT_READY.contains("x"));
    ServiceRegistry.NOT_READY.resolve("x", null, null);
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
    ServiceRegistry.add("xyz", DumbXYZ.class, (x, y, z) -> new DumbXYZ());
    Assert.assertNull(ServiceRegistry.getLinkDefinition("xyz", 1, "{}", new HashSet<>(), (err) -> {}));
  }
}
