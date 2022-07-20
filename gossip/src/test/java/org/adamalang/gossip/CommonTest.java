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

import org.adamalang.gossip.proto.Endpoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

public class CommonTest {
  public static Instance A() {
    return new Instance(
        Endpoint.newBuilder()
            .setCounter(100)
            .setId("id-a")
            .setIp("ip1")
            .setRole("proxy")
            .setPort(123)
            .setMonitoringPort(200)
            .build(),
        0);
  }

  public static Instance B() {
    return new Instance(
        Endpoint.newBuilder()
            .setCounter(200)
            .setId("id-b")
            .setIp("ip2")
            .setRole("proxy")
            .setPort(234)
            .setMonitoringPort(201)
            .build(),
        0);
  }

  public static Instance C() {
    return new Instance(
        Endpoint.newBuilder()
            .setCounter(300)
            .setId("id-c")
            .setIp("ip1")
            .setRole("proxy")
            .setPort(345)
            .setMonitoringPort(202)
            .build(),
        0);
  }

  public static Instance D() {
    return new Instance(
        Endpoint.newBuilder()
            .setCounter(400)
            .setId("id-d")
            .setIp("ip2")
            .setRole("proxy")
            .setPort(456)
            .setMonitoringPort(203)
            .build(),
        0);
  }

  public static TreeSet<Instance> INSTANCES(Instance... instances) {
    TreeSet<Instance> set = new TreeSet<>();
    for (Instance instance : instances) {
      set.add(instance);
    }
    return set;
  }

  public static Collection<Endpoint> ENDPOINTS(Instance... instances) {
    ArrayList<Endpoint> set = new ArrayList<>();
    for (Instance instance : instances) {
      set.add(instance.toEndpoint());
    }
    return set;
  }

  public ArrayList<Integer> counters(int... values) {
    ArrayList<Integer> c = new ArrayList<>(values.length);
    for (int val : values) {
      c.add(val);
    }
    return c;
  }
}
