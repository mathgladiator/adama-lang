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

import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.*;
import org.adamalang.mysql.data.SystemUsageInventoryRecord;
import org.adamalang.runtime.sys.domains.Domain;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class DomainsTest {
  @Test
  public void flow() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory()))) {
      Installer installer = new Installer(dataBase);
      try {
        installer.install();
        try {
          Domains.getNativeAppConfig(dataBase, "www.domain.com");
          Assert.fail();
        } catch (ErrorCodeException ex) {
          Assert.assertEquals(736243, ex.code);
        }
        Domain result = Domains.get(dataBase, "www.domain.com");
        Assert.assertNull(result);
        Assert.assertTrue(Domains.map(dataBase, 141, "www.domain.com", "my-space", null, true, "certificate-yay"));
        try {
          Domains.getNativeAppConfig(dataBase, "www.domain.com");
          Assert.fail();
        } catch (ErrorCodeException ex) {
          Assert.assertEquals(736243, ex.code);
        }
        Domains.putNativeAppConfig(dataBase, "www.domain.com", "some-config");
        Assert.assertEquals("some-config", Domains.getNativeAppConfig(dataBase, "www.domain.com"));
        result = Domains.get(dataBase, "www.domain.com");
        Assert.assertEquals(1, Domains.listBySpace(dataBase, "my-space").size());
        Assert.assertEquals("www.domain.com", Domains.listBySpace(dataBase, "my-space").get(0).domain);
        Assert.assertEquals("my-space", result.space);
        Assert.assertEquals("certificate-yay", result.certificate);
        Assert.assertEquals(141, result.owner);
        Assert.assertFalse(Domains.map(dataBase, 145, "www.domain.com", "my", null, false, "yo"));
        result = Domains.get(dataBase, "www.domain.com");
        Assert.assertEquals("my-space", result.space);
        Assert.assertEquals("certificate-yay", result.certificate);
        Assert.assertEquals(141, result.owner);
        Assert.assertTrue(Domains.map(dataBase, 141, "www.domain.com", "my-space-two", null,true, "certificate-ok"));
        Assert.assertFalse(Domains.unmap(dataBase, 150, "www.domain.com"));
        result = Domains.get(dataBase, "www.domain.com");
        Assert.assertEquals("my-space-two", result.space);
        Assert.assertEquals("certificate-ok", result.certificate);
        Assert.assertEquals(141, result.owner);
        Assert.assertTrue(Domains.unmap(dataBase, 141, "www.domain.com"));
        result = Domains.get(dataBase, "www.domain.com");
        Assert.assertNull(result);
        Assert.assertTrue(Domains.map(dataBase, 42, "www.domain1.com", "space", null,true, "certificate-ok"));
        Assert.assertTrue(Domains.map(dataBase, 42, "www.domain2.com", "space", "some-doc",false, "certificate-ok"));
        Assert.assertEquals("some-doc", Domains.get(dataBase, "www.domain2.com").key);
        Assert.assertTrue(Domains.map(dataBase, 42, "www.domain3.com", "space", null,false, "certificate-ok"));
        Assert.assertEquals(3, Domains.deleteSpace(dataBase, "space"));
        Assert.assertTrue(Domains.map(dataBase, 500, "www.my-domain.com", "space", "over",true, null));
        Assert.assertTrue(Domains.map(dataBase, 500, "www.my-domain.com", "space2", null,false, null));
        ArrayList<Domain> simpleList = Domains.list(dataBase, 500);
        HashMap<Integer, SystemUsageInventoryRecord> records = Inventory.inventorySystemUsage(dataBase);
        Assert.assertEquals(1, records.get(500).domains);
        Assert.assertEquals("space2", simpleList.get(0).space);
        Assert.assertEquals(0, Domains.list(dataBase, -1).size());
        ArrayList<Domain> superList = Domains.superListAutoDomains(dataBase, 100);
        Assert.assertEquals("space2", superList.get(0).space);
        Assert.assertNull(superList.get(0).certificate);
        Assert.assertTrue(Domains.superSetAutoCert(dataBase, "www.my-domain.com", "new-cert", 50));
        superList = Domains.superListAutoDomains(dataBase, 100);
        Assert.assertEquals("new-cert", superList.get(0).certificate);
        Assert.assertEquals(50, superList.get(0).timestamp);
        Assert.assertTrue(Domains.superSetAutoCert(dataBase, "www.my-domain.com", "new-cert", 150));
        superList = Domains.superListAutoDomains(dataBase, 100);
        Assert.assertEquals(0, superList.size());
      } finally {
        installer.uninstall();
      }
    }
  }
}
