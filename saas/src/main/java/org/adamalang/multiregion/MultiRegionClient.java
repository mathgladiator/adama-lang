/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.multiregion;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.model.Deployments;
import org.adamalang.mysql.model.Finder;
import org.adamalang.net.client.Client;
import org.adamalang.net.client.contracts.SimpleEvents;
import org.adamalang.runtime.contracts.AdamaStream;
import org.adamalang.runtime.data.DelayAdamaStream;
import org.adamalang.runtime.data.FinderService;
import org.adamalang.runtime.data.Key;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/** isolates the client away from the API level to control routing */
public class MultiRegionClient {
  private static final Logger LOG = LoggerFactory.getLogger(MultiRegionClient.class);
  private final SimpleExecutor executor;
  private final DataBase dataBase;
  private final Client local;
  private final String region;
  private final Finder finder;

  public MultiRegionClient(DataBase dataBase, Client local, String region, Finder finder) {
    this.executor = SimpleExecutor.create("multi-region-client");
    this.dataBase = dataBase;
    this.local = local;
    this.region = region;
    this.finder = finder;
  }

  public void shutdown() {
    executor.shutdown();
    local.shutdown();
  }

  public void deploy(String space, String hash, String planJson) {
    local.getDeploymentTargets(space, (target) -> {
      try {
        // persist the deployment binding
        Deployments.deploy(dataBase, space, target, hash, planJson);
        // notify the client of an update
        local.notifyDeployment(target, space);
      } catch (Exception ex) {
        LOG.error("failed-deployment-write", ex);
      }
    });
  }

  public void waitForCapacity(String space, int timeout, Consumer<Boolean> finished) {
    local.waitForCapacity(space, timeout, finished);
  }

  public void reflect(String space, String key, Callback<String> callback) {
    local.reflect(space, key, callback);
  }

  public void create(String ip, String origin, String agent, String authority, String space, String key, String entropy, String arg, Callback<Void> callback) {
    local.create(ip, origin, agent, authority, space, key, entropy, arg, callback);
  }

  public AdamaStream connect(AuthenticatedUser user, String space, String key, String viewerState, SimpleEvents events) {
    DelayAdamaStream stream = new DelayAdamaStream(executor, local.metrics.multi_region_find);
    finder.find(new Key(space, key), new Callback<>() {
      @Override
      public void success(FinderService.Result value) {
        if (value.location == FinderService.Location.Machine) {
          if (region.equals(value.region)) {
            stream.ready(local.connect(user.context.remoteIp, user.context.origin, user.who.agent, user.who.authority, space, key, viewerState, user.context.assetKey, events));
          } else {
            // TODO: USE THE REGION
            events.error(0);
          }
        } else {
          stream.ready(local.connect(user.context.remoteIp, user.context.origin, user.who.agent, user.who.authority, space, key, viewerState, user.context.assetKey, events));
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        stream.ready(local.connect(user.context.remoteIp, user.context.origin, user.who.agent, user.who.authority, space, key, viewerState, user.context.assetKey, events));
      }
    });
    return stream;
  }
}
