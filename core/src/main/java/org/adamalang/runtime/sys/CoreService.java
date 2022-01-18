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
import org.adamalang.common.*;
import org.adamalang.runtime.contracts.*;
import org.adamalang.runtime.json.PrivateView;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.metering.MeteringStateMachine;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/** The core service enables consumers to manage an in-process Adama */
public class CoreService {
  private final CoreMetrics metrics;
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
      CoreMetrics metrics,
      LivingDocumentFactoryFactory livingDocumentFactoryFactory,
      Consumer<HashMap<String, PredictiveInventory.MeteringSample>> meteringEvent,
      DataService dataService,
      TimeSource time,
      int nThreads) {
    this.metrics = metrics;
    this.livingDocumentFactoryFactory = livingDocumentFactoryFactory;
    bases = new DocumentThreadBase[nThreads];
    this.alive = new AtomicBoolean(true);
    for (int k = 0; k < nThreads; k++) {
      bases[k] = new DocumentThreadBase(dataService, SimpleExecutor.create("core-" + k), time);
      bases[k].kickOffInventory();
    }
    rng = new Random();
    new NamedRunnable("metering-run") {
      @Override
      public void execute() {
        NamedRunnable self = this;
        MeteringStateMachine.estimate(
            bases,
            livingDocumentFactoryFactory, samples -> {
              meteringEvent.accept(samples);
              bases[rng.nextInt(bases.length)].executor.schedule(self, 1000);
            });
      }
    }.run();
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

  /** reflect on the document's schema */
  public void reflect(Key key, Callback<String> callbackReal) {
    Callback<String> callback = metrics.reflect.wrap(callbackReal);
    int threadId = key.hashCode() % bases.length;
    DocumentThreadBase base = bases[threadId];
    base.executor.execute(
        new NamedRunnable("reflect", key.space) {
          @Override
          public void execute() throws Exception {
            livingDocumentFactoryFactory.fetch(key, new Callback<LivingDocumentFactory>() {
              @Override
              public void success(LivingDocumentFactory value) {
                callback.success(value.reflection);
              }

              @Override
              public void failure(ErrorCodeException ex) {
                callback.failure(ex);
              }
            });
          }
        });
  }

  /** create a document */
  public void create(NtClient who, Key key, String arg, String entropy, Callback<Void> callbackReal) {
    Callback<Void> callback = metrics.serviceCreate.wrap(callbackReal);
    // jump into thread caching which thread
    int threadId = key.hashCode() % bases.length;
    DocumentThreadBase base = bases[threadId];
    base.executor.execute(
        new NamedRunnable("create", key.space) {
          @Override
          public void execute() throws Exception {
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
                metrics.factoryFetchCreate.wrap(new Callback<LivingDocumentFactory>() {
                  @Override
                  public void success(LivingDocumentFactory factory) {
                    try {
                      if (!factory.canCreate(who)) {
                        callback.failure(new ErrorCodeException(ErrorCodes.SERVICE_DOCUMENT_REJECTED_CREATION));
                        return;
                      }
                    } catch (ErrorCodeException exNew) {
                      callback.failure(exNew);
                      return;
                    }
                    // bring the document into existence
                    DurableLivingDocument.fresh(
                        key,
                        factory,
                        who,
                        arg,
                        entropy,
                        null,
                        base,
                        metrics.documentFresh.wrap(new Callback<>() {
                          @Override
                          public void success(DurableLivingDocument document) {
                            // jump into the thread; note, the data service must ensure this will
                            // succeed once
                            base.executor.execute(
                                new NamedRunnable("loaded-factory", key.space) {
                                  @Override
                                  public void execute() throws Exception {
                                    base.map.put(key, document);
                                    document.scheduleCleanup();
                                    callback.success(null);
                                  }
                                });
                          }

                          @Override
                          public void failure(ErrorCodeException ex) {
                            callback.failure(ex);
                          }
                        }));
                  }

                  @Override
                  public void failure(ErrorCodeException ex) {
                    callback.failure(ex);
                  }
                }));
          }
        });
  }

  /** connect the given person to the document hooking up a streamback */
  public void connect(NtClient who, Key key, Streamback stream) {
    connect(who, key, stream, true);
  }

  /** internal: do the connect with retry */
  private void connect(NtClient who, Key key, Streamback stream, boolean canRetry) {
    // TODO: instrument the stream
    load(
        key,
        new Callback<>() {
          @Override
          public void success(DurableLivingDocument document) {
            connectDirectMustBeInDocumentBase(who, document, stream);
          }

          @Override
          public void failure(ErrorCodeException exOriginal) {
            if (exOriginal.code == ErrorCodes.UNIVERSAL_LOOKUP_FAILED) {
              livingDocumentFactoryFactory.fetch(
                  key,
                  metrics.factoryFetchConnect.wrap(new Callback<>() {
                    @Override
                    public void success(LivingDocumentFactory factory) {
                      try {
                        if (!factory.canInvent(who)) {
                          stream.failure(exOriginal);
                          return;
                        }
                        create(
                            who,
                            key,
                            "{}",
                            null,
                            metrics.implicitCreate.wrap(new Callback<Void>() {
                              @Override
                              public void success(Void value) {
                                connect(who, key, stream, false);
                              }

                              @Override
                              public void failure(ErrorCodeException ex) {
                                if (ex.code == ErrorCodes.UNIVERSAL_INITIALIZE_FAILURE) {
                                  connect(who, key, stream, false);
                                } else {
                                  stream.failure(exOriginal);
                                }
                              }
                            }));
                      } catch (ErrorCodeException exNew) {
                        stream.failure(exNew);
                      }
                    }

                    @Override
                    public void failure(ErrorCodeException ex) {
                      stream.failure(exOriginal);
                    }
                  }));
              return;
            }
            stream.failure(exOriginal);
          }
        });
  }

  private void load(Key key, Callback<DurableLivingDocument> callbackReal) {
    Callback<DurableLivingDocument> callback = metrics.serviceLoad.wrap(callbackReal);

    // bind to the thread
    int threadId = key.hashCode() % bases.length;
    DocumentThreadBase base = bases[threadId];

    // jump into thread
    base.executor.execute(
        new NamedRunnable("load", key.space, key.key) {
          @Override
          public void execute() throws Exception {

            // is document already loaded?
            DurableLivingDocument documentFetch = base.map.get(key);
            if (documentFetch != null) {
              callback.success(documentFetch);
              return;
            }

            // let's load the factory and pull from source
            livingDocumentFactoryFactory.fetch(
                key,
                metrics.factoryFetchLoad.wrap(new Callback<>() {
                  @Override
                  public void success(LivingDocumentFactory factory) {
                    // pull from data source
                    DurableLivingDocument.load(
                        key,
                        factory,
                        null,
                        base,
                        metrics.documentLoad.wrap(new Callback<>() {
                          @Override
                          public void success(DurableLivingDocument documentMade) {
                            // it was found, let's try to put it into memory
                            base.executor.execute(
                                new NamedRunnable("document-made") {
                                  @Override
                                  public void execute() throws Exception {
                                    // attempt to put
                                    DurableLivingDocument documentPut =
                                        base.map.putIfAbsent(key, documentMade);
                                    if (documentPut == null) {
                                      // the put was successful, so use the newly made document
                                      documentPut = documentMade;
                                    }
                                    callback.success(documentPut);
                                    DurableLivingDocument _putForClosure = documentPut;
                                    base.executor.schedule(
                                        new NamedRunnable("post-load-reconcile") {
                                          @Override
                                          public void execute() throws Exception {
                                            _putForClosure.reconcileClients();
                                          }
                                        },
                                        base.getMillisecondsAfterLoadForReconciliation());
                                  }
                                });
                          }

                          @Override
                          public void failure(ErrorCodeException ex) {
                            callback.failure(ex);
                          }
                        }));
                  }

                  @Override
                  public void failure(ErrorCodeException ex) {
                    callback.failure(ex);
                  }
                }));
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
                metrics.createPrivateView.wrap(new Callback<>() {
                  @Override
                  public void success(PrivateView view) {
                    stream.onSetupComplete(new CoreStream(metrics, who, inventory, document, view));
                  }

                  @Override
                  public void failure(ErrorCodeException ex) {
                    stream.failure(ex);
                  }
                }));
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
          new NamedRunnable("deploy-" + kThreadLocal) {
            @Override
            public void execute() throws Exception {
              for (Map.Entry<Key, DurableLivingDocument> entry :
                  bases[kThreadLocal].map.entrySet()) {
                deploy(bases[kThreadLocal], entry.getKey(), entry.getValue(), monitor);
              }
            }
          });
    }
  }

  /** internal: deploy a specific document */
  private void deploy(
      DocumentThreadBase base, Key key, DurableLivingDocument document, DeploymentMonitor monitor) {
    livingDocumentFactoryFactory.fetch(
        key,
        metrics.factoryFetchDeploy.wrap(new Callback<>() {
          @Override
          public void success(LivingDocumentFactory newFactory) {
            base.executor.execute(
                new NamedRunnable("deploy", key.space, key.key) {
                  @Override
                  public void execute() throws Exception {
                    boolean toChange = document.getCurrentFactory() != newFactory;
                    monitor.bumpDocument(toChange);
                    if (toChange) {
                      try {
                        document.deploy(
                            newFactory,
                            metrics.deploy.wrap(new Callback<Integer>() {
                              @Override
                              public void success(Integer value) {}

                              @Override
                              public void failure(ErrorCodeException ex) {
                                monitor.witnessException(ex);
                              }
                            }));
                      } catch (ErrorCodeException ex) {
                        monitor.witnessException(ex);
                      }
                    }
                  }
                });
          }

          @Override
          public void failure(ErrorCodeException ex) {
            monitor.witnessException(ex);
          }
        }));
  }
}
