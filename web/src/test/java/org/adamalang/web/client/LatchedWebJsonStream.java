/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.web.contracts.WebJsonStream;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LatchedWebJsonStream implements WebJsonStream {
  private final ArrayList<String> lines;
  private final ArrayList<CountDownLatch> latches;

  public LatchedWebJsonStream() {
    this.lines = new ArrayList<>();
    this.latches = new ArrayList<>();
  }

  public synchronized Runnable latchAt(int k) {
    CountDownLatch latch = new CountDownLatch(k);
    latches.add(latch);
    return () -> {
      try {
        Assert.assertTrue(latch.await(2500, TimeUnit.MILLISECONDS));
      } catch (Exception ex) {
        Assert.fail();
      }
    };
  }

  public synchronized void assertLine(int k, String expected) {
    Assert.assertEquals(expected, lines.get(k));
  }

  private synchronized void write(String line) {
    lines.add(line);
    Iterator<CountDownLatch> it = latches.iterator();
    while (it.hasNext()) {
      CountDownLatch latch = it.next();
      latch.countDown();
      if (latch.getCount() == 0) {
        it.remove();
      }
    }
  }

  @Override
  public void data(int cId, ObjectNode node) {
    write("DATA:" + node.toString());
  }

  @Override
  public void complete() {
    write("COMPLETE");
  }

  @Override
  public void failure(int code) {
    write("FAILURE:" + code);
  }
}
