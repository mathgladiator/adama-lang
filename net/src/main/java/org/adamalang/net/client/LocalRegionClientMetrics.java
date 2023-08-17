/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.client;

import org.adamalang.common.metrics.*;

public class LocalRegionClientMetrics {
  public final Inflight client_connection_alive;
  public final Inflight client_state_machines_alive;

  public final ItemActionMonitor client_ping;
  public final ItemActionMonitor client_create;
  public final ItemActionMonitor client_directsend;
  public final ItemActionMonitor client_delete;
  public final ItemActionMonitor client_auth;
  public final ItemActionMonitor client_webget;
  public final ItemActionMonitor client_find;
  public final ItemActionMonitor client_probe;
  public final ItemActionMonitor client_weboptions;
  public final ItemActionMonitor client_webput;
  public final CallbackMonitor client_create_cb;
  public final CallbackMonitor client_directsend_cb;
  public final CallbackMonitor client_delete_cb;
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
  public final RequestResponseMonitor client_delete_found_machine;
  public final RequestResponseMonitor client_directsend_found_machine;
  public final RequestResponseMonitor client_auth_found_machine;
  public final RequestResponseMonitor client_webget_found_machine;
  public final RequestResponseMonitor client_weboptions_found_machine;
  public final RequestResponseMonitor client_webput_found_machine;
  public final RequestResponseMonitor client_webdelete_found_machine;

  public final Runnable client_rxcache_fallback;
  public final Runnable client_rxcache_found;

  public final ItemActionMonitor lcsm_connection_password;
  public final ItemActionMonitor lcsm_connection_update;
  public final ItemActionMonitor lcsm_connection_send;
  public final ItemActionMonitor lcsm_connection_can_attach;
  public final ItemActionMonitor lcsm_connection_attach;
  public final Runnable lcsm_timeout;

  public final Inflight client_host_set_invalid;
  public final Inflight client_host_set_database_size;
  public final Inflight client_host_set_gossip_size;

  public final ItemActionMonitor multi_region_find;

  public LocalRegionClientMetrics(MetricsFactory factory) {
    client_connection_alive = factory.inflight("client_connection_alive");
    client_state_machines_alive = factory.inflight("client_state_machines_alive");
    client_notify_deploy_success = factory.counter("client_notify_deploy_success");
    client_notify_deploy_failure_do = factory.counter("client_notify_deploy_failure_do");
    client_notify_deploy_failure_find = factory.counter("client_notify_deploy_failure_find");
    client_metering_exchange = factory.makeItemActionMonitor("client_metering_exchange");
    client_ping = factory.makeItemActionMonitor("client_ping");
    client_create = factory.makeItemActionMonitor("client_create");
    client_probe = factory.makeItemActionMonitor("client_probe");
    client_directsend = factory.makeItemActionMonitor("client_directsend");
    client_delete = factory.makeItemActionMonitor("client_delete");
    client_auth = factory.makeItemActionMonitor("client_auth");
    client_find = factory.makeItemActionMonitor("client_find");
    client_webget = factory.makeItemActionMonitor("client_webget");
    client_webput = factory.makeItemActionMonitor("client_webput");
    client_weboptions = factory.makeItemActionMonitor("client_weboptions");
    client_create_cb = factory.makeCallbackMonitor("client_create");
    client_directsend_cb = factory.makeCallbackMonitor("client_directsend");
    client_delete_cb = factory.makeCallbackMonitor("client_delete");
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
    client_delete_found_machine = factory.makeRequestResponseMonitor("client_delete_found_machine");
    client_directsend_found_machine = factory.makeRequestResponseMonitor("client_directsend_found_machine");
    client_auth_found_machine = factory.makeRequestResponseMonitor("client_auth_found_machine");
    client_webget_found_machine = factory.makeRequestResponseMonitor("client_webget_found_machine");
    client_weboptions_found_machine = factory.makeRequestResponseMonitor("client_weboptions_found_machine");
    client_webput_found_machine = factory.makeRequestResponseMonitor("client_webput_found_machine");
    client_webdelete_found_machine = factory.makeRequestResponseMonitor("client_webdelete_found_machine");
    client_rxcache_fallback = factory.counter("client_rxcache_fallback");
    client_rxcache_found = factory.counter("client_rxcache_found");

    lcsm_connection_password = factory.makeItemActionMonitor("lcsm_connection_password");
    lcsm_connection_update = factory.makeItemActionMonitor("lcsm_connection_update");
    lcsm_connection_send = factory.makeItemActionMonitor("lcsm_connection_send");
    lcsm_connection_can_attach = factory.makeItemActionMonitor("lcsm_connection_can_attach");
    lcsm_connection_attach = factory.makeItemActionMonitor("lcsm_connection_attach");
    lcsm_timeout = factory.counter("lcsm_timeout");
    client_host_set_invalid = factory.inflight("alarm_client_host_set_invalid");
    client_host_set_database_size = factory.inflight("client_host_set_database_size");
    client_host_set_gossip_size = factory.inflight("client_host_set_gossip_size");

    multi_region_find = factory.makeItemActionMonitor("multi_region_find");
  }
}
