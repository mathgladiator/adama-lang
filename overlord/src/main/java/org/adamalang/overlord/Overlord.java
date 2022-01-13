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

import org.adamalang.gossip.Engine;
import org.adamalang.gossip.proto.Endpoint;
import org.adamalang.grpc.client.Client;

import java.io.File;

public class Overlord {
  public static void execute(Engine engine, Client client, File targetsDestination) {

    // TODO: ROLE #1: scan gossip table to make targets.json for promethesus (and either set on disk locally OR send to server via ssh)
    engine.setWatcher((endpoints) -> {
      // TODO: start building a JSON array
      for (Endpoint endpoint : endpoints) {
        if (endpoint.getMonitoringPort() >= 0) {
          // ADD entry to json array
        }
      }
      // write array to disk at location specified
      // Files.writeString(targetsDestination.toPath(), "");
    });

    // TODO: ROLE #2.A: pick a random adama host, download billing data, cut bills into hourly segments over to billing database
    // client.pickRandomHost((client) -> {});

    // TODO: ROLE #3.A: subscribe to every adama to get periodoic heat of host cpu + memory to find a hot host
    // TODO: ROLE #3.B: when a hot host appears, use billing information to find hottest space, and then make a decision to act on it
    // TODO: ROLE #3.C: adama should inform which spaces on a hot host are oversubscribed... this is an interesting challenge
    engine.subscribe("adama", client.getTargetPublisher());

    // client.setHeatSubscriber((endpoint, heat) -> {});

    // TODO: Periodically download the deployment mapping so we can contrast what hosts and reconcile deployments
    // TODO: ROLE #4: Ensure deployments are consistent


  }
}
