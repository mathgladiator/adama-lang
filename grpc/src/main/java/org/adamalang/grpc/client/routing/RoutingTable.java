package org.adamalang.grpc.client.routing;

import org.adamalang.grpc.client.contracts.SpaceTrackingEvents;
import org.adamalang.runtime.contracts.Key;

import java.util.*;
import java.util.function.Consumer;

/** the routing table which maps keys to targets for use by clients */
public class RoutingTable {
  /** raw targets held onto for differentiation */
  private HashMap<String, TreeSet<String>> history;

  /** the routing table of spaces to their tables */
  private HashMap<String, SpaceState> routing;

  /** what happens when a space is created and destroyed */
  private final SpaceTrackingEvents events;

  public RoutingTable(SpaceTrackingEvents events) {
    this.history = new HashMap<>();
    this.routing = new HashMap<>();
    this.events = events;
  }

  /** a target has reported their inventory */
  public void integrate(String target, Collection<String> spaces) {
    // there are two ways for the routing table to update
    TreeSet<String> prior = history.get(target);
    if (prior == null) {
      // the state doesn't exist, so we will simply integrate the changes
      history.put(target, new TreeSet<>(spaces));
      for (String space : spaces) {
        getOrCreateSpaceState(space).add(target);
      }
    } else {
      for (String space : spaces) {
        if (!prior.remove(space)) {
          getOrCreateSpaceState(space).add(target);
          events.gainInterestInSpace(space);
        }
      }
      for (String space : prior) {
        getOrCreateSpaceState(space).subtract(target);
        events.lostInterestInSpace(space);
      }
      history.put(target, new TreeSet<>(spaces));
    }
  }

  /** a target is no longer present */
  public void remove(String target) {
    TreeSet<String> spaces = history.remove(target);
    if (spaces != null) {
      for (String space : spaces) {
        getOrCreateSpaceState(space).subtract(target);
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
  public Runnable subscribe(Key key, Consumer<String> target) {
    return getOrCreateSpaceState(key.space).subscribe(key.key, target);
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
