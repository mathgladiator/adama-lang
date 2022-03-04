/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.net.client.routing;

import org.adamalang.net.client.contracts.SpaceTrackingEvents;
import org.adamalang.runtime.data.Key;

import java.util.*;
import java.util.function.Consumer;

/** the routing table which maps keys to targets for use by clients */
public class RoutingTable {
  /** what happens when a space is created and destroyed */
  private final SpaceTrackingEvents events;
  /** raw targets held onto for differentiation */
  private final HashMap<String, TreeSet<String>> history;
  /** the routing table of spaces to their tables */
  private final HashMap<String, SpaceState> routing;

  private final Random rng;

  public RoutingTable(SpaceTrackingEvents events) {
    this.history = new HashMap<>();
    this.routing = new HashMap<>();
    this.events = events;
    this.rng = new Random();
  }

  public TreeSet<String> targetsFor(String space) {
    return getOrCreateSpaceState(space).list();
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

  public String get(String space, String key) {
    return getOrCreateSpaceState(space).pick(key);
  }

  public String random() {
    ArrayList<String> targets = new ArrayList<>(history.keySet());
    if (targets.size() > 0) {
      return targets.get(rng.nextInt(targets.size()));
    }
    return null;
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
        }
      }
      for (String space : prior) {
        getOrCreateSpaceState(space).subtract(target);
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
    TreeSet<String> all = new TreeSet<>();
    for (TreeSet<String> possible : history.values()) {
      all.addAll(possible);
    }
    Iterator<Map.Entry<String, SpaceState>> it = routing.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<String, SpaceState> entry = it.next();
      if (entry.getValue().recompute((set) -> events.shareTargetsFor(entry.getKey(), set))) {
        if (!all.contains(entry.getKey())) {
          events.lostInterestInSpace(entry.getKey());
          it.remove();
        }
      }
    }
  }

  /** subscribe to a key */
  public Runnable subscribe(Key key, Consumer<String> target) {
    return getOrCreateSpaceState(key.space).subscribe(key.key, target);
  }
}
