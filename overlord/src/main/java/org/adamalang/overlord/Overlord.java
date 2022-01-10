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

public class Overlord {
  public static void execute() {
    // TODO: ROLE #1: scan gossip table to make targets.json for prometheesus (and either set on disk locally OR send to server via ssh)
    // TODO: ROLE #2: pick a random adama host, download billing data, cut bills into hourly segments over to billing database
    // TODO: ROLE #3.A: subscribe to every adama to get periodoic heat of host cpu + memory to find a hot host
    // TODO: ROLE #3.B: when a hot host appears, use billing information to find hottest space, and then make a decision to act on it
  }
}
