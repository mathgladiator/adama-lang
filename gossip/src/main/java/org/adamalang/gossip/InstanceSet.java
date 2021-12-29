/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.gossip;

import org.adamalang.gossip.proto.Endpoint;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

/** a collection of instances */
public class InstanceSet {
  private final String hash;
  private final ArrayList<Instance> instances;
  private final TreeSet<String> ids;

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

  public ArrayList<Integer> counters() {
    ArrayList<Integer> list = new ArrayList<>(instances.size());
    for (Instance instance : instances) {
      list.add(instance.counter());
    }
    return list;
  }

  public ArrayList<Endpoint> toEndpoints() {
    ArrayList<Endpoint> endpoints = new ArrayList<>();
    for (Instance instance : instances) {
      endpoints.add(instance.toEndpoint());
    }
    return endpoints;
  }

  public ArrayList<Endpoint> missing(InstanceSet prior) {
    ArrayList<Endpoint> eps = new ArrayList<>();
    for (Instance local : instances) {
      if (!prior.ids.contains(local.id)) {
        eps.add(local.toEndpoint());
      }
    }
    return eps;
  }

  public void ingest(Collection<Integer> counters, long now) {
    if (instances.size() == counters.size()) {
      Iterator<Instance> instanceIt = instances.iterator();
      Iterator<Integer> counterIt = counters.iterator();
      while (instanceIt.hasNext() && counterIt.hasNext()) {
        instanceIt.next().absorb(counterIt.next(), now);
      }
    }
  }

  public String hash() {
    return hash;
  }

  public ArrayList<String> targetsFor(String role) {
    ArrayList<String> targets = new ArrayList<>();
    for (Instance instance : instances) {
      if (role.equals(instance.role())) {
        targets.add(instance.target());
      }
    }
    return targets;
  }
}
