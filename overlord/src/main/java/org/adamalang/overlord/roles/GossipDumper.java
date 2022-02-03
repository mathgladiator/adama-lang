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
import org.adamalang.gossip.Engine;
import org.adamalang.overlord.OverlordMetrics;
import org.adamalang.overlord.html.ConcurrentCachedHttpHandler;

public class GossipDumper {
  public static void kickOff(OverlordMetrics metrics, Engine engine, ConcurrentCachedHttpHandler handler) {
    SimpleExecutor executor = SimpleExecutor.create("scan-gossip");
    executor.schedule(new NamedRunnable("dump-gossip") {
      @Override
      public void execute() throws Exception {
        metrics.gossip_dump.run();
        engine.summarizeHtml((html) -> {
          handler.put("/gossip", html);
          executor.schedule(this, 500);
        });
      }
    }, 250);
  }
}
