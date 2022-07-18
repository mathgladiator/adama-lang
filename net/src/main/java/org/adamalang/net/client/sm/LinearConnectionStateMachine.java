package org.adamalang.net.client.sm;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.queue.ItemQueue;
import org.adamalang.net.client.InstanceClient;
import org.adamalang.net.client.contracts.Events;
import org.adamalang.net.client.contracts.Remote;
import org.adamalang.net.client.contracts.RoutingSubscriber;
import org.adamalang.net.client.contracts.SimpleEvents;
import org.adamalang.runtime.data.Key;

/** Assumes a fixed routing decision and heads until first failure */
public class LinearConnectionStateMachine {
  // these can be put under a base
  private final ConnectionBase base;
  // these are critical to the request (i.e they are the request)
  private final String ip;
  private final String origin;
  private final String agent;
  private final String authority;
  private final Key key;
  private final String assetKey;
  private final SimpleEvents events;
  private String viewerState;
  // the buffer of actions to execute once we have a remote
  private final ItemQueue<Remote> queue;

  public LinearConnectionStateMachine(ConnectionBase base, String ip, String origin, String agent, String authority, String space, String key, String viewerState, String assetKey, SimpleEvents events) {
    this.base = base;
    this.ip = ip;
    this.origin = origin;
    this.agent = agent;
    this.authority = authority;
    this.key = new Key(space, key);
    this.viewerState = viewerState;
    this.assetKey = assetKey;
    this.events = events;
    this.queue = new ItemQueue<>(base.executor, base.config.getConnectionQueueSize(), base.config.getConnectionQueueTimeoutMS());
  }

  public void send() {
  }

  // TODO: update view state

  private void handleError(int code) {
  }

  public void open() {
    base.router.get(key, new RoutingSubscriber() {
      @Override
      public void onRegion(String region) {
        handleError(-1);
      }

      @Override
      public void onMachine(String machine) {
        base.mesh.find(target, new Callback<InstanceClient>() {
          @Override
          public void success(InstanceClient value) {
            value.connect(ip, origin, agent, authority, key.space, key.key, viewerState, assetKey, new Events() {
              @Override
              public void connected(Remote remote) {
                queue.ready(remote);
              }

              @Override
              public void delta(String data) {
                events.delta(data);
              }

              @Override
              public void error(int code) {
                events.error(code);
              }

              @Override
              public void disconnected() {
                handleError(-3); /* Unexpected disconnect */
              }
            });
          }

          @Override
          public void failure(ErrorCodeException ex) {
            events.error(ex.code);
            // TODO: LOG
          }
        });
      }

      @Override
      public void failure(ErrorCodeException ex) {
        events.error(ex.code);
      }
    });
  }

}
