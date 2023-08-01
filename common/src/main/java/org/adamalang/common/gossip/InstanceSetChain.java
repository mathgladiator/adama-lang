/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common.gossip;

import org.adamalang.common.TimeSource;
import org.adamalang.common.gossip.codec.GossipProtocol;

import java.util.*;
import java.util.function.Consumer;

/**
 * This consolidates all the instances and provides a historical chain of history so updates can
 * flow reasonably
 */
public class InstanceSetChain {
  private final TimeSource time;
  private final HashMap<String, Instance> primary;
  private final GarbageMap<InstanceSet> history;
  private final GarbageMap<Instance> recentlyLearnedAbout;
  private final GarbageMap<Instance> recentlyDeleted;
  private InstanceSet current;
  private Consumer<GossipProtocol.Endpoint[]> watcher;

  public InstanceSetChain(TimeSource time) {
    this.time = time;
    this.primary = new HashMap<>();
    this.history = new GarbageMap<>(Constants.MAX_HISTORY);
    this.current = new InstanceSet(new TreeSet<>(), time.nowMilliseconds());
    this.recentlyLearnedAbout = new GarbageMap<>(Constants.MAX_RECENT_ENTRIES);
    this.recentlyDeleted = new GarbageMap<>(Constants.MAX_DELETES);
    this.watcher = null;
  }

  public void setWatcher(Consumer<GossipProtocol.Endpoint[]> watcher) {
    this.watcher = watcher;
    broadcast();
  }

  private void broadcast() {
    if (watcher != null) {
      watcher.accept(all());
    }
  }

  public GossipProtocol.Endpoint[] all() {
    return current.toEndpoints();
  }

  public InstanceSet find(String hash) {
    if (current.hash().equals(hash)) {
      return current;
    }
    return history.get(hash);
  }

  public InstanceSet current() {
    return this.current;
  }

  public GossipProtocol.Endpoint[] recent() {
    ArrayList<GossipProtocol.Endpoint> list = new ArrayList<>();
    Iterator<Instance> instance = recentlyLearnedAbout.iterator();
    while (instance.hasNext()) {
      list.add(instance.next().toEndpoint());
    }
    return list.toArray(new GossipProtocol.Endpoint[list.size()]);
  }

  public long now() {
    return time.nowMilliseconds();
  }

  public GossipProtocol.Endpoint[] missing(InstanceSet set) {
    return current.missing(set);
  }

  public Runnable pick(String id) {
    Instance instance = primary.get(id);
    if (instance != null) {
      return () -> instance.bump(time.nowMilliseconds());
    } else {
      return null;
    }
  }

  public long scan() {
    long now = time.nowMilliseconds();
    long min = now;
    TreeSet<Instance> clone = null;
    Iterator<Map.Entry<String, Instance>> iterator = primary.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<String, Instance> entry = iterator.next();
      Instance instance = entry.getValue();
      if (instance.tooOldMustDelete(now)) {
        if (clone == null) {
          clone = current.clone();
        }
        recentlyDeleted.put(entry.getKey(), instance, now);
        recentlyLearnedAbout.remove(entry.getKey());
        iterator.remove();
        clone.remove(instance);
      } else if (instance.witnessed() < min) {
        min = instance.witnessed();
      }
    }
    if (clone != null) {
      history.put(current.hash(), current, now);
      current = new InstanceSet(clone, now);
      broadcast();
    }
    return min;
  }

  public void gc() {
    long now = time.nowMilliseconds();
    history.gc(now);
    recentlyDeleted.gc(now);
    recentlyLearnedAbout.gc(now);
  }

  public boolean ingest(GossipProtocol.Endpoint[] endpoints, String[] deletes) {
    long now = time.nowMilliseconds();
    TreeSet<Instance> clone = null;
    for (GossipProtocol.Endpoint ep : endpoints) {
      Instance prior = primary.get(ep.id);
      if (prior != null) {
        prior.absorb(ep.counter, now);
      } else {
        Instance newInstance = recentlyDeleted.remove(ep.id);
        if (newInstance == null) {
          newInstance = new Instance(ep, now);
        } else {
          newInstance.absorb(ep.counter, now);
        }
        if (clone == null) {
          clone = current.clone();
        }
        primary.put(ep.id, newInstance);
        clone.add(newInstance);
        recentlyLearnedAbout.put(ep.id, newInstance, now);
      }
    }
    for (String delId : deletes) {
      Instance prior = primary.get(delId);
      if (prior != null) {
        if (prior.canDelete(now)) {
          recentlyLearnedAbout.remove(delId);
          recentlyDeleted.put(delId, prior, now);
          primary.remove(delId);
          if (clone == null) {
            clone = current.clone();
          }
          clone.remove(prior);
        }
      }
    }
    if (clone != null) {
      history.put(current.hash(), current, now);
      current = new InstanceSet(clone, now);
      broadcast();
      return true;
    }
    return false;
  }

  public String[] deletes() {
    Collection<String> keys = recentlyDeleted.keys();
    return keys.toArray(new String[keys.size()]);
  }
}
