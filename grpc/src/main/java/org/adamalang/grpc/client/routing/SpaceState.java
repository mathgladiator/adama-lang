package org.adamalang.grpc.client.routing;

import org.adamalang.common.Hashing;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

/** the collection of targets for a single space; core element of the routing table */
public class SpaceState {
  private long idgen;
  private final TreeSet<String> targets;
  private final HashMap<String, HashMap<Long, TargetSubscriber>> subscribers;
  private boolean invalid;

  public SpaceState() {
    this.targets = new TreeSet<>();
    this.subscribers = new HashMap<>();
    this.invalid = false;
    this.idgen = 0;
  }

  public void subtract(String target) {
    targets.remove(target);
    invalid = true;
  }

  public void add(String target) {
    targets.add(target);
    invalid = true;
  }

  public boolean recompute(Consumer<Set<String>> share) {
    if (invalid) {
      int count = 0;
      for (Map.Entry<String, HashMap<Long, TargetSubscriber>> entry : subscribers.entrySet()) {
        String target = pick(entry.getKey());
        for (TargetSubscriber subscriber : entry.getValue().values()) {
          count++;
          subscriber.set(target);
        }
      }
      invalid = false;
      share.accept(targets);
      return count == 0;
    } else {
      return false;
    }
  }

  private String pick(String key) {
    String winner = null;
    String winningHash = "";
    byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
    for (String target : targets) {
      MessageDigest digest = Hashing.md5();
      digest.update(target.getBytes(StandardCharsets.UTF_8));
      digest.update(keyBytes);
      String targetHash = Hashing.finishAndEncode(digest);
      if (targetHash.compareTo(winningHash) > 0) {
        winner = target;
        winningHash = targetHash;
      }
    }
    return winner;
  }

  public Runnable subscribe(String key, Consumer<String> callback) {
    TargetSubscriber subscriber = new TargetSubscriber(callback, pick(key));
    long id = idgen++;
    HashMap<Long, TargetSubscriber> map = subscribers.get(key);
    if (map == null) {
      map = new HashMap<>();
      subscribers.put(key, map);
    }
    map.put(id, subscriber);
    HashMap<Long, TargetSubscriber> _map = map;
    return () -> {
      subscriber.set(null);
      _map.remove(id);
      if (_map.size() == 0) {
        subscribers.remove(key);
      }
    };
  }
}
