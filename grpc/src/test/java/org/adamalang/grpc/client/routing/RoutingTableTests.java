package org.adamalang.grpc.client.routing;

import org.adamalang.grpc.proto.InventoryRecord;
import org.adamalang.runtime.contracts.Key;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

public class RoutingTableTests {
  @Test
  public void sanity_flow() {
    RoutingTable table = new RoutingTable();
    ArrayList<String> decisions = new ArrayList<>();
    table.subscribe(new Key("space", "key"), (x) -> decisions.add(x));
    Assert.assertEquals(1, decisions.size());
    Assert.assertNull(decisions.get(0));
    decisions.clear();
    table.integrate("t1", Collections.singleton(InventoryRecord.newBuilder().setSpace("space").setCpuTicks(100).setMessages(1000).setCount(123).setPlanHash("hash").build()));
    Assert.assertEquals(0, decisions.size());
    table.broadcast();
    Assert.assertEquals(1, decisions.size());
    table.remove("t1");
    Assert.assertEquals(1, decisions.size());
    table.broadcast();
    Assert.assertEquals(2, decisions.size());
    Assert.assertEquals("t1", decisions.get(0));
    Assert.assertNull(decisions.get(1));
    table.unsubscribe(new Key("space", "key"));
    table.integrate("t1", Collections.singleton(InventoryRecord.newBuilder().setSpace("space").setCpuTicks(100).setMessages(1000).setCount(123).setPlanHash("hash").build()));
    Assert.assertEquals(2, decisions.size());
    table.broadcast();
    Assert.assertEquals(2, decisions.size());
  }

  @Test
  public void singleKeyAgainstMany() {
    RoutingTable table = new RoutingTable();
    ArrayList<String> decisions = new ArrayList<>();
    table.subscribe(new Key("space", "key"), (x) -> decisions.add(x));
    Assert.assertEquals(1, decisions.size());
    Assert.assertNull(decisions.get(0));
    decisions.clear();
    Assert.assertEquals(0, decisions.size());
    table.integrate("t1", Collections.singleton(InventoryRecord.newBuilder().setSpace("space").setCpuTicks(100).setMessages(1000).setCount(123).setPlanHash("hash").build()));
    table.integrate("t2", Collections.singleton(InventoryRecord.newBuilder().setSpace("space").setCpuTicks(200).setMessages(11000).setCount(1230).setPlanHash("hash").build()));
    table.integrate("t3", Collections.singleton(InventoryRecord.newBuilder().setSpace("space").setCpuTicks(300).setMessages(21000).setCount(12300).setPlanHash("hash").build()));
    Assert.assertEquals(0, decisions.size());
    table.broadcast();
    Assert.assertEquals(1, decisions.size());
    Assert.assertEquals("t3", decisions.get(0));
    table.remove("t3");
    Assert.assertEquals(1, decisions.size());
    table.broadcast();
    Assert.assertEquals(2, decisions.size());
    Assert.assertEquals("t2", decisions.get(1));
  }

  @Test
  public void multiKeysWithRebalanceByDeath() {
    RoutingTable table = new RoutingTable();
    ArrayList<String> decisions = new ArrayList<>();
    for (int k = 0; k < 100; k++) {
      table.subscribe(new Key("space", "key-" + k), (x) -> decisions.add(x));
      Assert.assertEquals(1, decisions.size());
      Assert.assertNull(decisions.get(0));
      decisions.clear();
    }
    Assert.assertEquals(0, decisions.size());
    table.integrate("t1", Collections.singleton(InventoryRecord.newBuilder().setSpace("space").setCpuTicks(100).setMessages(1000).setCount(123).setPlanHash("hash").build()));
    table.integrate("t2", Collections.singleton(InventoryRecord.newBuilder().setSpace("space").setCpuTicks(200).setMessages(11000).setCount(1230).setPlanHash("hash").build()));
    table.integrate("t3", Collections.singleton(InventoryRecord.newBuilder().setSpace("space").setCpuTicks(300).setMessages(21000).setCount(12300).setPlanHash("hash").build()));
    Assert.assertEquals(0, decisions.size());
    table.broadcast();
    Assert.assertEquals(100, decisions.size());
    {
      int t1Count = 0;
      int t2Count = 0;
      int t3Count = 0;
      for (String decision : decisions) {
        if ("t1".equals(decision)) {
          t1Count++;
        } else if ("t2".equals(decision)) {
          t2Count++;
        } else if ("t3".equals(decision)) {
          t3Count++;
        } else {
          Assert.fail();
        }
      }
      Assert.assertEquals(31, t1Count);
      Assert.assertEquals(32, t2Count);
      Assert.assertEquals(37, t3Count);
    }
    decisions.clear();
    table.remove("t3");
    Assert.assertEquals(0, decisions.size());
    table.broadcast();
    Assert.assertEquals(37, decisions.size());
    {
      int t1Count = 0;
      int t2Count = 0;
      for (String decision : decisions) {
        if ("t1".equals(decision)) {
          t1Count++;
        } else if ("t2".equals(decision)) {
          t2Count++;
        } else {
          Assert.fail();
        }
      }
      Assert.assertEquals(19, t1Count);
      Assert.assertEquals(18, t2Count);
    }
  }

