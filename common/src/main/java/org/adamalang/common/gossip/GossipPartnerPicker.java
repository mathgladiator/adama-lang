/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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

import java.util.*;

/** pick a partner to gossip with */
public class GossipPartnerPicker {
  private final String self;
  private final InstanceSetChain chain;
  private final HashSet<String> initial;
  private final ArrayList<String> peers;
  private final Random rng;
  private final HashMap<String, Integer> counts;
  private String cachedPeersHash;

  public GossipPartnerPicker(String self, InstanceSetChain chain, HashSet<String> initial, Random rng) {
    this.self = self;
    this.chain = chain;
    this.initial = initial;
    this.rng = rng;
    this.peers = new ArrayList<>();
    this.counts = new HashMap<>();
    this.cachedPeersHash = "";
  }

  public String pick() {
    if (!cachedPeersHash.equals(chain.current().hash())) {
      peers.clear();
      TreeSet<String> set = new TreeSet<>(initial);
      set.addAll(chain.current().targetsFor("gossip"));
      peers.addAll(set);
      cachedPeersHash = chain.current().hash();
    }
    if (peers.size() > 0) {
      String a = randomPeerNotSelf();
      String b = randomPeerNotSelf();
      if (a == null || b == null) {
        return null;
      }
      int x = countOf(a);
      int y = countOf(b);
      if (x < y) {
        counts.put(a, x + 1);
        return a;
      } else {
        counts.put(b, y + 1);
        return b;
      }
    } else {
      return null;
    }
  }

  private String randomPeerNotSelf() {
    int attempts = 0;
    while (attempts < 4) {
      String x = peers.get(rng.nextInt(peers.size()));
      if (!self.equals(x)) {
        return x;
      }
      attempts++;
    }
    return null;
  }

  private int countOf(String target) {
    Integer count = counts.get(target);
    if (count == null) {
      return 0;
    }
    return count;
  }
}
