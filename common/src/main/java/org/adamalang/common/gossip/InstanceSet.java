/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
