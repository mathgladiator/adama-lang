/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.web.service;

import org.junit.Assert;
import org.junit.Test;

public class ServiceRunnableTests {

  @Test
  public void test_interrupt() throws Exception {
    final var nexus = NexusTests.mockNexus(NexusTests.Scenario.Mock1);
    final var runnable = new ServiceRunnable(nexus);
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(2500));
    Assert.assertTrue(runnable.isAccepting());
    thread.interrupt();
    thread.join();
  }

  @Test
  public void test_shutdown() throws Exception {
    final var nexus = NexusTests.mockNexus(NexusTests.Scenario.Mock2);
    final var runnable = new ServiceRunnable(nexus);
    final var thread = new Thread(runnable);
    thread.start();
    Assert.assertTrue(runnable.waitForReady(10000));
    Assert.assertTrue(runnable.isAccepting());
    runnable.shutdown();
    thread.join();
  }

  @Test
  public void test_tight_shutdown() throws Exception {
    final var nexus = NexusTests.mockNexus(NexusTests.Scenario.Mock3);
    final var runnable = new ServiceRunnable(nexus);
    runnable.shutdown();
    final var thread = new Thread(runnable);
    thread.start();
    thread.join();
  }
}
