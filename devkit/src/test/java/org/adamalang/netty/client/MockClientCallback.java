/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.netty.client;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.adamalang.netty.contracts.ClientCallback;
import org.adamalang.runtime.stdlib.Utility;
import org.junit.Assert;

public class MockClientCallback implements ClientCallback {
  private final CountDownLatch doneLatch;
  private final ArrayList<CountDownLatch> latches;
  private final ArrayList<String> output;
  public final CountDownLatch ping;

  public MockClientCallback(final int responsesExpected) {
    doneLatch = new CountDownLatch(responsesExpected);
    output = new ArrayList<>();
    latches = new ArrayList<>();
    latches.add(doneLatch);
    ping = new CountDownLatch(1);
  }

  public void awaitDone() {
    try {
      Assert.assertTrue(doneLatch.await(5000, TimeUnit.MILLISECONDS));
    } catch (final InterruptedException ie) {
      Assert.fail();
    }
  }

  @Override
  public void closed() {
    write("Closed");
  }

  @Override
  public void failed(final Throwable exception) {
    exception.printStackTrace();
    write("Exception:" + exception.getMessage());
  }

  @Override
  public void failedToConnect() {
    write("FailedToConnect!");
  }

  public CountDownLatch latchAt(final int k) {
    final var latch = new CountDownLatch(k);
    latches.add(latch);
    return latch;
  }

  public synchronized ArrayList<String> output() {
    return new ArrayList<>(output);
  }

  @Override
  public void successfulResponse(final String data) {
    write("DATA:" + data);
  }

  private synchronized void write(final String out) {
    if (out != null && out.contains("\"ping\"")) {
      ping.countDown();
      return;
    }
    output.add(out);
    for (final CountDownLatch latch : latches) {
      latch.countDown();
    }
  }
}
