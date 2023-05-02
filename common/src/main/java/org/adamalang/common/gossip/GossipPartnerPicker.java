/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
