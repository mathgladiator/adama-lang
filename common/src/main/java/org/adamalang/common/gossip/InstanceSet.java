/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common.gossip;

import org.adamalang.common.Hashing;
import org.adamalang.common.gossip.codec.GossipProtocol;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/** a collection of instances */
public class InstanceSet {
  public final ArrayList<Instance> instances;
  public final TreeSet<String> ids;
  public final String hash;

  public InstanceSet(TreeSet<Instance> instances, long now) {
    this.instances = new ArrayList<>(instances);
    this.ids = new TreeSet<>();
    MessageDigest digest = Hashing.md5();
    for (Instance instance : instances) {
      ids.add(instance.id);
      digest.update(instance.id.getBytes(StandardCharsets.UTF_8));
    }
    this.hash = Hashing.finishAndEncode(digest);
  }

  public TreeSet<Instance> clone() {
    return new TreeSet<>(instances);
  }

  public int[] counters() {
    int[] counters = new int[instances.size()];
    int at = 0;
    for (Instance instance : instances) {
      counters[at] = instance.counter();
      at++;
    }
    return counters;
  }

  public GossipProtocol.Endpoint[] toEndpoints() {
    ArrayList<GossipProtocol.Endpoint> endpoints = new ArrayList<>();
    for (Instance instance : instances) {
      endpoints.add(instance.toEndpoint());
    }
    return endpoints.toArray(new GossipProtocol.Endpoint[endpoints.size()]);
  }

  public GossipProtocol.Endpoint[] missing(InstanceSet prior) {
    ArrayList<GossipProtocol.Endpoint> eps = new ArrayList<>();
    for (Instance local : instances) {
      if (!prior.ids.contains(local.id)) {
        eps.add(local.toEndpoint());
      }
    }
    return eps.toArray(new GossipProtocol.Endpoint[eps.size()]);
  }

  public void ingest(int[] counters, long now) {
    if (instances.size() == counters.length) {
      Iterator<Instance> instanceIt = instances.iterator();
      int at = 0;
      while (instanceIt.hasNext()) {
        instanceIt.next().absorb(counters[at], now);
        at++;
      }
    }
  }

  public String hash() {
    return hash;
  }

  public ArrayList<String> targetsFor(String role) {
    TreeSet<String> targets = new TreeSet<>();
    for (Instance instance : instances) {
      if (role.equals(instance.role())) {
        targets.add(instance.target());
      }
    }
    return new ArrayList<>(targets);
  }
}
