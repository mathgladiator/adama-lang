/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.mysql.frontend;

import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.DataBaseConfig;
import org.adamalang.mysql.DataBaseConfigTests;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class MeteringSampleTests {

  @Test
  public void batches() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig)) {
      FrontendManagementInstaller installer = new FrontendManagementInstaller(dataBase);
      try {
        installer.install();
        long now = System.currentTimeMillis();
        Metering.recordBatch(
            dataBase,
            "target1",
            "{\"time\":\""+ (now - 10000 )+"\",\"spaces\":{\"space\":{\"cpu\":\"14812904860\",\"messages\":\"2830000\",\"count_p95\":\"4\",\"memory_p95\":\"1000\"}}}", now);

        Metering.recordBatch(
            dataBase,
            "target2",
            "{\"time\":\""+(now + 10000)+"\",\"spaces\":{\"space\":{\"cpu\":\"14812904860\",\"messages\":\"2830000\",\"count_p95\":\"4\",\"memory_p95\":\"1000\"}}}", now);

        HashMap<String, Metering.SpaceSummary> summary1 = Metering.summarizeWindow(dataBase, now - 100000, now + 100000);
        Assert.assertEquals(1, summary1.size());
        Assert.assertEquals(
            "{\"cpu\":\"29625809720\",\"messages\":\"5660000\",\"count\":\"8\",\"memory\":\"2000\"}",
            summary1.get("space").summarize());

      } finally {
        installer.uninstall();
      }
    }
  }
}
