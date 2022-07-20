/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.gossip;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Random;

public class GossipPartnerPickerTests {
  @Test
  public void flow_empty() {
    MockTime time = new MockTime();
    InstanceSetChain chain = new InstanceSetChain(time);
    GossipPartnerPicker picker =
        new GossipPartnerPicker("127.0.0.1", chain, new HashSet<>(), new Random());
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
}
