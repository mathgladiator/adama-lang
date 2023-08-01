/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common.gossip;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class GossipPartnerPickerTests {
  @Test
  public void flow_empty() {
    MockTime time = new MockTime();
    InstanceSetChain chain = new InstanceSetChain(time);
    GossipPartnerPicker picker = new GossipPartnerPicker("127.0.0.1", chain, new HashSet<>(), new Random());
    Assert.assertNull(picker.pick());
  }

  @Test
  public void flow_only_self() {
    MockTime time = new MockTime();
    InstanceSetChain chain = new InstanceSetChain(time);
    HashSet<String> set = new HashSet<>();
    set.add("127.0.0.1");
    GossipPartnerPicker picker = new GossipPartnerPicker("127.0.0.1", chain, set, new Random());
    Assert.assertNull(picker.pick());
  }

  @Test
  public void flow_balance() {
    MockTime time = new MockTime();
    InstanceSetChain chain = new InstanceSetChain(time);
    HashSet<String> set = new HashSet<>();
    set.add("127.0.0.1");
    set.add("127.0.0.2");
    set.add("127.0.0.3");
    set.add("127.0.0.4");
    set.add("127.0.0.5");
    GossipPartnerPicker picker = new GossipPartnerPicker("127.0.0.1", chain, set, new Random());
    HashMap<String, Integer> counts = new HashMap<>();
    for (int k = 0; k < 100; k++) {
      String host = picker.pick();
      Integer prior = counts.get(host);
      if (prior == null) {
        counts.put(host, 1);
      } else {
        counts.put(host, prior + 1);
      }
    }
    int sum = 0;
    int max = -1;
    for (Integer val : counts.values()) {
      System.out.println(val);
      sum += val;
      max = Math.max(val, max);
    }
    Assert.assertEquals(sum, 100);
    Assert.assertTrue(max <= 40); // should be ~ 25
  }
}
