/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
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
