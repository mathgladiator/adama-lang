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
