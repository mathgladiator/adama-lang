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
package org.adamalang.mysql.impl;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.model.Capacity;
import org.adamalang.runtime.deploy.DeploySync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** as spaces come and go from the machine, keep the capacity table up to date */
public class GlobalCapacitySync implements DeploySync {
  private static final Logger LOG = LoggerFactory.getLogger(GlobalCapacitySync.class);
  private final DataBase db;
  private final String region;
  private final String machine;
  private final SimpleExecutor executor;
  private final DeploySync proxy;

  public GlobalCapacitySync(DataBase db, String region, String machine, SimpleExecutor executor, DeploySync proxy) {
    this.db = db;
    this.region = region;
    this.machine = machine;
    this.executor = executor;
    this.proxy = proxy;
  }

  @Override
  public void watch(String space) {
    executor.execute(new NamedRunnable("async-gcs-watch") {
      @Override
      public void execute() throws Exception {
        try {
          proxy.watch(space);
          Capacity.add(db, space, region, machine);
        } catch (Exception ex) {
          LOG.error("failed-add-capacity", ex);
        }
      }
    });
  }

  @Override
  public void unwatch(String space) {
    executor.execute(new NamedRunnable("async-gcs-watch") {
      @Override
      public void execute() throws Exception {
        try {
          proxy.unwatch(space);
          Capacity.remove(db, space, region, machine);
        } catch (Exception ex) {
          LOG.error("failed-remove-capacity", ex);
        }
      }
    });
  }
}
