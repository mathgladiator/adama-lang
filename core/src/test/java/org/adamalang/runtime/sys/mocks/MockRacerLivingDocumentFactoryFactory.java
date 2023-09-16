/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.contracts.LivingDocumentFactoryFactory;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MockRacerLivingDocumentFactoryFactory implements LivingDocumentFactoryFactory {
  private final HashMap<Key, ArrayList<Callback<LivingDocumentFactory>>> calls;
  private final ArrayList<CountDownLatch> latches;

  public MockRacerLivingDocumentFactoryFactory() {
    this.calls = new HashMap<>();
    this.latches = new ArrayList<>();
  }

  @Override
  public Collection<String> spacesAvailable() {
    return Collections.singleton("space");
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

  public void satisfyAll(Key key, LivingDocumentFactory factory) {
    ArrayList<Callback<LivingDocumentFactory>> callbacks = removeAt(key);
    if (callbacks != null) {
      for (Callback<LivingDocumentFactory> callback : callbacks) {
        callback.success(factory);
      }
    }
  }

  private synchronized ArrayList<Callback<LivingDocumentFactory>> removeAt(Key key) {
    return calls.remove(key);
  }

  public void satisfyNone(Key key) {
    ArrayList<Callback<LivingDocumentFactory>> callbacks = removeAt(key);
    for (Callback<LivingDocumentFactory> callback : callbacks) {
      callback.failure(new ErrorCodeException(50000));
    }
  }

  @Override
  public synchronized void fetch(Key key, Callback<LivingDocumentFactory> callback) {
    ArrayList<Callback<LivingDocumentFactory>> callsForKey = calls.get(key);
    if (callsForKey == null) {
      callsForKey = new ArrayList<>();
      calls.put(key, callsForKey);
    }
    callsForKey.add(callback);
    Iterator<CountDownLatch> it = latches.iterator();
    while (it.hasNext()) {
      CountDownLatch latch = it.next();
      latch.countDown();
      if (latch.getCount() == 0) {
        it.remove();
      }
    }
  }
}
