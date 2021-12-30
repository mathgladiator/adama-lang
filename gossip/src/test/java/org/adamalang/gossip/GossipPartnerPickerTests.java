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


}
