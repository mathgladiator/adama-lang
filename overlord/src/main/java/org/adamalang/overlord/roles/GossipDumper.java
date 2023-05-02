/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.overlord.roles;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.gossip.Engine;
import org.adamalang.overlord.OverlordMetrics;
import org.adamalang.overlord.html.ConcurrentCachedHttpHandler;

// Periodically dumps an HTML table of the state of the gossip engine
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
