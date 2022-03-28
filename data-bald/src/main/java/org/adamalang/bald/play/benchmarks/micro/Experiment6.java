/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.bald.play.benchmarks.micro;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Experiment6 {

  public static void main(String[] args) throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("executor");
    CountDownLatch first = new CountDownLatch(1);
    Thread.sleep(1000);
    executor.execute(new NamedRunnable("dump-slf4j") {
      @Override
      public void execute() throws Exception {
        first.countDown();
      }
    });
    first.await(1000, TimeUnit.MILLISECONDS);
    System.out.println("asked-wait-ms,achieved-wait-ms");
    for (int latency = 0; latency < 40; latency++) {
      final int latencyEx = latency;
      final long started = System.currentTimeMillis();
      Thread.sleep(latencyEx);
      System.out.println(latencyEx + "," + (System.currentTimeMillis() - started));
    }
  }
}
