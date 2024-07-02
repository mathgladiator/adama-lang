/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.net.client;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.net.client.routing.RoutingTable;
import org.adamalang.net.client.routing.RoutingTableTarget;
import org.adamalang.runtime.data.DocumentLocation;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.SimpleFinderService;

/** a simplified finder for finding where a document _could_ live within the local region */
public class LocalFinder implements SimpleFinderService {
  private final SimpleExecutor executor;
  private final RoutingTable table;
  private final InstanceClientFinder clientFinder;

  public LocalFinder(RoutingTableTarget target, InstanceClientFinder clientFinder) {
    this.executor = target.executor;
    this.table = target.table;
    this.clientFinder = clientFinder;
  }

  private void executeFind(String machine, Key key, Callback<DocumentLocation> callback) {
    clientFinder.find(machine, new Callback<InstanceClient>() {
      @Override
      public void success(InstanceClient value) {
        value.find(key, callback);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  @Override
  public void find(Key key, Callback<DocumentLocation> callback) {
    executor.execute(new NamedRunnable("get", key.space, key.key) {
      @Override
      public void execute() throws Exception {
        String localMachine = table.get(key.space, key.key);
        if (localMachine == null) {
          localMachine = table.pick(key.space);
        }
        if (localMachine == null) {
          callback.failure(new ErrorCodeException(ErrorCodes.FAILED_FIND_LOCAL_CAPACITY));
          return;
        }
        executeFind(localMachine, key, callback);
      }
    });
  }
}
