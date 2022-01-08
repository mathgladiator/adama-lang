/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.sys;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.TimeSource;
import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.contracts.LivingDocumentFactoryFactory;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.runtime.data.InMemoryDataService;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class BillingStateMachineTests {
  @Test
  public void flow() throws Exception {
    ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    DocumentThreadBase[] bases = new DocumentThreadBase[5];
    for (int k = 0; k < bases.length; k++) {
      bases[k] =
          new DocumentThreadBase(
              new InMemoryDataService((x) -> x.run(), TimeSource.REAL_TIME),
              new SimpleExecutor() {
                @Override
                public void execute(Runnable command) {
                  service.execute(command);
                }

                @Override
                public void schedule(Runnable command, long milliseconds) {
                  service.schedule(command, milliseconds, TimeUnit.MILLISECONDS);
                }

                @Override
                public CountDownLatch shutdown() {
                  return new CountDownLatch(0);
                }
              },
              TimeSource.REAL_TIME);
      }
    AtomicReference<HashMap<String, PredictiveInventory.Billing>> billing = new AtomicReference<>(null);
    CountDownLatch latch = new CountDownLatch(1);
    BillingStateMachine.bill(bases, new LivingDocumentFactoryFactory() {
      @Override
      public void fetch(Key key, Callback<LivingDocumentFactory> callback) {

      }

      @Override
      public Collection<String> spacesAvailable() {
        HashSet<String> set = new HashSet<>();
        set.add("x");
        set.add("y");
        return set;
      }
    }, (bill) -> {
      billing.set(bill);
      latch.countDown();
    });
    Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    Assert.assertEquals(2, billing.get().size());
  }

  @Test
  public void more_real() throws Exception {
    ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    DocumentThreadBase[] bases = new DocumentThreadBase[5];
    for (int k = 0; k < bases.length; k++) {
      bases[k] =
          new DocumentThreadBase(
              new InMemoryDataService((x) -> x.run(), TimeSource.REAL_TIME),
              new SimpleExecutor() {
                @Override
                public void execute(Runnable command) {
                  service.execute(command);
                }

                @Override
                public void schedule(Runnable command, long milliseconds) {
                  service.schedule(command, milliseconds, TimeUnit.MILLISECONDS);
                }

                @Override
                public CountDownLatch shutdown() {
                  return new CountDownLatch(0);
                }
              },
              TimeSource.REAL_TIME);
      bases[k].setInventoryMillisecondsSchedule(10, 5);
      bases[k].kickOffInventory();
    }
    LivingDocumentFactory factory = LivingDocumentTests.compile("public int x = 123;");
    {
      CountDownLatch latch = new CountDownLatch(1);
      DurableLivingDocument.fresh(new Key("space", "key"), factory, NtClient.NO_ONE, "{}", null, null, bases[0], new Callback<DurableLivingDocument>() {
        @Override
        public void success(DurableLivingDocument value) {
          bases[0].executor.execute(() -> {
            bases[0].map.put(new Key("space", "key"), value);
            latch.countDown();
          });
        }

        @Override
        public void failure(ErrorCodeException ex) {
          ex.printStackTrace();
        }
      });
      Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    }
    int attempts = 0;
    while (attempts < 10) {
      attempts++;
      AtomicReference<HashMap<String, PredictiveInventory.Billing>> billing = new AtomicReference<>(null);
      CountDownLatch latch = new CountDownLatch(1);
      BillingStateMachine.bill(
          bases,
          new LivingDocumentFactoryFactory() {
            @Override
            public void fetch(Key key, Callback<LivingDocumentFactory> callback) {
              try {
                callback.success(factory);
              } catch (Exception ex) {
                callback.failure(ErrorCodeException.detectOrWrap(100, ex, (t, c) -> {}));
              }
            }

            @Override
            public Collection<String> spacesAvailable() {
              return Collections.emptyList();
            }
          },
          (bill) -> {
            billing.set(bill);
            latch.countDown();
          });
      Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
      if (billing.get().size() == 1) {
        Assert.assertEquals(1, billing.get().get("space").count);
        return;
      }
      Thread.sleep(20);
    }
    Assert.fail("never billed for the object");
  }

  @Test
  public void preload() throws Exception {
    ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    DocumentThreadBase[] bases = new DocumentThreadBase[5];
    for (int k = 0; k < bases.length; k++) {
      bases[k] =
          new DocumentThreadBase(
              new InMemoryDataService((x) -> x.run(), TimeSource.REAL_TIME),
              new SimpleExecutor() {
                @Override
                public void execute(Runnable command) {
                  service.execute(command);
                }

                @Override
                public void schedule(Runnable command, long milliseconds) {
                  service.schedule(command, milliseconds, TimeUnit.MILLISECONDS);
                }

                @Override
                public CountDownLatch shutdown() {
                  return new CountDownLatch(0);
                }
              },
              TimeSource.REAL_TIME);
      bases[k].kickOffInventory();
    }
    LivingDocumentFactory factory = LivingDocumentTests.compile("public int x = 123;");
    AtomicReference<HashMap<String, PredictiveInventory.Billing>> billing = new AtomicReference<>(null);
    {
      CountDownLatch latch = new CountDownLatch(1);
      DurableLivingDocument.fresh(new Key("space", "key"), factory, NtClient.NO_ONE, "{}", null, null, bases[0], new Callback<DurableLivingDocument>() {
        @Override
        public void success(DurableLivingDocument value) {
          bases[0].executor.execute(() -> {
            bases[0].map.put(new Key("space", "key"), value);
            bases[0].performInventory();
            latch.countDown();
          });
        }

        @Override
        public void failure(ErrorCodeException ex) {
          ex.printStackTrace();
        }
      });
      Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    }
    CountDownLatch latch = new CountDownLatch(1);
    BillingStateMachine.bill(bases, new LivingDocumentFactoryFactory() {
      @Override
      public void fetch(Key key, Callback<LivingDocumentFactory> callback) {
        try {
          callback.success(factory);
        } catch (Exception ex) {
          callback.failure(ErrorCodeException.detectOrWrap(100, ex, (t, c) -> {}));
        }
      }

      @Override
      public Collection<String> spacesAvailable() {
        return Collections.singleton("space");
      }
    }, (bill) -> {
      billing.set(bill);
      latch.countDown();
    });
    Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    Assert.assertEquals(1, billing.get().size());
    Assert.assertEquals(1, billing.get().get("space").count);
  }
}
