/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.overlord.roles;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.multiregion.MultiRegionClient;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.data.UnbilledResources;
import org.adamalang.mysql.model.*;
import org.adamalang.mysql.data.MeteringSpaceSummary;
import org.adamalang.mysql.data.ResourcesPerPenny;
import org.adamalang.net.client.Client;
import org.adamalang.overlord.OverlordMetrics;
import org.adamalang.overlord.html.ConcurrentCachedHttpHandler;
import org.adamalang.overlord.html.FixedHtmlStringLoggerTable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;

/** aggregate metering records into billing records; note: this is a global task */
public class GlobalHourlyAccountant {

  public static int nextHour(int hour) {
    return toHourCode(fromHourCode(hour).plusHours(1));
  }

  public static int toHourCode(LocalDateTime localDateTime) {
    return localDateTime.getYear() * 1000000 + localDateTime.getMonth().getValue() * 10000 + localDateTime.getDayOfMonth() * 100 + localDateTime.getHour();
  }

  public static LocalDateTime fromHourCode(int hourCode) {
    int v = hourCode;
    int hour = v % 100;
    v /= 100;
    int day = v % 100;
    v /= 100;
    int month = v % 100;
    v /= 100;
    int year = v;
    return LocalDateTime.of(year, month, day, hour, 0);
  }

  public static void kickOff(OverlordMetrics metrics, MultiRegionClient client, DataBase dataBase, ConcurrentCachedHttpHandler handler) throws Exception {
    new HourlyAccountantTask(metrics, client, dataBase, handler);
  }

  public static class HourlyAccountantTask extends NamedRunnable {
    private final OverlordMetrics metrics;
    private final SimpleExecutor executor;
    private final MultiRegionClient client;
    private final DataBase dataBase;
    private final FixedHtmlStringLoggerTable accountantTable;
    private final ConcurrentCachedHttpHandler handler;
    private int billingHourAt;

    public HourlyAccountantTask(OverlordMetrics metrics, MultiRegionClient client, DataBase dataBase, ConcurrentCachedHttpHandler handler) throws Exception {
      super("hourly-accountant");
      this.metrics = metrics;
      this.executor = SimpleExecutor.create("hourly-accountant-executor");
      this.client = client;
      this.dataBase = dataBase;
      this.accountantTable = new FixedHtmlStringLoggerTable(128, "action", "notes", "value");
      Integer pickUp = Spaces.getLatestBillingHourCode(dataBase);
      if (pickUp != null && pickUp > 0) {
        this.billingHourAt = pickUp;
        accountantTable.row("found-latest-billing-code", "first try", "" + this.billingHourAt);
      } else {
        Long firstRecordAt = Metering.getEarliestRecordTimeOfCreation(dataBase);
        if (firstRecordAt != null) {
          this.billingHourAt = toHourCode(LocalDateTime.ofInstant(Instant.ofEpochMilli(firstRecordAt), ZoneId.systemDefault()).minusHours(6));
          accountantTable.row("found-latest-billing-code", "from metering", "" + this.billingHourAt);
        } else {
          this.billingHourAt = toHourCode(LocalDateTime.now().minusHours(6));
          accountantTable.row("found-latest-billing-code", "from neither (fresh)", "" + this.billingHourAt);
        }
      }
      this.handler = handler;
      executor.execute(this);
    }

    @Override
    public void execute() throws Exception {
      try {
        handler.put("/accountant", accountantTable.toHtml("Accountant Work"));
        int current = toHourCode(LocalDateTime.now().minusHours(2));
        while (this.billingHourAt < current) {
          int hourToRun = nextHour(billingHourAt);
          runBilling(hourToRun);
          billingHourAt = hourToRun;
        }
        accountantTable.row("accountant-ran", "hour:" + current, "scan:" + System.currentTimeMillis());
        handler.put("/accountant", accountantTable.toHtml("Accountant Work"));
        Sentinel.ping(dataBase, "accountant", System.currentTimeMillis());
      } finally {
        executor.schedule(this, 1000 * 60 * 5);
      }
    }

    public void runBilling(int forHour) throws Exception {
      metrics.accountant_task.run();
      LocalDateTime from = fromHourCode(forHour);
      LocalDateTime to = from.plusHours(1);
      long fromMs = from.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
      long toMs = to.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

      // TODO: simplify the summaries and inventory into an object, use the client to send the message to the billing document
      /*
      JsonStreamWriter billingMessage = new JsonStreamWriter();
      billingMessage.beginObject();
      billingMessage.endObject();
      */

      HashMap<String, MeteringSpaceSummary> summaries = Metering.summarizeWindow(dataBase, metrics.metering_metrics, fromMs, toMs);
      // TODO: need a more formal rate structure as this is B.S.
      // 2 penny per GB/mo is
      ResourcesPerPenny rates = new ResourcesPerPenny(1000 * 1000, 1000, 50, 1000 * 1000, 200, 386547056640L, 1000 * 1000 * 1000L, 1000, 5000);
      // add storage to the summary
      HashMap<String, Long> inventory = FinderOperations.inventoryStorage(dataBase);
      HashMap<String, UnbilledResources> unbilled = Spaces.collectUnbilledResources(dataBase);
      Billing.mergeStorageIntoSummaries(summaries, inventory, unbilled);
      long pennies = Billing.transcribeSummariesAndUpdateBalances(dataBase, forHour, summaries, rates);

      accountantTable.row("transcribe-summary", "pennies:" + pennies, "at:" + forHour);
    }
  }
}