  @Test
  public void multiKeysWithRebalanceByShift() {
    RoutingTable table = new RoutingTable();
    ArrayList<String> decisions = new ArrayList<>();
    for (int k = 0; k < 100; k++) {
      table.subscribe(new Key("space", "key-" + k), (x) -> decisions.add(x));
      Assert.assertEquals(1, decisions.size());
      Assert.assertNull(decisions.get(0));
      decisions.clear();
    }
    Assert.assertEquals(0, decisions.size());
    table.integrate("t1", Collections.singleton(InventoryRecord.newBuilder().setSpace("space").setCpuTicks(100).setMessages(1000).setCount(123).setPlanHash("hash").build()));
    table.integrate("t2", Collections.singleton(InventoryRecord.newBuilder().setSpace("space").setCpuTicks(200).setMessages(11000).setCount(1230).setPlanHash("hash").build()));
    table.integrate("t3", Collections.singleton(InventoryRecord.newBuilder().setSpace("space").setCpuTicks(300).setMessages(21000).setCount(12300).setPlanHash("hash").build()));
    Assert.assertEquals(0, decisions.size());
    table.broadcast();
    Assert.assertEquals(100, decisions.size());
    {
      int t1Count = 0;
      int t2Count = 0;
      int t3Count = 0;
      for (String decision : decisions) {
        if ("t1".equals(decision)) {
          t1Count++;
        } else if ("t2".equals(decision)) {
          t2Count++;
        } else if ("t3".equals(decision)) {
          t3Count++;
        } else {
          Assert.fail();
        }
      }
      Assert.assertEquals(31, t1Count);
      Assert.assertEquals(32, t2Count);
      Assert.assertEquals(37, t3Count);
    }
    decisions.clear();
    table.integrate("t3", Collections.singleton(InventoryRecord.newBuilder().setSpace("different").setCpuTicks(300).setMessages(21000).setCount(12300).setPlanHash("hash").build()));
    Assert.assertEquals(0, decisions.size());
    table.broadcast();
    Assert.assertEquals(37, decisions.size());
    {
      int t1Count = 0;
      int t2Count = 0;
      for (String decision : decisions) {
        if ("t1".equals(decision)) {
          t1Count++;
        } else if ("t2".equals(decision)) {
          t2Count++;
        } else {
          Assert.fail();
        }
      }
      Assert.assertEquals(19, t1Count);
      Assert.assertEquals(18, t2Count);
    }
  }

  @Test
  public void multiKeysWithRebalanceByAdd() {
    RoutingTable table = new RoutingTable();
    ArrayList<String> decisions = new ArrayList<>();
    for (int k = 0; k < 100; k++) {
      table.subscribe(new Key("space", "key-" + k), (x) -> decisions.add(x));
      Assert.assertEquals(1, decisions.size());
      Assert.assertNull(decisions.get(0));
      decisions.clear();
    }
    Assert.assertEquals(0, decisions.size());
    table.integrate("t1", Collections.singleton(InventoryRecord.newBuilder().setSpace("space").setCpuTicks(100).setMessages(1000).setCount(123).setPlanHash("hash").build()));
    table.integrate("t2", Collections.singleton(InventoryRecord.newBuilder().setSpace("space").setCpuTicks(200).setMessages(11000).setCount(1230).setPlanHash("hash").build()));
    Assert.assertEquals(0, decisions.size());
    table.broadcast();
    Assert.assertEquals(100, decisions.size());
    {
      int t1Count = 0;
      int t2Count = 0;
      for (String decision : decisions) {
        if ("t1".equals(decision)) {
          t1Count++;
        } else if ("t2".equals(decision)) {
          t2Count++;
        } else {
          Assert.fail();
        }
      }
      Assert.assertEquals(50, t1Count);
      Assert.assertEquals(50, t2Count);
    }
    decisions.clear();
    table.integrate("t3", Collections.singleton(InventoryRecord.newBuilder().setSpace("space").setCpuTicks(300).setMessages(21000).setCount(12300).setPlanHash("hash").build()));
    Assert.assertEquals(0, decisions.size());
    table.broadcast();
    Assert.assertEquals(37, decisions.size());
    {
      int t1Count = 0;
      int t2Count = 0;
      int t3Count = 0;
      for (String decision : decisions) {
        if ("t1".equals(decision)) {
          t1Count++;
        } else if ("t2".equals(decision)) {
          t2Count++;
        } else if ("t3".equals(decision)) {
          t3Count++;
        } else {
          Assert.fail();
        }
      }
      Assert.assertEquals(0, t1Count);
      Assert.assertEquals(0, t2Count);
      Assert.assertEquals(37, t3Count);
    }
  }
}
