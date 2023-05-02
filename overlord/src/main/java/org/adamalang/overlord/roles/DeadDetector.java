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
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.model.Sentinel;
import org.adamalang.overlord.OverlordMetrics;

import java.util.concurrent.atomic.AtomicBoolean;

// The dead detector will periodically scan the database to find tasks which haven't self reported in 15 minutes.
// This will raise a flag for the alarm system to pick up on
public class DeadDetector {
  public static void kickOff(OverlordMetrics metrics, DataBase dataBase, AtomicBoolean alive) {
    SimpleExecutor executor = SimpleExecutor.create("dead-detector");
    executor.schedule(new NamedRunnable("dead-detector") {
      @Override
      public void execute() throws Exception {
        if (alive.get()) {
          try {
            metrics.sentinel_behind.set(Sentinel.countBehind(dataBase, System.currentTimeMillis() - 15 * 60 * 1000));
            Sentinel.ping(dataBase, "dead-detector", System.currentTimeMillis());
          } finally {
            executor.schedule(this, (int) (30000 + Math.random() * 30000));
          }
        }
      }
    }, 1000);
  }
}
