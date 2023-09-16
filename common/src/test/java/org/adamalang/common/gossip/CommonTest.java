/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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


import org.adamalang.common.gossip.codec.GossipProtocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

public class CommonTest {


  public static Instance A() {
    return new Instance(newBuilder().setCounter(100).setId("id-a").setIp("ip1").setRole("proxy").setPort(123).setMonitoringPort(200).build(), 0, true);
  }

  public static EndpointBuilder newBuilder() {
    return new EndpointBuilder();
  }

  public static Instance B() {
    return new Instance(newBuilder().setCounter(200).setId("id-b").setIp("ip2").setRole("proxy").setPort(234).setMonitoringPort(201).build(), 0, false);
  }

  public static Instance C() {
    return new Instance(newBuilder().setCounter(300).setId("id-c").setIp("ip1").setRole("proxy").setPort(345).setMonitoringPort(202).build(), 0, false);
  }

  public static Instance D() {
    return new Instance(newBuilder().setCounter(400).setId("id-d").setIp("ip2").setRole("proxy").setPort(456).setMonitoringPort(203).build(), 0, false);
  }

  public static TreeSet<Instance> INSTANCES(Instance... instances) {
    TreeSet<Instance> set = new TreeSet<>();
    Collections.addAll(set, instances);
    return set;
  }

  public static GossipProtocol.Endpoint[] ENDPOINTS(Instance... instances) {
    ArrayList<GossipProtocol.Endpoint> set = new ArrayList<>();
    for (Instance instance : instances) {
      set.add(instance.toEndpoint());
    }
    return set.toArray(new GossipProtocol.Endpoint[set.size()]);
  }

  public int[] counters(int... values) {
    return values;
  }

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
}
