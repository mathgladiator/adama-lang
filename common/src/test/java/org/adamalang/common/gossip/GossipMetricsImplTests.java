/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.gossip;

import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.junit.Test;

public class GossipMetricsImplTests {
  @Test
  public void coverage() {
    GossipMetricsImpl impl = new GossipMetricsImpl(new NoOpMetricsFactory());
    impl.bump_client_slow_gossip();
    impl.bump_found_reverse();
    impl.bump_client_slow_gossip();
    impl.bump_quick_gossip();
    impl.wake();
    impl.bump_start();
    impl.bump_turn_tables();
    impl.bump_server_slow_gossip();
    impl.bump_sad_return();
    impl.bump_optimistic_return();
    impl.log_error(new NullPointerException());
    impl.gossips_inflight().up();
    impl.gossips_inflight().down();
  }
}
