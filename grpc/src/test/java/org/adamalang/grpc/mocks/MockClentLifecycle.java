/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.grpc.mocks;

import org.adamalang.grpc.client.InstanceClient;
import org.adamalang.grpc.client.contracts.Lifecycle;

import java.util.Collection;

public class MockClentLifecycle implements Lifecycle {
  public StringBuilder events;
  public StringBuilder heartbeats;

  public MockClentLifecycle() {
    this.events = new StringBuilder();
    this.heartbeats = new StringBuilder();
  }

  @Override
  public synchronized void connected(InstanceClient client) {
    events.append("C");
  }

  @Override
  public synchronized void heartbeat(InstanceClient client, Collection<String> spaces) {
    heartbeats.append("!");
  }

  @Override
  public synchronized void disconnected(InstanceClient client) {
    events.append("D");
  }

  @Override
  public synchronized String toString() {
    return events.toString();
  }
}
