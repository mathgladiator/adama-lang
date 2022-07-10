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
import org.adamalang.runtime.contracts.DeploymentMonitor;
import org.adamalang.runtime.contracts.LivingDocumentFactoryFactory;
import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.runtime.contracts.Streamback;
import org.adamalang.runtime.data.DataService;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.delta.secure.AssetIdEncoder;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.PrivateView;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.sys.metering.MeteringStateMachine;
import org.adamalang.runtime.sys.web.WebGet;
import org.adamalang.runtime.sys.web.WebPutRaw;
import org.adamalang.runtime.sys.web.WebResponse;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

/** The core service enables consumers to manage an in-process Adama */
public class CoreService {
  private static final Logger LOGGER = LoggerFactory.getLogger(CoreService.class);
  public final DataService dataService;
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
  public CoreService(CoreMetrics metrics, LivingDocumentFactoryFactory livingDocumentFactoryFactory, Consumer<HashMap<String, PredictiveInventory.MeteringSample>> meteringEvent, DataService dataService, TimeSource time, int nThreads) {
    this.metrics = metrics;
    this.dataService = dataService;
    this.livingDocumentFactoryFactory = livingDocumentFactoryFactory;
    bases = new DocumentThreadBase[nThreads];
    this.alive = new AtomicBoolean(true);
    for (int k = 0; k < nThreads; k++) {
      bases[k] = new DocumentThreadBase(dataService, metrics, SimpleExecutor.create("core-" + k), time);
      bases[k].kickOffInventory();
    }
    rng = new Random();
    new NamedRunnable("metering-run") {
      @Override
      public void execute() {
        long started = System.currentTimeMillis();
        NamedRunnable self = this;
        MeteringStateMachine.estimate(bases, livingDocumentFactoryFactory, samples -> {
          meteringEvent.accept(samples);
          bases[rng.nextInt(bases.length)].executor.schedule(self, Math.max(1000 - (System.currentTimeMillis() - started), 100));
        });
      }
    }.run();
  }

