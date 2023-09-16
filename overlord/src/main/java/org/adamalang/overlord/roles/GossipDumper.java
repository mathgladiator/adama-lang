/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
