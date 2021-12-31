package org.adamalang.grpc.client.routing;

import org.adamalang.grpc.proto.InventoryRecord;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class SpaceStateTests {
  @Test
  public void flow() {
    SpaceState state = new SpaceState();
    ArrayList<String> pub = new ArrayList<>();
    state.subscribe("key", pub::add);
    state.add("x", InventoryRecord.newBuilder().setPlanHash("hash").setMessages(1000).setCount(1).setCpuTicks(123).build(), true);
    state.recompute();
    state.add("y", InventoryRecord.newBuilder().setPlanHash("hash").setMessages(1000).setCount(10).setMemoryBytes(5000).setCpuTicks(10000).build(), true);
    state.add("z", InventoryRecord.newBuilder().setPlanHash("hash").setMessages(1000).setCount(100).setMemoryBytes(2000).build(), true);
    state.add("t", InventoryRecord.newBuilder().setPlanHash("hash").setMessages(1000).setCount(1000).setMemoryBytes(7000).build(), true);
    state.recompute();
    Assert.assertEquals(1111, state.count);
    Assert.assertEquals(10123, state.cpu);
    Assert.assertEquals(14000, state.memory);
    Assert.assertEquals(4000, state.messages);
    Assert.assertEquals(3, pub.size());
    Assert.assertNull(pub.get(0));
    Assert.assertEquals("x", pub.get(1));
    Assert.assertEquals("z", pub.get(2));
    state.subtract("y", InventoryRecord.newBuilder().setPlanHash("hash").setMessages(1000).setCount(10).setMemoryBytes(2000).setCpuTicks(10000).build(), true);
    state.recompute();
    Assert.assertEquals(3, pub.size());
    state.invalidate();
    state.recompute();
    Assert.assertEquals(3, pub.size());
    Assert.assertEquals(1101, state.count);
    Assert.assertEquals(123, state.cpu);
    Assert.assertEquals(12000, state.memory);
    Assert.assertEquals(3000, state.messages);
    state.unsubscribe("key");
    state.subtract("z", InventoryRecord.newBuilder().setPlanHash("hash").setMessages(1000).setCount(100).setMemoryBytes(2000).build(), true);
    state.recompute();
    Assert.assertEquals(3, pub.size());
    state.subscribe("key", pub::add);
    Assert.assertEquals(4, pub.size());
    Assert.assertEquals("x", pub.get(3));
  }
}
