/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.net.client.sm;

import org.adamalang.common.SimpleExecutor;
import org.adamalang.net.client.ClientConfig;
import org.adamalang.net.client.ClientMetrics;
import org.adamalang.net.client.InstanceClientFinder;
import org.adamalang.net.client.routing.Router;

/** each state machine has some common ground, and we form a base around that */
public class ConnectionBase {
  public final ClientConfig config;

  // metrics for the client
  public final ClientMetrics metrics;

  // how to map keys to targets;
  public final Router router;

  // how we turn targets into clients
  public final InstanceClientFinder mesh;

  // how we handle thread safety and time
  public final SimpleExecutor executor;

  public ConnectionBase(ClientConfig config, ClientMetrics metrics, Router router, InstanceClientFinder mesh, SimpleExecutor executor) {
    this.config = config;
    this.metrics = metrics;
    this.router = router;
    this.mesh = mesh;
    this.executor = executor;
  }
}
