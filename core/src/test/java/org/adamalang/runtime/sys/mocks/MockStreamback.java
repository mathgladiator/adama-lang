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
package org.adamalang.runtime.sys.mocks;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.contracts.Streamback;
import org.adamalang.runtime.sys.CoreStream;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MockStreamback implements Streamback {

  private final CountDownLatch began;
  private final CountDownLatch failed;
  private final ArrayList<CountDownLatch> latches;
  private ErrorCodeException failure;
  private CoreStream stream;
  private ArrayList<String> dataList;

  public MockStreamback() {
    this.began = new CountDownLatch(1);
    this.failure = null;
    this.failed = new CountDownLatch(1);
    this.dataList = new ArrayList<>();
    this.latches = new ArrayList<>();
  }

  public CoreStream get() {
    return stream;
  }

  @Override
  public void onSetupComplete(CoreStream stream) {
    this.stream = stream;
    if (began.getCount() == 0) {
      Assert.fail();
    }
    began.countDown();
  }

  @Override
  public void status(StreamStatus status) {
    next("STATUS:" + status);
  }

  @Override
  public synchronized void next(String data) {
    this.dataList.add(data);
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
  public void traffic(String trafficHint) {
    next("TRAFFIC:" + trafficHint);
  }

  @Override
  public void failure(ErrorCodeException exception) {
    failure = exception;
    failed.countDown();
  }

  public void await_began() {
    try {
      Assert.assertTrue(began.await(2000, TimeUnit.MILLISECONDS));
    } catch (InterruptedException ie) {
      Assert.fail();
    }
  }

  public synchronized String get(int k) {
    return dataList.get(k);
  }

  public synchronized int size() {
    return dataList.size();
  }

  public synchronized Runnable latchAt(int count) {
    CountDownLatch latch = new CountDownLatch(count);
    latches.add(latch);
    return () -> {
      try {
        Assert.assertTrue(latch.await(2000, TimeUnit.MILLISECONDS));
      } catch (InterruptedException ie) {
        Assert.fail();
      }
    };
  }

  public void await_failure(int code) {
    try {
      Assert.assertTrue(failed.await(2000, TimeUnit.MILLISECONDS));
      Assert.assertEquals(code, failure.code);
    } catch (InterruptedException ie) {
      Assert.fail();
    }
  }
}
