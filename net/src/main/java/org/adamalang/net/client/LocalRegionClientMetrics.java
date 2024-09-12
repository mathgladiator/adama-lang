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
package org.adamalang.net.client;

import org.adamalang.common.metrics.*;

public class LocalRegionClientMetrics {
  public final Inflight client_connection_alive;
  public final Inflight client_state_machines_alive;

  public final ItemActionMonitor client_ping;
  public final ItemActionMonitor client_create;
  public final ItemActionMonitor client_directsend;
  public final ItemActionMonitor client_forcebackup;
  public final ItemActionMonitor client_delete;
  public final ItemActionMonitor client_auth;
  public final ItemActionMonitor client_webget;
  public final ItemActionMonitor client_find;
  public final ItemActionMonitor client_probe;
  public final ItemActionMonitor client_weboptions;
  public final ItemActionMonitor client_webput;
  public final CallbackMonitor client_create_cb;
  public final CallbackMonitor client_directsend_cb;
  public final CallbackMonitor client_forcebackup_cb;
  public final CallbackMonitor client_delete_cb;
  public final ItemActionMonitor client_close;
  public final ItemActionMonitor client_reflection;
  public final CallbackMonitor client_reflection_cb;
  public final ItemActionMonitor client_scan_deployment;
  public final ItemActionMonitor client_ratelimit;
  public final ItemActionMonitor client_replica_create;
  public final CallbackMonitor client_scan_deployment_cb;
  public final ItemActionMonitor client_document_exchange;
  public final ItemActionMonitor client_document_exchange_observe;
  public final ItemActionMonitor client_drain;
  public final ItemActionMonitor client_loadget;
  public final CallbackMonitor client_rate_limit;

  public final Runnable client_retry;
  public final Runnable client_info_start;
  public final Runnable client_info_completed;
  public final Runnable client_info_failed_downstream;
  public final Runnable client_info_failed_ask;

  public final Runnable client_notify_deploy_success;
  public final Runnable client_notify_deploy_failure_do;
  public final Runnable client_notify_deploy_failure_find;

  public final ItemActionMonitor client_proxy;

  public final Runnable client_rxcache_fallback;
  public final Runnable client_rxcache_found;

  public final ItemActionMonitor lcsm_connection_password;
  public final ItemActionMonitor lcsm_connection_update;
  public final ItemActionMonitor lcsm_connection_send;
  public final ItemActionMonitor lcsm_connection_can_attach;
  public final ItemActionMonitor lcsm_connection_attach;
  public final Runnable lcsm_timeout;

  public final Inflight client_host_set_database_size;
  public final Inflight client_host_set_gossip_size;

  public final ItemActionMonitor multi_region_find;

  public LocalRegionClientMetrics(MetricsFactory factory) {
    client_connection_alive = factory.inflight("client_connection_alive");
    client_state_machines_alive = factory.inflight("client_state_machines_alive");
    client_notify_deploy_success = factory.counter("client_notify_deploy_success");
    client_notify_deploy_failure_do = factory.counter("client_notify_deploy_failure_do");
    client_notify_deploy_failure_find = factory.counter("client_notify_deploy_failure_find");
    client_ping = factory.makeItemActionMonitor("client_ping");
    client_create = factory.makeItemActionMonitor("client_create");
    client_probe = factory.makeItemActionMonitor("client_probe");
    client_directsend = factory.makeItemActionMonitor("client_directsend");
    client_forcebackup = factory.makeItemActionMonitor("client_forcebackup");
    client_ratelimit = factory.makeItemActionMonitor("client_ratelimit");
    client_replica_create = factory.makeItemActionMonitor("client_replica_create");
    client_delete = factory.makeItemActionMonitor("client_delete");
    client_auth = factory.makeItemActionMonitor("client_auth");
    client_find = factory.makeItemActionMonitor("client_find");
    client_webget = factory.makeItemActionMonitor("client_webget");
    client_webput = factory.makeItemActionMonitor("client_webput");
    client_weboptions = factory.makeItemActionMonitor("client_weboptions");
    client_create_cb = factory.makeCallbackMonitor("client_create");
    client_directsend_cb = factory.makeCallbackMonitor("client_directsend");
    client_forcebackup_cb = factory.makeCallbackMonitor("client_forcebackup_cb");
    client_delete_cb = factory.makeCallbackMonitor("client_delete");
    client_reflection = factory.makeItemActionMonitor("client_reflection");
    client_reflection_cb = factory.makeCallbackMonitor("client_reflection");
    client_close = factory.makeItemActionMonitor("client_close");
    client_scan_deployment = factory.makeItemActionMonitor("client_scan_deployment");
    client_scan_deployment_cb = factory.makeCallbackMonitor("client_scan_deployment");
    client_document_exchange = factory.makeItemActionMonitor("client_document_exchange");
    client_document_exchange_observe = factory.makeItemActionMonitor("client_document_exchange_observe");
    client_drain = factory.makeItemActionMonitor("client_drain");
    client_loadget = factory.makeItemActionMonitor("client_loadget");
    client_rate_limit = factory.makeCallbackMonitor("client_rate_limit");
    client_retry = factory.counter("client_retry");
    client_info_start = factory.counter("client_info_start");
    client_info_completed = factory.counter("client_info_completed");
    client_info_failed_downstream = factory.counter("client_info_failed_downstream");
    client_info_failed_ask = factory.counter("client_info_failed_ask");
    client_proxy = factory.makeItemActionMonitor("client_proxy");

    client_rxcache_fallback = factory.counter("client_rxcache_fallback");
    client_rxcache_found = factory.counter("client_rxcache_found");

    lcsm_connection_password = factory.makeItemActionMonitor("lcsm_connection_password");
    lcsm_connection_update = factory.makeItemActionMonitor("lcsm_connection_update");
    lcsm_connection_send = factory.makeItemActionMonitor("lcsm_connection_send");
    lcsm_connection_can_attach = factory.makeItemActionMonitor("lcsm_connection_can_attach");
    lcsm_connection_attach = factory.makeItemActionMonitor("lcsm_connection_attach");
    lcsm_timeout = factory.counter("lcsm_timeout");
    client_host_set_database_size = factory.inflight("client_host_set_database_size");
    client_host_set_gossip_size = factory.inflight("client_host_set_gossip_size");

    multi_region_find = factory.makeItemActionMonitor("multi_region_find");
  }
}
