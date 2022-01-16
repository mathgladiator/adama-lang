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

import org.adamalang.common.metrics.MetricsFactory;
import org.adamalang.gossip.Engine;
import org.adamalang.grpc.client.Client;
import org.adamalang.mysql.DataBase;
import org.adamalang.overlord.roles.CapacityManager;
import org.adamalang.overlord.roles.DeploymentReconciliation;
import org.adamalang.overlord.roles.PrometheusTargetMaker;

import java.io.File;

public class Overlord {
  public static void execute(Engine engine, Client client, MetricsFactory metricsFactory, File targetsDestination, DataBase deploymentsDatabase, DataBase dataBaseFront) {
    OverlordMetrics metrics = new OverlordMetrics(metricsFactory);
    PrometheusTargetMaker.kickOff(metrics, engine, targetsDestination);
    DeploymentReconciliation.kickOff(metrics, engine, deploymentsDatabase);
    CapacityManager.kickOff(metrics, client, deploymentsDatabase, dataBaseFront);

    // TODO: ROLE #2.A: pick a random adama host, download billing data, cut bills into hourly segments over to billing database
    // client.pickRandomHost((client) -> {});

    // TODO: ROLE #3.B: when a hot host appears, use billing information to find hottest space, and then make a decision to act on it
    // TODO: ROLE #3.C: adama should inform which spaces on a hot host are oversubscribed... this is an interesting challenge
    engine.subscribe("adama", client.getTargetPublisher());
  }
}
