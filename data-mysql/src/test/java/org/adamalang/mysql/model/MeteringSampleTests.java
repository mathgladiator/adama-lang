/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.mysql.model;

import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.*;
import org.adamalang.mysql.data.*;
import org.adamalang.mysql.model.metrics.MeteringMetrics;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class MeteringSampleTests {
  @Test
  public void lateEarly() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory()))) {
      Installer installer = new Installer(dataBase);
      try {
        MeteringMetrics metrics = new MeteringMetrics(new NoOpMetricsFactory());
        installer.install();
        Assert.assertNull(Spaces.getLatestBillingHourCode(dataBase));
        Assert.assertNull(Metering.getEarliestRecordTimeOfCreation(dataBase));
        int userId = Users.getOrCreateUserId(dataBase, "user@user.com");
        int spaceId = Spaces.createSpace(dataBase, userId, "space");
        Assert.assertEquals(0, (int) Spaces.getLatestBillingHourCode(dataBase));
        long now = System.currentTimeMillis();
        Metering.recordBatch(dataBase, "target1", "{\"time\":\"" + (now - 100000000) + "\",\"spaces\":{\"space\":{\"cpu\":\"14812904860\",\"messages\":\"2830000\",\"count_p95\":\"4\",\"memory_p95\":\"1000\",\"connections_p95\":29,\"bandwidth\":\"540\",\"third_party_service_calls\":10000}}}", now);
        Metering.recordBatch(dataBase, "target2", "{\"time\":\"" + (now + 100000000) + "\",\"spaces\":{\"space\":{\"cpu\":\"14812904860\",\"messages\":\"2830000\",\"count_p95\":\"4\",\"memory_p95\":\"1000\",\"connections_p95\":19,\"first_party_service_calls\":17}}}", now);
        Assert.assertNotNull(Metering.getEarliestRecordTimeOfCreation(dataBase));
        HashMap<String, MeteringSpaceSummary> summary1 = Metering.summarizeWindow(dataBase, metrics, now - 100000, now + 100000);
        Assert.assertEquals(1, summary1.size());
        HashMap<String, Long> inventory = new HashMap<>();
        inventory.put("space", 1024L);
        inventory.put("space2", 2048L);
        HashMap<String, UnbilledResources> unbilled = Spaces.collectUnbilledStorage(dataBase);
        unbilled.put("space2", new UnbilledResources(1000000000L - 2000, 0, 0, 0));
        Billing.mergeStorageIntoSummaries(summary1, inventory, unbilled);
        ResourcesPerPenny rates = new ResourcesPerPenny(1000000, 2000, 50, 1024, 500, 1000000000, 1000, 100, 500);
        {
          MeteredWindowSummary summarySpace = summary1.get("space").summarize(rates);
          Assert.assertEquals("{\"cpu\":\"29625809720\",\"messages\":\"5660000\",\"count\":\"8\",\"memory\":\"2000\",\"bandwidth\":\"540\",\"connections\":\"48\",\"storageBytes\":\"1024\",\"bandwidth\":\"540\",\"first_party_service_calls\":\"17\",\"third_party_service_calls\":\"10000\"}", summarySpace.resources);
          Assert.assertEquals(1024, summarySpace.storageBytes);
          Assert.assertEquals(1024, summarySpace.changeUnbilled.storage);
          Assert.assertEquals(29646, summarySpace.pennies);
        }
        {
          MeteredWindowSummary summarySpace2 = summary1.get("space2").summarize(rates);
          Assert.assertEquals("{\"storageBytes\":\"2048\"}", summarySpace2.resources);
          Assert.assertEquals(2048, summarySpace2.storageBytes);
          Assert.assertEquals(-999997952, summarySpace2.changeUnbilled.storage);
          Assert.assertEquals(1, summarySpace2.pennies);
        }
        Assert.assertEquals(500, Users.getBalance(dataBase, userId));
        Billing.transcribeSummariesAndUpdateBalances(dataBase, 52, summary1, rates);
        Assert.assertEquals(-29146, Users.getBalance(dataBase, userId));
        Assert.assertEquals(52, (int) Spaces.getLatestBillingHourCode(dataBase));
        HashMap<String, UnbilledResources> unbilledAfter = Spaces.collectUnbilledStorage(dataBase);
        Assert.assertEquals(1024, (long) (unbilledAfter.get("space").storage));
        Billing.mergeStorageIntoSummaries(summary1, inventory, unbilled);

        ArrayList<BillingUsage> usages = Billing.usageReport(dataBase, spaceId, 2);
        Assert.assertEquals(1, usages.size());
        Assert.assertEquals(29625809720L, usages.get(0).cpu);
        Assert.assertEquals(2000, usages.get(0).memory);
        Assert.assertEquals(1024, usages.get(0).storageBytes);
        Assert.assertEquals(5660000, usages.get(0).messages);
        Assert.assertEquals(8, usages.get(0).documents);
        Assert.assertEquals(48, usages.get(0).connections);

      } finally {
        installer.uninstall();
      }
    }
  }

  @Test
  public void batches() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory()))) {
      Installer installer = new Installer(dataBase);
      try {
        MeteringMetrics metrics = new MeteringMetrics(new NoOpMetricsFactory());
        installer.install();
        Assert.assertNull(Spaces.getLatestBillingHourCode(dataBase));
        Assert.assertNull(Metering.getEarliestRecordTimeOfCreation(dataBase));
        int userId = Users.getOrCreateUserId(dataBase, "user@user.com");
        int spaceId = Spaces.createSpace(dataBase, userId, "space");
        Assert.assertEquals(0, (int) Spaces.getLatestBillingHourCode(dataBase));
        long now = System.currentTimeMillis();
        Metering.recordBatch(dataBase, "target1", "{\"time\":\"" + (now - 10000) + "\",\"spaces\":{\"space\":{\"cpu\":\"14812904860\",\"messages\":\"2830000\",\"count_p95\":\"4\",\"memory_p95\":\"1000\",\"connections_p95\":29}}}", now);
        Metering.recordBatch(dataBase, "target2", "{\"time\":\"" + (now + 10000) + "\",\"spaces\":{\"space\":{\"cpu\":\"14812904860\",\"messages\":\"2830000\",\"count_p95\":\"4\",\"memory_p95\":\"1000\",\"connections_p95\":19}}}", now);
        Assert.assertNotNull(Metering.getEarliestRecordTimeOfCreation(dataBase));
        HashMap<String, MeteringSpaceSummary> summary1 = Metering.summarizeWindow(dataBase, metrics, now - 100000, now + 100000);
        Assert.assertEquals(1, summary1.size());
        HashMap<String, Long> inventory = new HashMap<>();
        inventory.put("space", 1024L);
        inventory.put("space2", 2048L);
        HashMap<String, UnbilledResources> unbilled = Spaces.collectUnbilledStorage(dataBase);
        unbilled.put("space2", new UnbilledResources(1000000000L - 2000, 0, 0, 0));
        Billing.mergeStorageIntoSummaries(summary1, inventory, unbilled);
        ResourcesPerPenny rates = new ResourcesPerPenny(1000000, 2000, 50, 1024, 500, 1000000000, 1000, 100, 500);
        {
          MeteredWindowSummary summarySpace = summary1.get("space").summarize(rates);
          Assert.assertEquals("{\"cpu\":\"29625809720\",\"messages\":\"5660000\",\"count\":\"8\",\"memory\":\"2000\",\"connections\":\"48\",\"storageBytes\":\"1024\"}", summarySpace.resources);
          Assert.assertEquals(1024, summarySpace.storageBytes);
          Assert.assertEquals(1024, summarySpace.changeUnbilled.storage);
          Assert.assertEquals(29626, summarySpace.pennies);
        }
        {
          MeteredWindowSummary summarySpace2 = summary1.get("space2").summarize(rates);
          Assert.assertEquals("{\"storageBytes\":\"2048\"}", summarySpace2.resources);
          Assert.assertEquals(2048, summarySpace2.storageBytes);
          Assert.assertEquals(-999997952, summarySpace2.changeUnbilled.storage);
          Assert.assertEquals(1, summarySpace2.pennies);
        }
        Assert.assertEquals(500, Users.getBalance(dataBase, userId));
        Billing.transcribeSummariesAndUpdateBalances(dataBase, 52, summary1, rates);
        Assert.assertEquals(-29126, Users.getBalance(dataBase, userId));
        Assert.assertEquals(52, (int) Spaces.getLatestBillingHourCode(dataBase));
        HashMap<String, UnbilledResources> unbilledAfter = Spaces.collectUnbilledStorage(dataBase);
        Assert.assertEquals(1024, (long) (unbilledAfter.get("space").storage));
        Billing.mergeStorageIntoSummaries(summary1, inventory, unbilled);

        ArrayList<BillingUsage> usages = Billing.usageReport(dataBase, spaceId, 2);
        Assert.assertEquals(1, usages.size());
        Assert.assertEquals(29625809720L, usages.get(0).cpu);
        Assert.assertEquals(2000, usages.get(0).memory);
        Assert.assertEquals(1024, usages.get(0).storageBytes);
        Assert.assertEquals(5660000, usages.get(0).messages);
        Assert.assertEquals(8, usages.get(0).documents);
        Assert.assertEquals(48, usages.get(0).connections);

        Assert.assertTrue(Spaces.getSpaceInfo(dataBase, "space").enabled);
        Users.disableSweep(dataBase);
        Assert.assertFalse(Spaces.getSpaceInfo(dataBase, "space").enabled);
        Users.addToBalance(dataBase, userId, 50000);
        Assert.assertEquals(50000-29126, Users.getBalance(dataBase, userId));
        Assert.assertTrue(Spaces.getSpaceInfo(dataBase, "space").enabled);
      } finally {
        installer.uninstall();
      }
    }
  }
}
