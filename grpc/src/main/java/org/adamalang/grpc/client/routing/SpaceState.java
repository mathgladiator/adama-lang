package org.adamalang.grpc.client.routing;

import org.adamalang.common.Hashing;
import org.adamalang.grpc.proto.InventoryRecord;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

/** the collection of targets for a single space; core element of the routing table */
public class SpaceState {
  public long count;
  public long cpu;
  public long memory;
  public long messages;

  private TreeMap<String, String> targetsToPlan;

  private HashMap<String, TargetSubscriber> subscribers;
  private boolean invalid;

  public SpaceState() {
    this.count = 0;
    this.cpu = 0;
    this.memory = 0;
    this.messages = 0;
    this.targetsToPlan = new TreeMap<>();
    this.subscribers = new HashMap<>();
    this.invalid = false;
  }

  public void subtract(String target, InventoryRecord record, boolean invalidate) {
    targetsToPlan.remove(target);
    count -= record.getCount();
    cpu -= record.getCpuTicks();
    memory -= record.getMemoryBytes();
    messages -= record.getMessages();
    if (invalidate) {
      invalid = true;
    }
  }

  public void add(String target, InventoryRecord record, boolean invalidate) {
    targetsToPlan.put(target, record.getPlanHash());
    count += record.getCount();
    cpu += record.getCpuTicks();
    memory += record.getMemoryBytes();
    messages += record.getMessages();
    if (invalidate) {
      invalid = true;
    }
  }

  public void invalidate() {
    this.invalid = true;
  }

  private String pick(String key) {
    String winner = null;
    String winningHash = "";
    byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
    for (String target : targetsToPlan.keySet()) {
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

  public void recompute() {
    if (invalid) {
      for (Map.Entry<String, TargetSubscriber> entry : subscribers.entrySet()) {
        entry.getValue().set(pick(entry.getKey()));
      }
      invalid = false;
    }
  }

  public void subscribe(String key, Consumer<String> callback) {
    TargetSubscriber subscriber = new TargetSubscriber(callback, pick(key));
    this.subscribers.put(key, subscriber);
  }

  public void unsubscribe(String key) {
    this.subscribers.remove(key);
  }
}
