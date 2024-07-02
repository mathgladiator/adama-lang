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
