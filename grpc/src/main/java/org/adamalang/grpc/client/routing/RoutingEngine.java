package org.adamalang.grpc.client.routing;

import org.adamalang.grpc.client.contracts.SpaceTrackingEvents;
import org.adamalang.grpc.proto.InventoryRecord;
import org.adamalang.runtime.contracts.Key;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class RoutingEngine {
  private final Executor executor;
  private final RoutingTable table;
  private final HashMap<Key, ArrayList<Consumer<String>>> subscribersByKey;

  public RoutingEngine(Executor executor) {
    this.executor = executor;
    this.table = new RoutingTable(new SpaceTrackingEvents() {
      @Override
      public void gainInterestInSpace(String space) {

      }

      @Override
      public void shareTargetsFor(String space, Set<String> targets) {

      }

      @Override
      public void lostInterestInSpace(String space) {

      }
    });
    this.subscribersByKey = new HashMap<>();
  }

  public void integrate(String target, Collection<InventoryRecord> newRecords) {
    executor.execute(() -> {
      table.integrate(target, newRecords);
    });
  }

  public void remove(String target) {
    executor.execute(() -> {
      table.remove(target);
    });
  }

  public void broadcast() {
    executor.execute(() -> {
      table.broadcast();
    });
  }

  public void subscribe(Key key, Consumer<String> subscriber, Consumer<Runnable> onCancel) {
    executor.execute(() -> {
      ArrayList<Consumer<String>> subscribers = subscribersByKey.get(key);
      {
        if (subscribers == null) {
          subscribers = new ArrayList<>();
          subscribersByKey.put(key, subscribers);
          ArrayList<Consumer<String>> _subscribers = subscribers;
          table.subscribe(
              key,
              (target) -> {
                for (Consumer<String> single : _subscribers) {
                  single.accept(target);
                }
              });
        }
      }
      subscribers.add(subscriber);
      ArrayList<Consumer<String>> _subscribers = subscribers;
      onCancel.accept(() -> {
        _subscribers.remove(subscriber);
        if (_subscribers.size() == 0) {
          table.unsubscribe(key);
          subscribersByKey.remove(key);
        }
      });
    });
  }
}
