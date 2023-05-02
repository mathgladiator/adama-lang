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

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.multiregion.MultiRegionClient;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.data.DeletedSpace;
import org.adamalang.mysql.data.DocumentIndex;
import org.adamalang.mysql.model.Domains;
import org.adamalang.mysql.model.FinderOperations;
import org.adamalang.mysql.model.Sentinel;
import org.adamalang.mysql.model.Spaces;
import org.adamalang.overlord.OverlordMetrics;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.web.io.ConnectionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/** a bot that wakes up, looks for deleted spaces, and then ensures the name is ready for usage */
public class GlobalSpaceDeleteBot {
  public static AuthenticatedUser OVERLORD = new AuthenticatedUser(
      AuthenticatedUser.Source.Super,
      -1,
      new NtPrincipal("overlord", "overlord"),
      new ConnectionContext("overlord", "0.0.0.0", "overlord", ""),
      true);
  private static Logger LOG = LoggerFactory.getLogger(GlobalSpaceDeleteBot.class);
  private final OverlordMetrics metrics;
  private final DataBase dataBase;
  private final MultiRegionClient client; // TODO: convert to multi-region client

  private GlobalSpaceDeleteBot(OverlordMetrics metrics, DataBase dataBase, MultiRegionClient client) {
    this.metrics = metrics;
    this.dataBase = dataBase;
    this.client = client;
  }

  private void item(DeletedSpace ds) throws Exception {
    metrics.delete_bot_found.run();
    Domains.deleteSpace(dataBase, ds.name);
    ArrayList<DocumentIndex> found = FinderOperations.list(dataBase, ds.name, "", 100);
    LOG.error("cleaning-up:" + ds.name + ";size=" + found.size());
    if (found.size() == 0) {
      CountDownLatch latch = new CountDownLatch(1);
      client.delete(OVERLORD, "ide", ds.name, metrics.delete_bot_delete_ide.wrap(new Callback<Void>() {
        @Override
        public void success(Void value) {
          LOG.error("cleaned:" + ds.name);
          metrics.delete_bot_delete_space.run();
          try {
            Spaces.delete(dataBase, ds.id, 0);
          } catch (Exception ex) {
            LOG.error("failed-delete-space", ex);
          }
          latch.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {
          if (ex.code == ErrorCodes.UNIVERSAL_LOOKUP_FAILED || ex.code == ErrorCodes.NET_FINDER_GAVE_UP || ex.code == ErrorCodes.NET_FINDER_FAILED_PICK_HOST) {
            success(null);
            return;
          }
          LOG.error("failed-delete-ide: " + ds.name, ex);
          latch.countDown();
        }
      }));
      latch.await(10000, TimeUnit.MILLISECONDS);
    } else {
      for (DocumentIndex doc : found) {
        client.delete(OVERLORD, ds.name, doc.key, metrics.delete_bot_delete_document.wrap(Callback.DONT_CARE_VOID));
      }
    }
  }

  public void round() throws Exception {
    metrics.delete_bot_wake.run();
    for (final DeletedSpace ds : Spaces.listDeletedSpaces(dataBase)) {
      try {
        item(ds);
      } catch (Exception ex) {
        LOG.error("failed-delete:" + ds.name, ex);
      }
    }
  }

  public static void kickOff(OverlordMetrics metrics, DataBase dataBase, MultiRegionClient client, AtomicBoolean alive) {
    SimpleExecutor executor = SimpleExecutor.create("space-delete-bot");
    GlobalSpaceDeleteBot bot = new GlobalSpaceDeleteBot(metrics, dataBase, client);
    executor.schedule(new NamedRunnable("space-delete-bot") {
      @Override
      public void execute() throws Exception {
        if (alive.get()) {
          try {
            bot.round();
            Sentinel.ping(dataBase, "space-delete-bot", System.currentTimeMillis());
          } finally {
            executor.schedule(this, (int) (30000 + Math.random() * 30000));
          }
        }
      }
    }, 1000);
  }
}
