/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.sys.mocks;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.contracts.Key;
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
