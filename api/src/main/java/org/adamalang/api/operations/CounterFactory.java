/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.api.operations;

import java.util.HashMap;

/** entry point for counters; this is thread safe with a lock, so best to use once at the start */
public class CounterFactory {
  private final HashMap<String, Counter> counters;
  private final HashMap<String, LatencyDistribution> latencyDistributions;
  private final HashMap<String, Total> totals;

  public CounterFactory() {
    this.counters = new HashMap<>();
    this.latencyDistributions = new HashMap<>();
    this.totals = new HashMap<>();
  }

  public synchronized Counter makeCounter(String name) {
    Counter counter = new Counter();
    counters.put(name, counter);
    return counter;
  }

  public synchronized LatencyDistribution makeLatencyDistribution(String name) {
    LatencyDistribution distribution = new LatencyDistribution();
    latencyDistributions.put(name, distribution);
    return distribution;
  }

  public synchronized Total makeTotalTracker(String name) {
    Total total = new Total();
    totals.put(name, total);
    return total;
  }
}
