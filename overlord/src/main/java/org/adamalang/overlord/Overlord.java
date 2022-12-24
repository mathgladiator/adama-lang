/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.overlord;

import org.adamalang.caravan.contracts.Cloud;
import org.adamalang.common.gossip.Engine;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.mysql.DataBase;
import org.adamalang.net.client.Client;
import org.adamalang.overlord.heat.HeatTable;
import org.adamalang.overlord.html.ConcurrentCachedHttpHandler;
import org.adamalang.overlord.roles.*;
import org.adamalang.runtime.data.ColdAssetSystem;
import org.adamalang.web.contracts.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public class Overlord {
  private static final Logger LOGGER = LoggerFactory.getLogger(Overlord.class);

  // TODO: convert Client to multi-region client
  public static HttpHandler execute(ConcurrentCachedHttpHandler handler, HeatTable heatTable, Client client, Engine engine, MetricsFactory metricsFactory, File targetsDestination, DataBase dataBase, String scanPath, ColdAssetSystem lister, Cloud cloud, AtomicBoolean alive) throws Exception {
    // the overlord has metrics
    OverlordMetrics metrics = new OverlordMetrics(metricsFactory);

    // start producing the prometheus targets.json from the gossip engine
    PrometheusTargetMaker.kickOff(metrics, engine, targetsDestination, handler);

    // make sure that we remove deployments from dead hosts
    /*
    DeploymentReconciliation.kickOff(metrics, engine, dataBase, handler);
    */
    GlobalSpaceDeleteBot.kickOff(metrics, dataBase, client, alive);

    // detect dead things
    DeadDetector.kickOff(metrics, dataBase, alive);

    // kick off the garbage collector to clean up documents
    GarbageCollector.kickOff(metrics, dataBase, lister, cloud, alive);

    // kick off capacity management will will add/remove capacity per space
    CapacityManager.kickOffReturnHotTargetEvent(metrics, client, dataBase, handler, heatTable);

    // start aggregating bills from hosts and write them to database
    MeteringAggregator.kickOff(metrics, client, dataBase, handler);

    // make a table of a dump of all gossip
    GossipDumper.kickOff(metrics, engine, handler);

    // start doing the accounting work
    GlobalHourlyAccountant.kickOff(metrics, client, dataBase, handler);

    // build the index
    StringBuilder indexHtmlBuilder = new StringBuilder();
    indexHtmlBuilder.append("<html><head><title>OVERLORD</title></head><body>\n");
    indexHtmlBuilder.append("<a href=\"/capacity-manager\">Capacity Management View</a><br />\n");
    indexHtmlBuilder.append("<a href=\"/heat\">Heat Table</a><br />\n");
    indexHtmlBuilder.append("<a href=\"/reconcile\">Deployment Reconciliation</a><br />\n");
    indexHtmlBuilder.append("<a href=\"/targets\">Targets</a><br />\n");
    indexHtmlBuilder.append("<a href=\"/metering\">Recent Metering Data</a><br />\n");
    indexHtmlBuilder.append("<a href=\"/gossip\">Gossip Dump</a><br />\n");
    indexHtmlBuilder.append("<a href=\"/accountant\">Accountant Log</a><br />\n");
    indexHtmlBuilder.append("<a href=\"/webroot\">Web Root Scanner</a><br />\n");
    indexHtmlBuilder.append("</body></html>");
    String indexHtml = indexHtmlBuilder.toString();
    handler.put("/", indexHtml);
    return handler;
  }
}
