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
    ConcurrentCachedHtmlHandler handler = new ConcurrentCachedHtmlHandler();
    OverlordMetrics metrics = new OverlordMetrics(metricsFactory);
    PrometheusTargetMaker.kickOff(metrics, engine, targetsDestination, handler);
    DeploymentReconciliation.kickOff(metrics, engine, deploymentsDatabase, handler);
    HeatTable heatTable = new HeatTable(handler);
    Client client = new Client(identity, new ClientMetrics(metricsFactory), heatTable::onSample);
    engine.subscribe("adama", client.getTargetPublisher());
    CapacityManager.kickOffReturnHotTargetEvent(metrics, client, deploymentsDatabase, dataBaseFront, handler, heatTable);
    BillingAggregator.kickOff(metrics, client, handler);

    // build the index
    StringBuilder indexHtmlBuilder = new StringBuilder();
    indexHtmlBuilder.append("<html><head><title>OVERLORD</title></head><body>\n");
    indexHtmlBuilder.append("<a href=\"/capacity-manager\">Capacity Management View</a><br />\n");
    indexHtmlBuilder.append("<a href=\"/heat\">Heat Table</a><br />\n");
    indexHtmlBuilder.append("<a href=\"/reconcile\">Deployment Reconciliation</a><br />\n");
    indexHtmlBuilder.append("<a href=\"/targets\">Targets</a><br />\n");
    indexHtmlBuilder.append("</body></html>");
    String indexHtml = indexHtmlBuilder.toString();
    handler.put("/", indexHtml);
    return handler;
  }
}
