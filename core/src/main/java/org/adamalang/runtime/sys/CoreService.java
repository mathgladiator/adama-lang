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

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.TimeSource;
import org.adamalang.runtime.contracts.*;
import org.adamalang.runtime.json.PrivateView;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.threads.NamedThreadFactory;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/** The core service enables consumers to manage an in-process Adama */
public class CoreService {
  private final LivingDocumentFactoryFactory livingDocumentFactoryFactory;
  private final DocumentThreadBase[] bases;
  private final AtomicBoolean alive;
  private final Random rng;

  /**
   * @param livingDocumentFactoryFactory a mapping of how living documents come into existence
   * @param dataService the data service for durability
   * @param time the source of time
   * @param nThreads the number of threads to use
   */
  public CoreService(
      LivingDocumentFactoryFactory livingDocumentFactoryFactory,
      Consumer<HashMap<String, PredictiveInventory.Billing>> billing,
      DataService dataService,
      TimeSource time,
      int nThreads) {
    this.livingDocumentFactoryFactory = livingDocumentFactoryFactory;
    bases = new DocumentThreadBase[nThreads];
    this.alive = new AtomicBoolean(true);
    for (int k = 0; k < nThreads; k++) {
      ScheduledExecutorService realExecutorToUse =
          Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("core-" + k));
      SimpleExecutor executor =
          new SimpleExecutor() {
            @Override
            public void execute(Runnable command) {
              realExecutorToUse.execute(command);
            }

            @Override
            public void schedule(Runnable command, long milliseconds) {
              // register to pick up this key on restart
              realExecutorToUse.schedule(
                  () -> {
                    if (alive.get()) {
                      command.run();
                    }
                  },
                  milliseconds,
                  TimeUnit.MILLISECONDS);
            }

            @Override
            public CountDownLatch shutdown() {
              CountDownLatch latch = new CountDownLatch(1);
              realExecutorToUse.execute(
                  () -> {
                    for (Runnable run : realExecutorToUse.shutdownNow()) {
                      run.run();
                    }
                    latch.countDown();
                  });
              return latch;
            }
          };
      bases[k] = new DocumentThreadBase(dataService, executor, time);
      bases[k].kickOffInventory();
    }
    rng = new Random();
    Runnable doBilling = new Runnable() {
      @Override
      public void run() {
        Runnable self = this;
        BillingStateMachine.bill(bases, livingDocumentFactoryFactory, new Consumer<HashMap<String, PredictiveInventory.Billing>>() {
          @Override
          public void accept(HashMap<String, PredictiveInventory.Billing> bill) {
            billing.accept(bill);
            bases[rng.nextInt(bases.length)].executor.schedule(self, 1000);
          }
        });
      }
    };
    doBilling.run();
  }

  private void load(Key key, Callback<DurableLivingDocument> callback) {
    // bind to the thread
    int threadId = key.hashCode() % bases.length;
    DocumentThreadBase base = bases[threadId];

    // jump into thread
    base.executor.execute(
        () -> {

          // is document already loaded?
          DurableLivingDocument documentFetch = base.map.get(key);
          if (documentFetch != null) {
            callback.success(documentFetch);
            return;
          }

          // let's load the factory and pull from source
          livingDocumentFactoryFactory.fetch(
              key,
              new Callback<>() {
                @Override
                public void success(LivingDocumentFactory factory) {
                  // pull from data source
                  DurableLivingDocument.load(
                      key,
                      factory,
                      null,
                      base,
                      new Callback<>() {
                        @Override
                        public void success(DurableLivingDocument documentMade) {
                          // it was found, let's try to put it into memory
                          base.executor.execute(
                              () -> {
                                // attempt to put
                                DurableLivingDocument documentPut =
                                    base.map.putIfAbsent(key, documentMade);
                                if (documentPut == null) {
                                  // the put was successful, so use the newly made document
                                  documentPut = documentMade;
                                }
                                callback.success(documentPut);
                                base.executor.schedule(
                                    documentPut::postLoadReconcile,
                                    base.getMillisecondsAfterLoadForReconciliation());
                              });
                        }

                        @Override
                        public void failure(ErrorCodeException ex) {
                          callback.failure(ex);
                        }
                      });
                }

                @Override
                public void failure(ErrorCodeException ex) {
                  callback.failure(ex);
                }
              });
        });
  }

  public void shutdown() throws InterruptedException {
    alive.set(false);
    CountDownLatch[] latches = new CountDownLatch[bases.length];
    for (int kThread = 0; kThread < bases.length; kThread++) {
      latches[kThread] = bases[kThread].executor.shutdown();
    }
    for (int kThread = 0; kThread < bases.length; kThread++) {
      latches[kThread].await(1000, TimeUnit.MILLISECONDS);
    }
  }

  /** create a document */
  public void create(NtClient who, Key key, String arg, String entropy, Callback<Void> callback) {
    // jump into thread caching which thread
    int threadId = key.hashCode() % bases.length;
    DocumentThreadBase base = bases[threadId];
    base.executor.execute(
        () -> {
          // the document already exists
          if (base.map.containsKey(key)) {
            callback.failure(new ErrorCodeException(ErrorCodes.SERVICE_DOCUMENT_ALREADY_CREATED));
            return;
          }
          // estimate the impact
          base.getOrCreateInventory(key.space).grow();
          // fetch the factory
          livingDocumentFactoryFactory.fetch(
              key,
              new Callback<LivingDocumentFactory>() {
                @Override
                public void success(LivingDocumentFactory factory) {
                  // bring the document into existence
                  DurableLivingDocument.fresh(
                      key,
                      factory,
                      who,
                      arg,
                      entropy,
                      null,
                      base,
                      new Callback<>() {
                        @Override
                        public void success(DurableLivingDocument document) {
                          // jump into the thread; note, the data service must ensure this will
                          // succeed once
                          base.executor.execute(
                              () -> {
                                // try to put into the map
                                base.map.put(key, document);
                                document.scheduleCleanup();
                                callback.success(null);
                              });
                        }

                        @Override
                        public void failure(ErrorCodeException ex) {
                          callback.failure(ex);
                        }
                      });
                }

                @Override
                public void failure(ErrorCodeException ex) {
                  callback.failure(ex);
                }
              });
        });
  }

  /** connect the given person to the document hooking up a streamback */
  public void connect(NtClient who, Key key, Streamback stream) {
    load(
        key,
        new Callback<>() {
          @Override
          public void success(DurableLivingDocument document) {
            connectDirectMustBeInDocumentBase(who, document, stream);
          }

          @Override
          public void failure(ErrorCodeException ex) {
            stream.failure(ex);
          }
        });
  }

  /** internal: send connection to the document if not joined, then join */
  private void connectDirectMustBeInDocumentBase(
      NtClient who, DurableLivingDocument document, Streamback stream) {
    PredictiveInventory inventory = document.base.getOrCreateInventory(document.key.space);
    Callback<Integer> onConnected =
        new Callback<>() {
          @Override
          public void success(Integer value) {
            stream.status(Streamback.StreamStatus.Connected);
            document.createPrivateView(
                who,
                new Perspective() {
                  @Override
                  public void data(String data) {
                    stream.next(data);
                  }

                  @Override
                  public void disconnect() {
                    stream.status(Streamback.StreamStatus.Disconnected);
                  }
                },
                new Callback<>() {
                  @Override
                  public void success(PrivateView view) {
                    CoreStream core = new CoreStream(who, inventory, document, view);
                    stream.onSetupComplete(core);
                  }

                  @Override
                  public void failure(ErrorCodeException ex) {
                    stream.failure(ex);
                  }
                });
          }

          @Override
          public void failure(ErrorCodeException ex) {
            stream.failure(ex);
          }
        };

    // are we already connected, then execute now
    if (document.isConnected(who)) {
      onConnected.success(null);
    } else {
      document.connect(who, onConnected);
    }
  }

  public void tune(Consumer<DocumentThreadBase> tuner) {
    for (int kThread = 0; kThread < bases.length; kThread++) {
      tuner.accept(bases[kThread]);
    }
  }

  /**
   * a change has been made to the factory, so try to fetch all the new factories and deploy them
   */
  public void deploy(DeploymentMonitor monitor) {
    for (int kThread = 0; kThread < bases.length; kThread++) {
      final int kThreadLocal = kThread;
      bases[kThread].executor.execute(
          () -> {
            for (Map.Entry<Key, DurableLivingDocument> entry : bases[kThreadLocal].map.entrySet()) {
              deploy(bases[kThreadLocal], entry.getKey(), entry.getValue(), monitor);
            }
          });
    }
  }

  /** internal: deploy a specific document */
  private void deploy(
      DocumentThreadBase base, Key key, DurableLivingDocument document, DeploymentMonitor monitor) {
    livingDocumentFactoryFactory.fetch(
        key,
        new Callback<>() {
          @Override
          public void success(LivingDocumentFactory newFactory) {
            base.executor.execute(
                () -> {
                  boolean toChange = document.getCurrentFactory() != newFactory;
                  monitor.bumpDocument(toChange);
                  if (toChange) {
                    try {
                      document.deploy(
                          newFactory,
                          new Callback<Integer>() {
                            @Override
                            public void success(Integer value) {}

                            @Override
                            public void failure(ErrorCodeException ex) {
                              monitor.witnessException(ex);
                            }
                          });
                    } catch (ErrorCodeException ex) {
                      monitor.witnessException(ex);
                    }
                  }
                });
          }

          @Override
          public void failure(ErrorCodeException ex) {
            monitor.witnessException(ex);
          }
        });
  }
}
