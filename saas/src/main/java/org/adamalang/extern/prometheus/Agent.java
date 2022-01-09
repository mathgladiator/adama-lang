/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.extern.prometheus;

import org.adamalang.common.MachineIdentity;
import org.adamalang.common.TimeSource;
import org.adamalang.gossip.Engine;
import org.adamalang.gossip.Metrics;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;

public class Agent {
  public static void drive(MachineIdentity identity, TimeSource time, HashSet<String> initial, int port, Metrics metrics, String dumpTo) throws Exception {
    Engine engine = new Engine(identity, time, initial, port, metrics);
    engine.start();


  }
}
