package org.adamalang.grpc.client.routing;

import org.adamalang.grpc.client.contracts.SpaceTrackingEvents;
import org.adamalang.grpc.proto.InventoryRecord;
import org.adamalang.runtime.contracts.Key;

import java.util.*;
import java.util.function.Consumer;

/** the routing table which maps keys to targets for use by clients */
public class RoutingTable {
  /** raw targets held onto for differentiation */
  private HashMap<String, Collection<InventoryRecord>> targets;

  /** the routing table of spaces to their tables */
  private HashMap<String, SpaceState> routing;

  /** what happens when a space is created and destroyed */
  private final SpaceTrackingEvents events;

  public RoutingTable(SpaceTrackingEvents events) {
    this.targets = new HashMap<>();
    this.routing = new HashMap<>();
    this.events = events;
  }

  /** a target has reported their inventory */
  public void integrate(String target, Collection<InventoryRecord> newRecords) {
    // there are two ways for the routing table to update
    Collection<InventoryRecord> records = targets.get(target);
    if (records == null) {
      // the state doesn't exist, so we will simply integrate the changes
      targets.put(target, newRecords);
      for (InventoryRecord record : newRecords) {
        getOrCreateSpaceState(record.getSpace()).add(target, record, true);
      }
    } else {
      TreeSet<String> requireCompute = new TreeSet<>();
      for (InventoryRecord record : records) {
        requireCompute.add(record.getSpace());
        getOrCreateSpaceState(record.getSpace()).subtract(target, record, false);
      }
      targets.put(target, newRecords);
      for (InventoryRecord record : newRecords) {
        boolean contains = requireCompute.remove(record.getSpace());
        getOrCreateSpaceState(record.getSpace()).add(target, record, !contains);
      }
      for (String space : requireCompute) {
        getOrCreateSpaceState(space).invalidate();
      }
    }
  }

  /** a target is no longer present */
  public void remove(String target) {
    Collection<InventoryRecord> state = targets.remove(target);
    if (state != null) {
      for (InventoryRecord record : state) {
        getOrCreateSpaceState(record.getSpace()).subtract(target, record, true);
      }
    }
  }

  /** broadcast to subscribers that the table has convergence (expensive recompute) */
  public void broadcast() {
    Iterator<Map.Entry<String, SpaceState>> it = routing.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<String, SpaceState> entry = it.next();
      if (entry.getValue().recompute((set) -> events.shareTargetsFor(entry.getKey(), set)) == 0) {
        events.lostInterestInSpace(entry.getKey());
        it.remove();
      }
    }
  }

  /** subscribe to a key */
  public void subscribe(Key key, Consumer<String> target) {
    getOrCreateSpaceState(key.space).subscribe(key.key, target);
  }

  /** unsubscribe a key */
  public void unsubscribe(Key key) {
    getOrCreateSpaceState(key.space).unsubscribe(key.key);
  }

  private SpaceState getOrCreateSpaceState(String space) {
    SpaceState state = routing.get(space);
    if (state == null) {
      state = new SpaceState();
      routing.put(space, state);
      events.gainInterestInSpace(space);
    }
    return state;
  }
}
