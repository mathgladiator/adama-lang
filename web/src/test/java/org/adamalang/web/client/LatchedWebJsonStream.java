/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
