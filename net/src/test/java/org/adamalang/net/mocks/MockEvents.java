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
package org.adamalang.net.mocks;

import org.adamalang.net.client.contracts.Events;
import org.adamalang.net.client.contracts.Remote;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class MockEvents implements Events {
  private final ArrayList<String> history;
  private ArrayList<CountDownLatch> latches;
  private final AtomicReference<Remote> gotRemote;
  private CountDownLatch remoteFound;

  public MockEvents() {
    this.history = new ArrayList<>();
    latches = new ArrayList<>();
    this.gotRemote = new AtomicReference<>();
    this.remoteFound = new CountDownLatch(1);
  }

  public Remote getRemote() throws Exception {
    Assert.assertTrue(remoteFound.await(5000, TimeUnit.MILLISECONDS));
    return gotRemote.get();
  }

  public synchronized Runnable latchAt(int write) {
    CountDownLatch latch = new CountDownLatch(write);
    latches.add(latch);
    return () -> {
      try {
        Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
      } catch (InterruptedException ie) {
        Assert.fail();
      }
    };
  }

  public synchronized void assertWrite(int write, String expected) {
    Assert.assertTrue(write < history.size());
    Assert.assertEquals(expected, history.get(write));
  }

  @Override
  public void connected(Remote remote) {
    gotRemote.set(remote);
    remoteFound.countDown();
    write("CONNECTED");
  }

  @Override
  public void traffic(String traffic) {
    write("TRAFFIC:" + traffic);
  }

  @Override
  public void delta(String data) {
    write("DELTA:" + data);
  }

  @Override
  public void error(int code) {
    write("ERROR:" + code);
  }

  @Override
  public void disconnected() {
    write("DISCONNECTED");
  }

  private synchronized void write(String x) {
    System.err.println("MOCK:" + x);
    history.add(x);
    for (CountDownLatch latch : latches) {
      latch.countDown();
    }
  }
}