  public void shed(Function<Key, Boolean> condition) {
    for (int k = 0; k < bases.length; k++) {
      bases[k].shed(condition);
    }
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
    base.executor.execute(new NamedRunnable("reflect", key.space) {
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
  public void create(CoreRequestContext context, Key key, String arg, String entropy, Callback<Void> callbackReal) {
    createInternal(context, key, arg, entropy, metrics.serviceCreate.wrap(callbackReal));
  }

  /** internal: actually create with the given callback */
  private void createInternal(CoreRequestContext context, Key key, String arg, String entropy, Callback<Void> callback) {
    // jump into thread caching which thread
    int threadId = key.hashCode() % bases.length;
    DocumentThreadBase base = bases[threadId];
    base.executor.execute(new NamedRunnable("create", key.space) {
      @Override
      public void execute() throws Exception {
        // the document already exists
        if (base.map.containsKey(key)) {
          callback.failure(new ErrorCodeException(ErrorCodes.SERVICE_DOCUMENT_ALREADY_CREATED));
          return;
        }

        // since create will typically fail, we want to only retry the requests if there are concurrent calls
        ArrayList<Runnable> concurrentCreateCalls = base.mapCreationsInflightRetryBuffer.get(key);
        if (concurrentCreateCalls != null) {
          concurrentCreateCalls.add(() -> create(context, key, arg, entropy, callback));
          return;
        }
        base.mapCreationsInflightRetryBuffer.put(key, new ArrayList<>());
        Runnable executeConcurrent = () -> {
          base.executor.execute(new NamedRunnable("retry-connect") {
            @Override
            public void execute() throws Exception {
              for (Runnable other : base.mapCreationsInflightRetryBuffer.remove(key)) {
                other.run();
              }
            }
          });
        };

        // estimate the impact
        base.getOrCreateInventory(key.space).grow();
        // fetch the factory
        livingDocumentFactoryFactory.fetch(key, metrics.factoryFetchCreate.wrap(new Callback<LivingDocumentFactory>() {
          @Override
          public void success(LivingDocumentFactory factory) {
            try {
              if (!factory.canCreate(context)) {
                callback.failure(new ErrorCodeException(ErrorCodes.SERVICE_DOCUMENT_REJECTED_CREATION));
                executeConcurrent.run();
                return;
              }
            } catch (ErrorCodeException exNew) {
              callback.failure(exNew);
              executeConcurrent.run();
              return;
            }
            // bring the document into existence
            DurableLivingDocument.fresh(key, factory, context.who, arg, entropy, null, base, metrics.documentFresh.wrap(new Callback<>() {
              @Override
              public void success(DurableLivingDocument document) {
                // jump into the thread; note, the data service must ensure this will succeed once
                base.executor.execute(new NamedRunnable("loaded-factory", key.space) {
                  @Override
                  public void execute() throws Exception {
                    callback.success(null);
                    executeConcurrent.run();
                  }
                });
              }

              @Override
              public void failure(ErrorCodeException ex) {
                callback.failure(ex);
                executeConcurrent.run();
              }
            }));
          }

          @Override
          public void failure(ErrorCodeException ex) {
            callback.failure(ex);
            executeConcurrent.run();
          }
        }));
      }
    });
  }

  /** connect the given person to the document hooking up a streamback */
  public void connect(CoreRequestContext context, Key key, String viewerState, AssetIdEncoder assetIdEncoder, Streamback stream) {
    connect(context, key, stream, viewerState, assetIdEncoder, true);
  }

  /** internal: do the connect with retry when connect executes create */
  private void connect(CoreRequestContext context, Key key, Streamback stream, String viewerState, AssetIdEncoder assetIdEncoder, boolean canRetry) {
    // TODO: instrument the stream
    load(key, new Callback<>() {
      @Override
      public void success(DurableLivingDocument document) {
        connectDirectMustBeInDocumentBase(context, document, stream, new JsonStreamReader(viewerState), assetIdEncoder);
      }

      @Override
      public void failure(ErrorCodeException exOriginal) {
        if (exOriginal.code == ErrorCodes.UNIVERSAL_LOOKUP_FAILED && canRetry) {
          livingDocumentFactoryFactory.fetch(key, metrics.factoryFetchConnect.wrap(new Callback<>() {
            @Override
            public void success(LivingDocumentFactory factory) {
              try {
                if (!factory.canInvent(context)) {
                  stream.failure(exOriginal);
                  return;
                }
                create(context, key, "{}", null, metrics.implicitCreate.wrap(new Callback<Void>() {
                  @Override
                  public void success(Void value) {
                    connect(context, key, stream, viewerState, assetIdEncoder, canRetry);
                  }

                  @Override
                  public void failure(ErrorCodeException exNew) {
                    if (exNew.code == ErrorCodes.UNIVERSAL_INITIALIZE_FAILURE || exNew.code == ErrorCodes.LIVING_DOCUMENT_TRANSACTION_ALREADY_CONSTRUCTED || exNew.code == ErrorCodes.SERVICE_DOCUMENT_ALREADY_CREATED) {
                      connect(context, key, stream, viewerState, assetIdEncoder, canRetry);
                    } else {
                      metrics.failed_invention.run();
                      stream.failure(exNew);
                    }
                  }
                }));
              } catch (ErrorCodeException exNew) {
                stream.failure(exNew);
              }
            }

            @Override
            public void failure(ErrorCodeException ex) {
              stream.failure(ex);
            }
          }));
          return;
        }
        stream.failure(exOriginal);
      }
    });
  }

  private void load(Key key, Callback<DurableLivingDocument> callbackReal) {
    Callback<DurableLivingDocument> callbackToQueue = metrics.serviceLoad.wrap(callbackReal);

    // bind to the thread
    int threadId = key.hashCode() % bases.length;
    DocumentThreadBase base = bases[threadId];

    // jump into thread
    base.executor.execute(new NamedRunnable("load", key.space, key.key) {
      @Override
      public void execute() throws Exception {

        // is document already loaded?
        DurableLivingDocument documentFetch = base.map.get(key);
        if (documentFetch != null) {
          callbackToQueue.success(documentFetch);
          return;
        }

        ArrayList<Callback<DurableLivingDocument>> callbacks = base.mapInsertsInflight.get(key);
        if (callbacks == null) {
          callbacks = new ArrayList<>();
          callbacks.add(callbackToQueue);
          base.mapInsertsInflight.put(key, callbacks);

          Consumer<ErrorCodeException> failure = (ex) -> {
            base.executor.execute(new NamedRunnable("document-load-failure") {
              @Override
              public void execute() throws Exception {
                for (Callback<DurableLivingDocument> callbackToSignal : base.mapInsertsInflight.remove(key)) {
                  callbackToSignal.failure(ex);
                }
              }
            });
          };

          // let's load the factory and pull from source
          livingDocumentFactoryFactory.fetch(key, metrics.factoryFetchLoad.wrap(new Callback<>() {
            @Override
            public void success(LivingDocumentFactory factory) {
              // pull from data source
              DurableLivingDocument.load(key, factory, null, base, metrics.documentLoad.wrap(new Callback<>() {
                @Override
                public void success(DurableLivingDocument documentToAttemptPut) {
                  // it was found, let's try to put it into memory
                  base.executor.execute(new NamedRunnable("document-made") {
                    @Override
                    public void execute() throws Exception {
                      DurableLivingDocument priorDocumentFound = base.map.putIfAbsent(key, documentToAttemptPut);
                      if (priorDocumentFound != null) {
                        metrics.document_collision.run();
                        CoreService.LOGGER.error("found-prior-value, using it: {}", key.key);
                      }
                      DurableLivingDocument document = priorDocumentFound == null ? documentToAttemptPut : priorDocumentFound;
                      metrics.inflight_documents.up();
                      for (Callback<DurableLivingDocument> callbackToSignal : base.mapInsertsInflight.remove(key)) {
                        callbackToSignal.success(document);
                      }
                      base.executor.schedule(new NamedRunnable("post-load-reconcile") {
                        @Override
                        public void execute() throws Exception {
                          document.afterLoad();
                        }
                      }, base.getMillisecondsAfterLoadForReconciliation());
                    }
                  });
                }

                @Override
                public void failure(ErrorCodeException ex) {
                  failure.accept(ex);
                }
              }));
            }

            @Override
            public void failure(ErrorCodeException ex) {
              failure.accept(ex);
            }
          }));
        } else {
          callbacks.add(metrics.documentPiggyBack.wrap(callbackToQueue));
        }
      }
    });
  }

  /** internal: send connection to the document if not joined, then join */
  private void connectDirectMustBeInDocumentBase(CoreRequestContext context, DurableLivingDocument document, Streamback stream, JsonStreamReader viewerState, AssetIdEncoder assetIdEncoder) {
    PredictiveInventory inventory = document.base.getOrCreateInventory(document.key.space);
    Callback<Integer> onConnected = new Callback<>() {
      @Override
      public void success(Integer dontCare) {
        document.createPrivateView(context.who, new Perspective() {
          @Override
          public void data(String data) {
            stream.next(data);
            inventory.message();
          }

          @Override
          public void disconnect() {
            stream.status(Streamback.StreamStatus.Disconnected);
          }
        }, viewerState, assetIdEncoder, metrics.createPrivateView.wrap(new Callback<>() {
          @Override
          public void success(PrivateView view) {
            stream.onSetupComplete(new CoreStream(context, metrics, inventory, document, view));
            stream.status(Streamback.StreamStatus.Connected);
          }

          @Override
          public void failure(ErrorCodeException ex) {
            stream.failure(ex);
          }
        }));
      }

      @Override
      public void failure(ErrorCodeException ex) {
        if (ex.code == ErrorCodes.LIVING_DOCUMENT_TRANSACTION_ALREADY_CONNECTED) {
          success(null);
        } else {
          stream.failure(ex);
        }
      }
    };

    // are we already connected, then execute now
    if (document.isConnected(context.who)) {
      onConnected.success(null);
    } else {
      document.connect(context.who, onConnected);
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
      bases[kThread].executor.execute(new NamedRunnable("deploy-" + kThreadLocal) {
        @Override
        public void execute() throws Exception {
          for (Map.Entry<Key, DurableLivingDocument> entry : bases[kThreadLocal].map.entrySet()) {
            deploy(bases[kThreadLocal], entry.getKey(), entry.getValue(), monitor);
          }
        }
      });
    }
  }

  /** execute a web get against the document */
  public void webGet(Key key, WebGet request, Callback<WebResponse> callback) {
    load(key, new Callback<DurableLivingDocument>() {
      @Override
      public void success(DurableLivingDocument document) {
        document.registerActivity();
        WebResponse response = document.document().__get(request);
        if (response != null) {
          callback.success(response);
        } else {
          callback.failure(new ErrorCodeException(ErrorCodes.DOCUMENT_WEB_GET_NOT_FOUND));
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  public void startupLoad(Key key) {
    load(key, metrics.document_load_startup.wrap(DONT_CARE_DOCUMENT));
  }

  public static Callback<DurableLivingDocument> DONT_CARE_DOCUMENT = new Callback<DurableLivingDocument>() {
    @Override
    public void success(DurableLivingDocument value) {}

    @Override
    public void failure(ErrorCodeException ex) {}
  };

  /** execute a web put against the document */
  public void webPut(NtClient who, Key key, WebPutRaw request, Callback<WebResponse> callback) {
    load(key, new Callback<>() {
      @Override
      public void success(DurableLivingDocument document) {
        document.webPut(who, request, callback);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  /** internal: deploy a specific document */
  private void deploy(DocumentThreadBase base, Key key, DurableLivingDocument document, DeploymentMonitor monitor) {
    livingDocumentFactoryFactory.fetch(key, metrics.factoryFetchDeploy.wrap(new Callback<>() {
      @Override
      public void success(LivingDocumentFactory newFactory) {
        base.executor.execute(new NamedRunnable("deploy", key.space, key.key) {
          @Override
          public void execute() throws Exception {
            boolean toChange = document.getCurrentFactory() != newFactory;
            monitor.bumpDocument(toChange);
            if (toChange) {
              try {
                document.deploy(newFactory, metrics.deploy.wrap(new Callback<Integer>() {
                  @Override
                  public void success(Integer value) {
                  }

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
