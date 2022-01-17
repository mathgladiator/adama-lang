/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.overlord.roles;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.grpc.client.Client;
import org.adamalang.grpc.client.contracts.BillingStream;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.frontend.Billing;
import org.adamalang.overlord.OverlordMetrics;
import org.adamalang.overlord.html.ConcurrentCachedHtmlHandler;
import org.adamalang.overlord.html.FixedHtmlStringLoggerTable;

public class BillingAggregator {
  public static void kickOff(
      OverlordMetrics metrics,
      Client client,
      DataBase dataBaseFront,
      ConcurrentCachedHtmlHandler handler) {
    SimpleExecutor executor = SimpleExecutor.create("billing-aggregator");

    FixedHtmlStringLoggerTable table = new FixedHtmlStringLoggerTable(32, "target", "batch", "time");

    executor.schedule(new NamedRunnable("billing-fetch") {
      @Override
      public void execute() throws Exception {
        NamedRunnable self = this;
        client.randomBillingExchange(new BillingStream() {
          @Override
          public void handle(String target, String batch, Runnable after) {
            metrics.billing_fetch_found.run();
            executor.execute(new NamedRunnable("handle-batch") {
              @Override
              public void execute() throws Exception {
                long now = System.currentTimeMillis();
                if (!batch.contains("\"spaces\":{}")) {
                  Billing.recordBatch(dataBaseFront, target, batch, now);
                  table.row(target, batch, Long.toString(now));
                  metrics.billing_fetch_saved.run();
                } else {
                  // don't bother saving an empty batch
                  metrics.billing_fetch_empty.run();
                }
                after.run();
              }
            });
          }

          @Override
          public void failure(int code) {
            metrics.billing_fetch_failed.run();
            finished();
          }

          private boolean gotFinished = false;

          @Override
          public void finished() {
            executor.execute(new NamedRunnable("got-finished") {
              @Override
              public void execute() throws Exception {
                if (!gotFinished) {
                  gotFinished = true;
                  metrics.billing_fetch_finished.run();
                  handler.put("/billing", table.toHtml("Recent Billing Data"));
                  executor.schedule(self, 10000);
                }
              }
            });
          }
        });
      }
    }, 1000 * 5);


    // TODO: ROLE #3.B: when a hot host appears, use billing information to find hottest space, and then make a decision to act on it
    // TODO: ROLE #3.C: adama should inform which spaces on a hot host are oversubscribed... this is an interesting challenge


  }
}
