/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
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
