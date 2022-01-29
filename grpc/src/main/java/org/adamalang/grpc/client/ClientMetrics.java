/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.grpc.client;

import org.adamalang.common.metrics.Inflight;
import org.adamalang.common.metrics.ItemActionMonitor;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.common.metrics.StreamMonitor;

public class ClientMetrics {
  public final Inflight client_state_machines_alive;
  public final ItemActionMonitor client_notify_deployment;
  public final Runnable client_notify_deploy_success;
  public final Runnable client_notify_deploy_failure_do;
  public final Runnable client_notify_deploy_failure_find;
  public final ItemActionMonitor client_find_client;
  public final Runnable client_too_many_failures_finding_client;
  public final Runnable client_too_many_failures_disconnected_by_peer;
  public final ItemActionMonitor client_metering_exchange;
  public final ItemActionMonitor client_reflection;
  public final ItemActionMonitor client_create;
  public final ItemActionMonitor client_connection_send;
  public final ItemActionMonitor client_connection_attach;
  public final ItemActionMonitor client_connection_can_attach;
  public final StreamMonitor client_open_document;

  public ClientMetrics(MetricsFactory factory) {
    client_state_machines_alive = factory.inflight("client_state_machines_alive");
    client_notify_deployment = factory.makeItemActionMonitor("client_notify_deployment");
    client_notify_deploy_success = factory.counter("client_notify_deploy_success");
    client_notify_deploy_failure_do = factory.counter("client_notify_deploy_failure_do");
    client_notify_deploy_failure_find = factory.counter("client_notify_deploy_failure_find");
    client_find_client = factory.makeItemActionMonitor("client_find_client");
    client_too_many_failures_finding_client = factory.counter("client_too_many_failures_finding_client");
    client_too_many_failures_disconnected_by_peer = factory.counter("client_too_many_failures_disconnected_by_peer");
    client_metering_exchange = factory.makeItemActionMonitor("client_metering_exchange");
    client_reflection = factory.makeItemActionMonitor("client_reflection");
    client_create = factory.makeItemActionMonitor("client_create");
    client_connection_send = factory.makeItemActionMonitor("client_connection_send");
    client_connection_attach = factory.makeItemActionMonitor("client_connection_attach");
    client_connection_can_attach = factory.makeItemActionMonitor("client_connection_can_attach");
    client_open_document = factory.makeStreamMonitor("client_open_document");
  }
}
