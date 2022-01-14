/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.gossip;

import org.adamalang.common.metrics.MetricsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricsImpl implements Metrics {
  private static final Logger LOGGER = LoggerFactory.getLogger(MetricsImpl.class);

  private final Runnable wake;
  private final Runnable sadReturn;
  private final Runnable clientSlowGossip;
  private final Runnable optimisticReturn;
  private final Runnable turnTables;
  private final Runnable start;
  private final Runnable foundReverse;
  private final Runnable quickGossip;
  private final Runnable serverSlowGossip;

  public MetricsImpl(MetricsFactory factory) {
    wake = factory.counter("gossip_wake");
    sadReturn = factory.counter("gossip_sad");
    clientSlowGossip = factory.counter("gossip_slow_c");
    optimisticReturn = factory.counter("gossip_optimistic");
    turnTables = factory.counter("gossip_turn");
    start = factory.counter("gossip_start");
    foundReverse = factory.counter("gossip_found_rev");
    quickGossip = factory.counter("gossip_quick");
    serverSlowGossip = factory.counter("gossip_slow_s");
  }

  @Override
  public void wake() {
    wake.run();
  }

  @Override
  public void bump_sad_return() {
    sadReturn.run();
  }

  @Override
  public void bump_client_slow_gossip() {
    clientSlowGossip.run();
  }

  @Override
  public void bump_optimistic_return() {
    optimisticReturn.run();
  }

  @Override
  public void bump_turn_tables() {
    turnTables.run();
  }

  @Override
  public void bump_start() {
    start.run();
  }

  @Override
  public void bump_found_reverse() {
    foundReverse.run();
  }

  @Override
  public void bump_quick_gossip() {
    quickGossip.run();
  }

  @Override
  public void bump_server_slow_gossip() {
    serverSlowGossip.run();
  }

  @Override
  public void log_error(Throwable cause) {
    LOGGER.error("gossip-error", cause);
  }
}
