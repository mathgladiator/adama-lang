/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.gossip;


import org.adamalang.common.gossip.codec.GossipProtocol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

public class CommonTest {


  public static class EndpointBuilder {
    public GossipProtocol.Endpoint endpoint;

    public EndpointBuilder() {
      this.endpoint = new GossipProtocol.Endpoint();
    }

    public EndpointBuilder setCounter(int c) {
      this.endpoint.counter = c;
      return this;
    }

    public EndpointBuilder setId(String id) {
      this.endpoint.id = id;
      return this;
    }

    public EndpointBuilder setIp(String ip) {
      this.endpoint.ip = ip;
      return this;
    }

    public EndpointBuilder setRole(String role) {
      this.endpoint.role = role;
      return this;
    }

    public EndpointBuilder setPort(int port) {
      this.endpoint.port = port;
      return this;
    }

    public EndpointBuilder setMonitoringPort(int monitoringPort) {
      this.endpoint.monitoringPort = monitoringPort;
      return this;
    }

    public GossipProtocol.Endpoint build() {
      return endpoint;
    }
  }

  public static EndpointBuilder newBuilder() {
    return new EndpointBuilder();
  }

  public static Instance A() {
    return new Instance(
        newBuilder()
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
        newBuilder()
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
        newBuilder()
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
        newBuilder()
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

  public static Collection<GossipProtocol.Endpoint> ENDPOINTS(Instance... instances) {
    ArrayList<GossipProtocol.Endpoint> set = new ArrayList<>();
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
