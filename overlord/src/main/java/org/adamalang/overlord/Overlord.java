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
package org.adamalang.overlord;

import org.adamalang.caravan.contracts.Cloud;
import org.adamalang.common.gossip.Engine;
import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.multiregion.MultiRegionClient;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.impl.GlobalBillingDocumentFinder;
import org.adamalang.net.client.LocalRegionClient;
import org.adamalang.overlord.html.ConcurrentCachedHttpHandler;
import org.adamalang.overlord.roles.*;
import org.adamalang.runtime.data.ColdAssetSystem;
import org.adamalang.web.contracts.HttpHandler;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public class Overlord {
  public static HttpHandler execute(ConcurrentCachedHttpHandler handler, boolean isGlobalOverlord, LocalRegionClient localClient, MultiRegionClient client, Engine engine, MetricsFactory metricsFactory, File targetsDestination, DataBase dataBase, ColdAssetSystem lister, Cloud cloud, AtomicBoolean alive) throws Exception {
    // the overlord has metrics
    OverlordMetrics metrics = new OverlordMetrics(metricsFactory);

    // start producing the prometheus targets.json from the gossip engine
    PrometheusTargetMaker.kickOff(metrics, engine, targetsDestination, handler);

    if (isGlobalOverlord) {
      // delete things
      GlobalSpaceDeleteBot.kickOff(metrics, dataBase, client, alive);

      // kick off the garbage collector to clean up documents
      GlobalGarbageCollector.kickOff(metrics, dataBase, lister, cloud, alive);

      GlobalStorageReporter.kickOff(metrics, client, dataBase, new GlobalBillingDocumentFinder(dataBase));
    }

    // detect dead things
    DeadDetector.kickOff(metrics, dataBase, alive);

    // do a round to reconcile storage
    ReconcileDirectoryVersusStorage.kickOff(dataBase, metrics, cloud);

    // make a table of a dump of all gossip
    GossipDumper.kickOff(metrics, engine, handler);

    // build the index
    StringBuilder indexHtmlBuilder = new StringBuilder();
    indexHtmlBuilder.append("<html><head><title>OVERLORD</title></head><body>\n");
    indexHtmlBuilder.append("<a href=\"/heat\">Heat Table</a><br />\n");
    indexHtmlBuilder.append("<a href=\"/targets\">Targets</a><br />\n");
    indexHtmlBuilder.append("<a href=\"/gossip\">Gossip Dump</a><br />\n");
    indexHtmlBuilder.append("</body></html>");
    String indexHtml = indexHtmlBuilder.toString();
    handler.put("/", indexHtml);
    return handler;
  }
}
