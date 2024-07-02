/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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

import java.util.Objects;

/** an instance represents an application on a host that is heartbeating */
public class Instance implements Comparable<Instance> {
  public final String id;
  public final int monitoringPort;
  public final String ip;
  public final int port;
  public final String role;
  public final long created;
  private int counter;
  private long witness;
  public final boolean local;

  public Instance(GossipProtocol.Endpoint endpoint, long now, boolean local) {
    this.id = endpoint.id;
    this.ip = endpoint.ip;
    this.port = endpoint.port;
    this.monitoringPort = endpoint.monitoringPort;
    this.role = endpoint.role;
    this.counter = endpoint.counter;
    this.witness = now;
    this.created = endpoint.created;
    this.local = local;
  }

  public static int humanizeCompare(Instance x, Instance y) {
    int delta = x.ip.compareTo(y.ip);
    if (delta == 0) {
      return x.role.compareTo(y.role);
    }
    return delta;
  }

  public String role() {
    return role;
  }

  public String target() {
    return ip + ":" + port;
  }

  public int counter() {
    return this.counter;
  }

  public long witnessed() {
    return this.witness;
  }

  public GossipProtocol.Endpoint toEndpoint() {
    GossipProtocol.Endpoint endpoint = new GossipProtocol.Endpoint();
    endpoint.id = id;
    endpoint.ip = ip;
    endpoint.port = port;
    endpoint.monitoringPort = monitoringPort;
    endpoint.role = role;
    endpoint.created = created;
    endpoint.counter = counter;
    return endpoint;
  }

  /**
   * the application should call this every second locally.
   *
   * <p>This means each app's counter will wrap around every 2 billion seconds.
   *
   * <p>At 1 bump per second, we will bump 86400 times per day. This means we will have 2147483648 /
   * 86400 = 24855.13 days until the counter wraps around. This software then will fail after
   * 24855.13 / 366 = 67.91 years.
   *
   * <p>A built-in assumption is that an instance, upon process restart, will reset the counter back
   * to 0. This means we just need a cron job to wrong every 50 years to restart the process to not
   * worry about negative numbers.
   */
  public void bump(long now) {
    counter++;
    witness = now;
  }

  public void absorb(int incCounter, long now) {
    if (incCounter > counter) {
      this.counter = incCounter;
      this.witness = now;
    }
  }

  public boolean canDelete(long now) {
    return (now - witness) > Constants.MILLISECONDS_FOR_DELETION_CANDIDATE;
  }

  public boolean tooOldMustDelete(long now) {
    return (now - witness) > Constants.MILLISECONDS_FOR_RECOMMEND_DELETION_CANDIDATE;
  }

  @Override
  public int compareTo(Instance o) {
    return id.compareTo(o.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, ip, port, role);
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Instance) {
      Instance instance = (Instance) o;
      return id.equals(instance.id);
    }
    return false;
  }
}
