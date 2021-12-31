package org.adamalang.grpc.client.routing;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class TargetSubscriberTests {
  @Test
  public void flow() {
    ArrayList<String> decisions = new ArrayList<>();
    TargetSubscriber subscriber = new TargetSubscriber(decisions::add, "x");
    subscriber.set("y");
    subscriber.set(null);
    subscriber.set(null);
    subscriber.set(null);
    subscriber.set("x");
    subscriber.set("x");
    subscriber.set("x");
    subscriber.set("x");
    subscriber.set("x");
    Assert.assertEquals(4, decisions.size());
    Assert.assertEquals("x", decisions.get(0));
    Assert.assertEquals("y", decisions.get(1));
    Assert.assertEquals(null, decisions.get(2));
    Assert.assertEquals("x", decisions.get(3));
  }
}
