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
