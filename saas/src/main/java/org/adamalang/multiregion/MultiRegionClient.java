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
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.model.Deployments;
import org.adamalang.mysql.model.Finder;
import org.adamalang.net.client.Client;
import org.adamalang.net.client.contracts.SimpleEvents;
import org.adamalang.runtime.contracts.AdamaStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/** isolates the client away from the API level to control routing */
public class MultiRegionClient {
  private static final Logger LOG = LoggerFactory.getLogger(MultiRegionClient.class);
  private final DataBase dataBase;
  private final Client local;
  private final Finder finder;

  public MultiRegionClient(DataBase dataBase, Client local, Finder finder) {
    this.dataBase = dataBase;
    this.local = local;
    this.finder = finder;
  }

  public void shutdown() {
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

  public AdamaStream connect(String ip, String origin, String agent, String authority, String space, String key, String viewerState, String assetKey, SimpleEvents events) {
    return local.connect(ip, origin, agent, authority, space, key, viewerState, assetKey, events);
  }
}
