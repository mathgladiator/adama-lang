/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.region;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.api.*;
import org.adamalang.common.*;
import org.adamalang.runtime.deploy.Deploy;
import org.adamalang.runtime.deploy.DeploySync;

import java.util.HashMap;
import java.util.Iterator;
import java.util.function.BiConsumer;

/** connect to ide/$space to learn when deployments happen */
public class AdamaDeploymentSync implements DeploySync {
  private final SelfClient client;
  private final String identity;
  private final SimpleExecutor executor;
  private final HashMap<String, SM> stateMachines;
  private final Deploy event;

  public AdamaDeploymentSync(SelfClient client, SimpleExecutor executor, String identity, Deploy event) {
    this.client = client;
    this.executor = executor;
    this.identity = identity;
    this.event = event;
    this.stateMachines = new HashMap<>();
  }

  public class SM {
    private final ClientConnectionCreateRequest request;
    private SelfClient.DocumentStreamHandler handler;
    private boolean alive;
    private final Callback<SelfClient.DocumentStreamHandler> callbackWrite;
    private final Stream<ClientDataResponse> callbackRead;

    private SM(String space) {
      request = new ClientConnectionCreateRequest();
      request.identity = identity;
      request.space = "ide";
      request.key = space;
      request.viewerState = Json.newJsonObject();
      alive = true;
      handler = null;

      callbackWrite = new Callback<SelfClient.DocumentStreamHandler>() {
        @Override
        public void success(SelfClient.DocumentStreamHandler value) {
          executor.execute(new NamedRunnable("set-handler") {
            @Override
            public void execute() throws Exception {
              handler = value;
              if (!alive) {
                sendEnd();
              }
            }
          });
        }

        @Override
        public void failure(ErrorCodeException ex) {

        }
      };

      callbackRead = new Stream<ClientDataResponse>() {
        int backoff = 500;
        int deploymentAt = -1;
        @Override
        public void next(ClientDataResponse value) {
          if (value.delta.has("data")) {
            ObjectNode data = (ObjectNode) value.delta.get("data");
            if (data.has("deployments")) {
              int deploymentsValue = data.get("deployments").intValue();
              if (deploymentsValue > deploymentAt) {
                event.deploy(space, Callback.DONT_CARE_VOID);
              }
              deploymentAt = deploymentsValue;
            }
          }
        }

        @Override
        public void complete() {
        }

        @Override
        public void failure(ErrorCodeException ex) {
          backoff = (int) Math.min(5000, backoff * (1 + Math.random()));
          executor.execute(new NamedRunnable("deploymentsync-failed") {
            @Override
            public void execute() throws Exception {
              handler = null;
              if (alive) {
                executor.schedule(new NamedRunnable("deploymentsync-retry") {
                  @Override
                  public void execute() throws Exception {
                    retry();
                  }
                }, backoff);
              }
            }
          });
        }
      };
    }

    private void sendEnd() {
      handler.end(new ClientConnectionEndRequest(), new Callback<>() {
        @Override
        public void success(ClientSimpleResponse value) {}

        @Override
        public void failure(ErrorCodeException ex) {}
      });
    }

    public void retry() {
      client.connectionCreate(request, callbackWrite, callbackRead);
    }

    public void stop() {
      executor.execute(new NamedRunnable("deploymentsync-stop") {
        @Override
        public void execute() throws Exception {
          alive = false;
          if (handler != null) {
            sendEnd();
          }
        }
      });
    }
  }

  @Override
  public void watch(String space) {
    executor.execute(new NamedRunnable("deploymentsync-watch") {
      @Override
      public void execute() throws Exception {
        if (!stateMachines.containsKey(space)) {
          SM sm = new SM(space);
          stateMachines.put(space, sm);
          sm.retry();
        }
      }
    });
  }

  @Override
  public void unwatch(String space) {
    executor.execute(new NamedRunnable("deploymentsync-unwatch") {
      @Override
      public void execute() throws Exception {
        SM sm = stateMachines.remove(space);
        if (sm != null) {
          sm.stop();
        }
      }
    });
  }

  public void shutdown() {
    executor.execute(new NamedRunnable("deploymentsync-shutdown") {
      @Override
      public void execute() throws Exception {
        Iterator<SM> it = stateMachines.values().iterator();
        while (it.hasNext()) {
          it.next().stop();
          it.remove();
        }
      }
    });
  }
}
