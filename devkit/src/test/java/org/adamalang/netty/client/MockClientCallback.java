/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.client;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.adamalang.netty.contracts.ClientCallback;
import org.junit.Assert;

public class MockClientCallback implements ClientCallback {
  private final CountDownLatch doneLatch;
  private final ArrayList<CountDownLatch> latches;
  private final ArrayList<String> output;

  public MockClientCallback(final int responsesExpected) {
    doneLatch = new CountDownLatch(responsesExpected);
    output = new ArrayList<>();
    latches = new ArrayList<>();
    latches.add(doneLatch);
  }

  public void awaitDone() {
    try {
      Assert.assertTrue(doneLatch.await(5000, TimeUnit.MILLISECONDS));
    } catch (final InterruptedException ie) {
      Assert.fail();
    }
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
    output.add(out);
    for (final CountDownLatch latch : latches) {
      latch.countDown();
    }
  }
}
