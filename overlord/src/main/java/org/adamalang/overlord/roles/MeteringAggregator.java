/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.overlord.roles;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.model.Metering;
import org.adamalang.net.client.Client;
import org.adamalang.net.client.contracts.MeteringStream;
import org.adamalang.overlord.OverlordMetrics;
import org.adamalang.overlord.html.ConcurrentCachedHttpHandler;
import org.adamalang.overlord.html.FixedHtmlStringLoggerTable;

public class MeteringAggregator {
  public static void kickOff(OverlordMetrics metrics, Client client, DataBase dataBaseFront, ConcurrentCachedHttpHandler handler) {
    SimpleExecutor executor = SimpleExecutor.create("metering-aggregator");
    FixedHtmlStringLoggerTable table = new FixedHtmlStringLoggerTable(32, "target", "batch", "time");
    executor.schedule(new NamedRunnable("metering-fetch") {
      @Override
      public void execute() throws Exception {
        NamedRunnable self = this;
        client.randomMeteringExchange(new MeteringStream() {
          private boolean gotFinished = false;

          @Override
          public void handle(String target, String batch, Runnable after) {
            metrics.metering_fetch_found.run();
            executor.execute(new NamedRunnable("handle-metering-batch") {
              @Override
              public void execute() throws Exception {
                long now = System.currentTimeMillis();
                if (!batch.contains("\"spaces\":{}")) {
                  Metering.recordBatch(dataBaseFront, target, batch, now);
                  table.row(target, batch, Long.toString(now));
                  metrics.metering_fetch_saved.run();
                } else {
                  // don't bother saving an empty batch
                  metrics.metering_fetch_empty.run();
                }
                after.run();
              }
            });
          }

          @Override
          public void failure(int code) {
            metrics.metering_fetch_failed.run();
            finished();
          }

          @Override
          public void finished() {
            executor.execute(new NamedRunnable("got-finished") {
              @Override
              public void execute() throws Exception {
                if (!gotFinished) {
                  gotFinished = true;
                  metrics.metering_fetch_finished.run();
                  handler.put("/metering", table.toHtml("Recent Metering Data"));
                  executor.schedule(self, 10000);
                }
              }
            });
          }
        });
      }
    }, 1000 * 5);
  }
}
