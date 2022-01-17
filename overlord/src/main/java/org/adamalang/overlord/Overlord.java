/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.overlord;

import org.adamalang.common.MachineIdentity;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.gossip.Engine;
import org.adamalang.grpc.client.Client;
import org.adamalang.grpc.client.ClientMetrics;
import org.adamalang.mysql.DataBase;
import org.adamalang.overlord.heat.HeatTable;
import org.adamalang.overlord.html.ConcurrentCachedHtmlHandler;
import org.adamalang.overlord.roles.BillingAggregator;
import org.adamalang.overlord.roles.CapacityManager;
import org.adamalang.overlord.roles.DeploymentReconciliation;
import org.adamalang.overlord.roles.PrometheusTargetMaker;
import org.adamalang.web.contracts.HtmlHandler;

import java.io.File;

public class Overlord {
  public static HtmlHandler execute(MachineIdentity identity, Engine engine, MetricsFactory metricsFactory, File targetsDestination, DataBase deploymentsDatabase, DataBase dataBaseFront) {
    // the HTTP web server will render data that has been put/cached in this handler
    ConcurrentCachedHtmlHandler handler = new ConcurrentCachedHtmlHandler();

    // the overlord has metrics
    OverlordMetrics metrics = new OverlordMetrics(metricsFactory);

    // start producing the prometheus targets.json from the gossip engine
    PrometheusTargetMaker.kickOff(metrics, engine, targetsDestination, handler);

    // make sure that we remove deployments from dead hosts
    DeploymentReconciliation.kickOff(metrics, engine, deploymentsDatabase, handler);

    // we will be monitoring the heat on each host within this table
    HeatTable heatTable = new HeatTable(handler);

    // build a full mesh from overlord to all clients
    Client client = new Client(identity, new ClientMetrics(metricsFactory), heatTable::onSample);
    engine.subscribe("adama", client.getTargetPublisher());

    // kick off capacity management will will add/remove capacity per space
    CapacityManager.kickOffReturnHotTargetEvent(metrics, client, deploymentsDatabase, dataBaseFront, handler, heatTable);

    // start aggregating bills from hosts and write them to database
    BillingAggregator.kickOff(metrics, client, dataBaseFront, handler);

    // build the index
    StringBuilder indexHtmlBuilder = new StringBuilder();
    indexHtmlBuilder.append("<html><head><title>OVERLORD</title></head><body>\n");
    indexHtmlBuilder.append("<a href=\"/capacity-manager\">Capacity Management View</a><br />\n");
    indexHtmlBuilder.append("<a href=\"/heat\">Heat Table</a><br />\n");
    indexHtmlBuilder.append("<a href=\"/reconcile\">Deployment Reconciliation</a><br />\n");
    indexHtmlBuilder.append("<a href=\"/targets\">Targets</a><br />\n");
    indexHtmlBuilder.append("<a href=\"/billing\">Recent Billing Data</a><br />\n");
    indexHtmlBuilder.append("</body></html>");
    String indexHtml = indexHtmlBuilder.toString();
    handler.put("/", indexHtml);
    return handler;
  }
}
