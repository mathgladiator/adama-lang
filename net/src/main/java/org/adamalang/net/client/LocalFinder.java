/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.client;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.net.client.routing.cache.RoutingTable;
import org.adamalang.net.client.routing.cache.RoutingTableTarget;
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
