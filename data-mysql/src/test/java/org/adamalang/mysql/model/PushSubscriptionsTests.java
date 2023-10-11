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
package org.adamalang.mysql.model;

import org.adamalang.common.keys.VAPIDFactory;
import org.adamalang.common.keys.VAPIDPublicPrivateKeyPair;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.*;
import org.adamalang.mysql.data.DeviceSubscription;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

import java.security.SecureRandom;
import java.util.List;

public class PushSubscriptionsTests {
  @Test
  public void flow() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory()))) {
      Installer installer = new Installer(dataBase);
      try {
        installer.install();
        VAPIDFactory factory = new VAPIDFactory(new SecureRandom());
        VAPIDPublicPrivateKeyPair pair1 =  Domains.getOrCreateVapidKeyPair(dataBase, "www.domain.com", factory);
        VAPIDPublicPrivateKeyPair pair2 =  Domains.getOrCreateVapidKeyPair(dataBase, "www.domain.com", factory);
        Assert.assertEquals(pair1.privateKeyBase64, pair2.privateKeyBase64);
        PushSubscriptions.registerSubscription(dataBase, "www.domain.com", new NtPrincipal("1", "adama"), "sub-1", "device", System.currentTimeMillis() + 14 * 90000);
        PushSubscriptions.registerSubscription(dataBase, "www.domain.com", new NtPrincipal("1", "adama"), "sub-2", "device", System.currentTimeMillis() + 14 * 90000);
        List<DeviceSubscription> subs = PushSubscriptions.list(dataBase, "www.domain.com", new NtPrincipal("1", "adama"));
        Assert.assertEquals(2, subs.size());
        Assert.assertEquals("sub-1", subs.get(0).subscription);
        Assert.assertEquals("sub-2", subs.get(1).subscription);
      } finally {
        installer.uninstall();
      }
    }
  }
}
