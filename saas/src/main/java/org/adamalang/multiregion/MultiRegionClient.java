/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.multiregion;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.mysql.DataBase;
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

import java.security.PrivateKey;
import java.util.function.Consumer;

/** isolates the client away from the API level to control routing */
public class MultiRegionClient {
  private static final Logger LOG = LoggerFactory.getLogger(MultiRegionClient.class);
  private final SimpleExecutor executor;
  private final DataBase dataBase;
  private final Client local;
  private final String region;
  private final PrivateKey privateKey;
  private final int keyId;
  private final Finder finder;

  public MultiRegionClient(DataBase dataBase, Client local, String region, PrivateKey privateKey, int keyId, Finder finder) {
    this.executor = SimpleExecutor.create("multi-region-client");
    this.dataBase = dataBase;
    this.local = local;
    this.region = region;
    this.privateKey = privateKey;
    this.keyId = keyId;
    this.finder = finder;
  }

  public void shutdown() {
    executor.shutdown();
    local.shutdown();
  }

  public void deployLocal(String space) {
    local.getDeploymentTargets(space, (target) -> {
      try {
        // notify the client of an update
        local.notifyDeployment(target, space);
      } catch (Exception ex) {
        LOG.error("failed-deployment-write", ex);
      }
    });
  }

  public void deployCrossRegion(AuthenticatedUser who, String space) {
    // TODO: find remote instances, and do a cross-region deployment...
    // TODO: also have each Adama host periodically download all spaces, and check if a deployment is needed
  }

  public void waitForCapacity(String space, int timeout, Consumer<Boolean> finished) {
    local.waitForCapacity(space, timeout, finished);
  }

  public void reflect(String space, String key, Callback<String> callback) {
    local.reflect(space, key, callback);
  }

  public void create(AuthenticatedUser user, String space, String key, String entropy, String arg, Callback<Void> callback) {
    local.create(user.context.remoteIp, user.context.origin, user.who.agent, user.who.authority, space, key, entropy, arg, callback);
  }

  public void directSend(AuthenticatedUser user, String space, String key, String marker, String channel, String message, Callback<Integer> callback) {
    finder.find(new Key(space, key), new Callback<>() {
      @Override
      public void success(FinderService.Result value) {
        if (value.location == FinderService.Location.Machine) {
          if (region.equals(value.region)) {
            local.directSend(user.context.remoteIp, user.context.origin, user.who.agent, user.who.authority, space, key, marker, channel, message, callback);
          } else {
            String identity = user.asIdentity(keyId, privateKey);
            // TODO: route to remote remote
            callback.failure(new ErrorCodeException(-123));
          }
        } else {
          local.directSend(user.context.remoteIp, user.context.origin, user.who.agent, user.who.authority, space, key, marker, channel, message, callback);
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  public void delete(AuthenticatedUser user, String space, String key, Callback<Void> callback) {
    finder.find(new Key(space, key), new Callback<>() {
      @Override
      public void success(FinderService.Result value) {
        if (value.location == FinderService.Location.Machine) {
          if (region.equals(value.region)) {
            local.delete(user.context.remoteIp, user.context.origin, user.who.agent, user.who.authority, space, key, callback);
          } else {
            String identity = user.asIdentity(keyId, privateKey);
            // TODO: route to remote remote
            callback.failure(new ErrorCodeException(-123));
          }
        } else {
          local.delete(user.context.remoteIp, user.context.origin, user.who.agent, user.who.authority, space, key, callback);
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });

  }

  public AdamaStream connect(AuthenticatedUser user, String space, String key, String viewerState, SimpleEvents events) {
    DelayAdamaStream stream = new DelayAdamaStream(executor, local.metrics.multi_region_find);
    finder.find(new Key(space, key), new Callback<>() {
      @Override
      public void success(FinderService.Result value) {
        if (value.location == FinderService.Location.Machine) {
          if (region.equals(value.region)) {
            stream.ready(local.connect(value.machine, user.context.remoteIp, user.context.origin, user.who.agent, user.who.authority, space, key, viewerState, user.context.assetKey, events));
          } else {
            String identity = user.asIdentity(keyId, privateKey);
            // TODO: USE THE REGION CLIENT
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

  public void authorize(String ip, String origin, String space, String key, String username, String password, Callback<String> callback) {
    local.authorize(ip, origin, space, key, username, password, callback);
  }
}
