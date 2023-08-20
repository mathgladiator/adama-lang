/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.impl;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.keys.MasterKey;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.*;
import org.adamalang.mysql.model.Domains;
import org.adamalang.runtime.sys.domains.Domain;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GlobalDomainFinderTests {
  @Test
  public void flow() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory()))) {
      Installer installer = new Installer(dataBase);
      try {
        installer.install();
        String masterKey = MasterKey.generateMasterKey();
        GlobalDomainFinder finder = new GlobalDomainFinder(dataBase, masterKey);
        CountDownLatch latch = new CountDownLatch(2);
        finder.find("domain", new Callback<Domain>() {
          @Override
          public void success(Domain value) {
            Assert.assertNull(value);
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        });
        Assert.assertTrue(Domains.map(dataBase, 1, "new-domain", "space", "key", true, MasterKey.encrypt(masterKey, "cert")));
        finder.find("new-domain", new Callback<Domain>() {
          @Override
          public void success(Domain value) {
            Assert.assertEquals("space", value.space);
            Assert.assertEquals("cert", value.certificate);
            latch.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        });
        Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
      } finally {
        installer.uninstall();
      }
    }
  }
}
