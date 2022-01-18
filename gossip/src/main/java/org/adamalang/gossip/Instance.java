/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.gossip;

import org.adamalang.gossip.proto.Endpoint;

import java.util.Objects;

/** an instance represents an application on a host that is heartbeating */
public class Instance implements Comparable<Instance> {
  public final String id;
  public final int monitoringPort;
  public final String ip;
  public final int port;
  public final String role;
  private int counter;
  private long witness;

  public Instance(Endpoint endpoint, long now) {
    this.id = endpoint.getId();
    this.ip = endpoint.getIp();
    this.port = endpoint.getPort();
    this.monitoringPort = endpoint.getMonitoringPort();
    this.role = endpoint.getRole();
    this.counter = endpoint.getCounter();
    this.witness = now;
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

  public Endpoint toEndpoint() {
    return Endpoint.newBuilder()
        .setId(id)
        .setIp(ip)
        .setPort(port)
        .setMonitoringPort(monitoringPort)
        .setRole(role)
        .setCounter(counter)
        .build();
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
