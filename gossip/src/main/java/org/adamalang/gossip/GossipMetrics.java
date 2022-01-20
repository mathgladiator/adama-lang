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

/** how to learn of events happening during the mysterious gossip protocol */
public interface GossipMetrics {
  // a round of gossip is being considered
  void wake();

  void bump_sad_return();

  void bump_client_slow_gossip();

  void bump_optimistic_return();

  void bump_turn_tables();

  void bump_start();

  void bump_found_reverse();

  void bump_quick_gossip();

  void bump_server_slow_gossip();

  void log_error(Throwable cause);
}
