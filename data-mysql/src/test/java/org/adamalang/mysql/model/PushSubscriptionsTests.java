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
package org.adamalang.mysql.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
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
  private void assertHash(String ex, ObjectNode node) throws Exception {
    String hash = PushSubscriptions.dedupeHash(node);
    Assert.assertTrue(hash.length() <= 64);
    Assert.assertEquals(ex, hash);
  }
  @Test
  public void hashing() throws Exception {
    // straight hash for unknowns (probably a problem)
    try {
      assertHash(null, Json.parseJsonObject("{}"));
      Assert.fail();
    } catch (Exception ex) {
      Assert.assertEquals("unknown push subscription type", ex.getMessage());
    }
    // validate only endpoint matters
    assertHash("KfdOvoFCOejVSyZ8yU3Hm5ZIIfARTtlOrpFtCUlYSL33eUS91tnv+Ez6D9s/xAxr", Json.parseJsonObject("{\"endpoint\":\"https://updates.push.services.mozilla.com/wpush/v2/gAAAAABl2NJJGBSdXb0I9ULp764E21ocVdW9uAn5_bndoxnPXUhxKtJuU\",\"expirationTime\":null,\"keys\":{\"auth\":\"lAQBI\",\"p256dh\":\"BJ-\"},\"@method\":\"webpush\",\"@time\":1708708426029}"));
    assertHash("KfdOvoFCOejVSyZ8yU3Hm5ZIIfARTtlOrpFtCUlYSL33eUS91tnv+Ez6D9s/xAxr", Json.parseJsonObject("{\"endpoint\":\"https://updates.push.services.mozilla.com/wpush/v2/gAAAAABl2NJJGBSdXb0I9ULp764E21ocVdW9uAn5_bndoxnPXUhxKtJuU\",\"expirationTime\":null,\"keys\":{\"auth\":\"lAQBI\",\"p256dh\":\"BJ-asdf\"},\"@method\":\"webpush\",\"@time\":17087084269}"));
    // validate only
    assertHash("CgYwcQzh2YvCysfs8b1L+2Hw/Ff79P9149CQABwA2IC2P9IWaIOsILD2wiDTZVRD", Json.parseJsonObject("{\"@method\":\"capacitor\",\"@time\":12,\"token\":\"dW9lUdCwE0mME3:APA91bEsnByfphEwz59iFAdCG\"}"));
    assertHash("CgYwcQzh2YvCysfs8b1L+2Hw/Ff79P9149CQABwA2IC2P9IWaIOsILD2wiDTZVRD", Json.parseJsonObject("{\"@method\":\"capacitor\",\"@time\":1224242,\"token\":\"dW9lUdCwE0mME3:APA91bEsnByfphEwz59iFAdCG\"}"));
  }
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
        PushSubscriptions.registerSubscription(dataBase, "www.domain.com", new NtPrincipal("1", "adama"), "a", "sub-1", "device1", System.currentTimeMillis() + 14 * 90000);
        PushSubscriptions.registerSubscription(dataBase, "www.domain.com", new NtPrincipal("1", "adama"), "b", "sub-2", "device2", System.currentTimeMillis() + 14 * 90000);
        List<DeviceSubscription> subs = PushSubscriptions.list(dataBase, "www.domain.com", new NtPrincipal("1", "adama"));
        Assert.assertEquals(2, subs.size());
        Assert.assertEquals("sub-1", subs.get(0).subscription);
        Assert.assertEquals("sub-2", subs.get(1).subscription);
        Assert.assertEquals("a", subs.get(0).dedupe);
        Assert.assertEquals("b", subs.get(1).dedupe);
        Assert.assertEquals("device1", subs.get(0).deviceInfo);
        Assert.assertEquals("device2", subs.get(1).deviceInfo);
        PushSubscriptions.registerSubscription(dataBase, "www.domain.com", new NtPrincipal("1", "adama"), "a", "sub-3", "device3", System.currentTimeMillis() + 14 * 90000);
        subs = PushSubscriptions.list(dataBase, "www.domain.com", new NtPrincipal("1", "adama"));
        Assert.assertEquals(2, subs.size());
        Assert.assertEquals("sub-2", subs.get(0).subscription);
        Assert.assertEquals("sub-3", subs.get(1).subscription);
        Assert.assertEquals("b", subs.get(0).dedupe);
        Assert.assertEquals("a", subs.get(1).dedupe);
        Assert.assertEquals("device2", subs.get(0).deviceInfo);
        Assert.assertEquals("device3", subs.get(1).deviceInfo);
      } finally {
        installer.uninstall();
      }
    }
  }
}
