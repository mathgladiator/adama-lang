package org.adamalang.runtime.reactives.tables;

import org.adamalang.runtime.mocks.MockRxParent;
import org.junit.Assert;
import org.junit.Test;

public class TablePubSubTests {
  @Test
  public void flow() {
    MockRxParent par = new MockRxParent();
    TablePubSub pubsub = new TablePubSub(par);
    MockTableSubscription one = new MockTableSubscription();
    MockTableSubscription two = new MockTableSubscription();
    pubsub.subscribe(one);
    pubsub.subscribe(two);
    pubsub.gc();
    pubsub.primary(123);
    pubsub.index(42, 13, 69);
    pubsub.all();
    one.alive = false;
    pubsub.gc();
    pubsub.primary(123);
    pubsub.index(42, 13, 69);
    pubsub.all();
    Assert.assertTrue(pubsub.alive());
    par.alive = false;
    Assert.assertFalse(pubsub.alive());
    Assert.assertEquals(3, one.publishes.size());
    Assert.assertEquals(6, two.publishes.size());
    Assert.assertEquals("PKEY:123", one.publishes.get(0));
    Assert.assertEquals("IDX[42];13=69", one.publishes.get(1));
    Assert.assertEquals("ALL", one.publishes.get(2));
    Assert.assertEquals("PKEY:123", two.publishes.get(0));
    Assert.assertEquals("IDX[42];13=69", two.publishes.get(1));
    Assert.assertEquals("ALL", two.publishes.get(2));
    Assert.assertEquals("PKEY:123", two.publishes.get(3));
    Assert.assertEquals("IDX[42];13=69", two.publishes.get(4));
    Assert.assertEquals("ALL", two.publishes.get(5));
  }
}
