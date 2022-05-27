/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.net.client;

import org.adamalang.common.metrics.*;

public class ClientMetrics {
  public final Inflight client_connection_alive;
  public final Inflight client_state_machines_alive;
  public final Runnable client_too_many_failures_finding_client;
  public final Runnable client_too_many_failures_disconnected_by_peer;
  public final ItemActionMonitor client_connection_send;
  public final ItemActionMonitor client_connection_update;
  public final ItemActionMonitor client_connection_attach;
  public final ItemActionMonitor client_connection_can_attach;
  public final StreamMonitor client_open_document;

  public final ItemActionMonitor client_ping;
  public final ItemActionMonitor client_create;
  public final CallbackMonitor client_create_cb;
  public final ItemActionMonitor client_close;
  public final ItemActionMonitor client_reflection;
  public final CallbackMonitor client_reflection_cb;
  public final ItemActionMonitor client_metering_exchange;
  public final ItemActionMonitor client_scan_deployment;
  public final CallbackMonitor client_scan_deployment_cb;
  public final ItemActionMonitor client_document_exchange;

  public final Runnable client_retry;
  public final Runnable client_info_start;
  public final Runnable client_info_completed;
  public final Runnable client_info_failed_downstream;
  public final Runnable client_info_failed_ask;

  public final Runnable client_notify_deploy_success;
  public final Runnable client_notify_deploy_failure_do;
  public final Runnable client_notify_deploy_failure_find;

  public final ItemActionMonitor client_proxy;

  public final RequestResponseMonitor client_create_found_machine;

  public ClientMetrics(MetricsFactory factory) {
    client_connection_alive = factory.inflight("client_connection_alive");
    client_state_machines_alive = factory.inflight("client_state_machines_alive");
    client_notify_deploy_success = factory.counter("client_notify_deploy_success");
    client_notify_deploy_failure_do = factory.counter("client_notify_deploy_failure_do");
    client_notify_deploy_failure_find = factory.counter("client_notify_deploy_failure_find");
    client_too_many_failures_finding_client = factory.counter("client_too_many_failures_finding_client");
    client_too_many_failures_disconnected_by_peer = factory.counter("client_too_many_failures_disconnected_by_peer");
    client_metering_exchange = factory.makeItemActionMonitor("client_metering_exchange");
    client_connection_send = factory.makeItemActionMonitor("client_connection_send");
    client_connection_update = factory.makeItemActionMonitor("client_connection_update");
    client_connection_attach = factory.makeItemActionMonitor("client_connection_attach");
    client_connection_can_attach = factory.makeItemActionMonitor("client_connection_can_attach");
    client_open_document = factory.makeStreamMonitor("client_open_document");
    client_ping = factory.makeItemActionMonitor("client_ping");
    client_create = factory.makeItemActionMonitor("client_create");
    client_create_cb = factory.makeCallbackMonitor("client_create");
    client_reflection = factory.makeItemActionMonitor("client_reflection");
    client_reflection_cb = factory.makeCallbackMonitor("client_reflection");
    client_close = factory.makeItemActionMonitor("client_close");
    client_scan_deployment = factory.makeItemActionMonitor("client_scan_deployment");
    client_scan_deployment_cb = factory.makeCallbackMonitor("client_scan_deployment");
    client_document_exchange = factory.makeItemActionMonitor("client_document_exchange");
    client_retry = factory.counter("client_retry");
    client_info_start = factory.counter("client_info_start");
    client_info_completed = factory.counter("client_info_completed");
    client_info_failed_downstream = factory.counter("client_info_failed_downstream");
    client_info_failed_ask = factory.counter("client_info_failed_ask");
    client_proxy = factory.makeItemActionMonitor("client_proxy");
    client_create_found_machine = factory.makeRequestResponseMonitor("client_create_found_machine");
  }
}
