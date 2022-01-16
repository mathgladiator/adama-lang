/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.overlord.roles;

import org.adamalang.grpc.client.Client;
import org.adamalang.mysql.DataBase;
import org.adamalang.overlord.OverlordMetrics;
import org.adamalang.overlord.html.ConcurrentCachedHtmlHandler;

public class BillingAggregator {
  public static void kickOff(
      OverlordMetrics metrics,
      Client client,
      DataBase dataBaseFront,
      ConcurrentCachedHtmlHandler handler) {
    // TODO: ROLE #2.A: pick a random adama host, download billing data, cut bills into hourly segments over to billing database
    // client.pickRandomHost((client) -> {});

    // TODO: ROLE #3.B: when a hot host appears, use billing information to find hottest space, and then make a decision to act on it
    // TODO: ROLE #3.C: adama should inform which spaces on a hot host are oversubscribed... this is an interesting challenge

  }
}
