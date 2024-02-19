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
package org.adamalang.runtime.sys;

import org.adamalang.ErrorCodes;
import org.adamalang.common.*;
import org.adamalang.runtime.contracts.*;
import org.adamalang.runtime.data.DataObserver;
import org.adamalang.runtime.data.DataService;
import org.adamalang.runtime.data.DocumentRestore;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateView;
import org.adamalang.runtime.json.TrivialPrivateView;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.remote.MetricsReporter;
import org.adamalang.runtime.remote.RemoteResult;
import org.adamalang.runtime.sys.capacity.CurrentLoad;
import org.adamalang.runtime.sys.metering.MeteringStateMachine;
import org.adamalang.runtime.sys.web.WebDelete;
import org.adamalang.runtime.sys.web.WebGet;
import org.adamalang.runtime.sys.web.WebPut;
import org.adamalang.runtime.sys.web.WebResponse;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

/** The core service enables consumers to manage an in-process Adama */
public class CoreService implements Deliverer, Queryable {
  private static final Logger LOGGER = LoggerFactory.getLogger(CoreService.class);
  public static Callback<DurableLivingDocument> DONT_CARE_DOCUMENT = new Callback<DurableLivingDocument>() {
    @Override
    public void success(DurableLivingDocument value) {
    }

    @Override
    public void failure(ErrorCodeException ex) {
    }
  };
  public final DataService dataService;
  public final BackupService backupService;
  public final ServiceShield shield;
  public final CoreMetrics metrics;
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
  public CoreService(CoreMetrics metrics, LivingDocumentFactoryFactory livingDocumentFactoryFactory, Consumer<HashMap<String, PredictiveInventory.MeteringSample>> meteringEvent, MetricsReporter metricsReporter, DataService dataService, BackupService backupService, TimeSource time, int nThreads) {
    this.metrics = metrics;
    this.dataService = dataService;
    this.backupService = backupService;
    this.shield = new ServiceShield();
    this.livingDocumentFactoryFactory = livingDocumentFactoryFactory;
    bases = new DocumentThreadBase[nThreads];
    this.alive = new AtomicBoolean(true);
    for (int k = 0; k < nThreads; k++) {
      bases[k] = new DocumentThreadBase(k, shield, metricsReporter, dataService, backupService, metrics, SimpleExecutor.create("core-" + k), time);
      bases[k].kickOffInventory();
    }
    rng = new Random();
    new NamedRunnable("metering-run") {
      @Override
      public void execute() {
        long started = System.currentTimeMillis();
        NamedRunnable self = this;
        MeteringStateMachine.estimate(bases, livingDocumentFactoryFactory, samples -> {
          livingDocumentFactoryFactory.account(samples);
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

  /** this state machine will execute a drain over all the threads one at a time and signal each thread is being drained */
  private class DrainStateMachine extends NamedRunnable {
    private final TreeSet<Key> inventory;
    private final Callback<Void> callback;
    private int at;

    private DrainStateMachine(Set<Key> inventory, Callback<Void> callback) {
      super("drain-state-machine");
      this.inventory = new TreeSet<>(inventory);
      this.callback = callback;
      this.at = 0;
    }

    @Override
    public void execute() throws Exception {
      inventory.removeAll(bases[at].map.keySet());
      bases[at].shedFromWithinExecutor((key) -> true);
      bases[at].drain();
      this.at++;
      next();
    }

    private void next() {
      if (at >= bases.length) {
        for (Key key : inventory) {
          // force everything in the data service to shed
          dataService.shed(key);
        }
        callback.success(null);
      } else {
        bases[at].executor.execute(this);
      }
    }
  }

  public void drainService(Callback<Void> callback) {
    dataService.inventory(new Callback<Set<Key>>() {
      @Override
      public void success(Set<Key> keys) {
        new DrainStateMachine(keys, callback).next();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  /** state machine for computing the load of the service in terms of documents and connections */
  private class LoadGatherStateMachine extends NamedRunnable {
    private final int documents;
    private final Callback<CurrentLoad> callback;
    private int connections;
    private int at;

    private LoadGatherStateMachine(int documents, Callback<CurrentLoad> callback) {
      super("load-gather-state-machine");
      this.documents = documents;
      this.callback = callback;
      this.at = 0;
    }

    @Override
    public void execute() throws Exception {
      for (Map.Entry<Key, DurableLivingDocument> documentEntry : bases[at].map.entrySet()) {
        connections += documentEntry.getValue().getConnectionsCount();
      }
      this.at++;
      next();
    }

    private void next() {
      if (at >= bases.length) {
        callback.success(new CurrentLoad(documents, connections));
      } else {
        bases[at].executor.execute(this);
      }
    }
  }

  public void getLoad(Callback<CurrentLoad> callback) {
    dataService.inventory(new Callback<Set<Key>>() {
      @Override
      public void success(Set<Key> keys) {
        new LoadGatherStateMachine(keys.size(), callback).next();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
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

  @Override
  public void deliver(NtPrincipal agent, Key key, int id, RemoteResult result, boolean firstParty, Callback<Integer> callbackReal) {
    Callback<Integer> callback = metrics.deliver.wrap(callbackReal);
    load(key, new Callback<>() {
      @Override
      public void success(DurableLivingDocument document) {
        PredictiveInventory inventory = document.base.getOrCreateInventory(key.space);
        if (firstParty) {
          inventory.first_party_service_call();
        } else {
          inventory.third_party_service_call();
        }
        document.deliver(agent, id, result, callback);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  @Override
  public void query(TreeMap<String, String> query, Callback<String> callback) {
    if (query.containsKey("space") && query.containsKey("key")) {
      Key key = new Key(query.get("space"), query.get("key"));
      load(key, new Callback<>() {
        @Override
        public void success(DurableLivingDocument document) {
          document.query(query, callback);
        }

        @Override
        public void failure(ErrorCodeException ex) {
          callback.failure(ex);
        }
      });
    } else {
      callback.failure(new ErrorCodeException(ErrorCodes.QUERY_MADE_NO_SENSE));
    }
  }

  public void saveCustomerBackup(Key key, Callback<String> callback) {
    load(key, new Callback<DurableLivingDocument>() {
      @Override
      public void success(DurableLivingDocument doc) {
        JsonStreamWriter writer = new JsonStreamWriter();
        doc.document().__dump(writer);
        callback.success(writer.toString());
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  private void load(Key key, Callback<DurableLivingDocument> callbackReal) {
    loadInternal(key, metrics.serviceLoad.wrap(callbackReal));
  }

  private void loadInternal(Key key, Callback<DurableLivingDocument> callbackReal) {
    // bind to the thread
    int threadId = key.hashCode() % bases.length;
    DocumentThreadBase base = bases[threadId];

    Callback<DurableLivingDocument> callback = SimpleTimeout.WRAP(SimpleTimeout.make(base.executor, 30000, () -> {
      LOGGER.error("load-internal-timeout-loading:" + key.space + "/" + key.key);
    }), callbackReal);

    // jump into thread
    base.executor.execute(new NamedRunnable("load", key.space, key.key) {
      @Override
      public void execute() throws Exception {

        // is document already loaded?
        DurableLivingDocument documentFetch = base.map.get(key);
        if (documentFetch != null) {
          callback.success(documentFetch);
          return;
        }
        // we are draining, so don't event start with it
        if (base.isDrained()) {
          callback.failure(new ErrorCodeException(ErrorCodes.DOCUMENT_DRAINING_LOAD));
          return;
        }
        if (enqueueForLaterWhileInExecutorReturnAbort(base, key, () -> load(key, callback))) {
          return;
        }

        Consumer<ErrorCodeException> failure = (ex) -> {
          base.executor.execute(new NamedRunnable("document-load-failure") {
            @Override
            public void execute() throws Exception {
              callback.failure(ex);
              drain(base, key);
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
                    } else {
                      metrics.inflight_documents.up();
                    }
                    DurableLivingDocument document = priorDocumentFound == null ? documentToAttemptPut : priorDocumentFound;
                    if (base.isDrained()) {
                      callback.failure(new ErrorCodeException(ErrorCodes.DOCUMENT_DRAINING_LOAD));
                      document.shedWhileInExecutor();
                    } else {
                      callback.success(document);
                      base.executor.schedule(new NamedRunnable("post-load-reconcile") {
                        @Override
                        public void execute() throws Exception {
                          document.afterLoadWhileInExecutor();
                        }
                      }, base.getMillisecondsAfterLoadForReconciliation());
                    }
                    drain(base, key);
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

      }
    });
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

  /** delete a document */
  public void delete(CoreRequestContext context, Key key, Callback<Void> callback) {
    load(key, new Callback<DurableLivingDocument>() {
      @Override
      public void success(DurableLivingDocument value) {
        value.delete(context, callback);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  private boolean enqueueForLaterWhileInExecutorReturnAbort(DocumentThreadBase base, Key key, Runnable task) {
    ArrayList<Runnable> pending = base.pending.get(key);
    if (pending != null) {
      pending.add(task);
      return true;
    }
    base.pending.put(key, new ArrayList<>(0));
    return false;
  }

  private void drain(DocumentThreadBase base, Key key) {
    base.executor.execute(new NamedRunnable("retry-connect") {
      @Override
      public void execute() throws Exception {
        ArrayList<Runnable> tasks = base.pending.remove(key);
        for (Runnable other : tasks) {
          other.run();
        }
      }
    });
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
        // we are draining, so don't event start with it
        if (base.isDrained()) {
          callback.failure(new ErrorCodeException(ErrorCodes.DOCUMENT_DRAINING_LOAD));
          return;
        }
        // let's make sure we block any additional creation/load attempts
        if (enqueueForLaterWhileInExecutorReturnAbort(base, key, () -> createInternal(context, key, arg, entropy, callback))) {
          return;
        }
        Runnable afterExecuted = () -> {
          if (base.isDrained()) { // the document was created while a drain operation was initiatied, so shed the document here
            base.service.shed(key);
          }
          drain(base, key);
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
                afterExecuted.run();
                return;
              }
            } catch (ErrorCodeException exNew) {
              callback.failure(exNew);
              afterExecuted.run();
              return;
            }
            // bring the document into existence
            DurableLivingDocument.fresh(key, factory, context, arg, entropy, null, base, metrics.documentFresh.wrap(new Callback<>() {
              @Override
              public void success(DurableLivingDocument document) {
                // jump into the thread; note, the data service must ensure this will succeed once
                base.executor.execute(new NamedRunnable("loaded-factory", key.space) {
                  @Override
                  public void execute() throws Exception {
                    callback.success(null);
                    afterExecuted.run();
                  }
                });
              }

              @Override
              public void failure(ErrorCodeException ex) {
                callback.failure(ex);
                afterExecuted.run();
              }
            }));
          }

          @Override
          public void failure(ErrorCodeException ex) {
            callback.failure(ex);
            afterExecuted.run();
          }
        }));
      }
    });
  }

  /** watch a key for data changes */
  public void watch(Key key, DataObserver observer) {
    load(key, new Callback<>() {
      @Override
      public void success(DurableLivingDocument document) {
        document.watch(observer);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        observer.failure(ex);
      }
    });
  }

  public void unwatch(Key key, DataObserver observer) {
    load(key, new Callback<DurableLivingDocument>() {
      @Override
      public void success(DurableLivingDocument document) {
        document.unwatch(observer);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        observer.failure(ex);
      }
    });
  }

  private void loadOrCreate(CoreRequestContext context, Key key, Callback<DurableLivingDocument> callback) {
    load(key, new Callback<>() {
      @Override
      public void success(DurableLivingDocument document) {
        callback.success(document);
      }

      @Override
      public void failure(ErrorCodeException exOriginal) {
        if (exOriginal.code == ErrorCodes.UNIVERSAL_LOOKUP_FAILED) {
          livingDocumentFactoryFactory.fetch(key, metrics.factoryFetchConnect.wrap(new Callback<>() {
            @Override
            public void success(LivingDocumentFactory factory) {
              try {
                if (!factory.canInvent(context)) {
                  callback.failure(exOriginal);
                  return;
                }
                create(context, key, "{}", null, metrics.implicitCreate.wrap(new Callback<Void>() {
                  @Override
                  public void success(Void value) {
                    load(key, callback);
                  }

                  @Override
                  public void failure(ErrorCodeException exNew) {
                    if (exNew.code == ErrorCodes.UNIVERSAL_INITIALIZE_FAILURE || exNew.code == ErrorCodes.LIVING_DOCUMENT_TRANSACTION_ALREADY_CONSTRUCTED || exNew.code == ErrorCodes.SERVICE_DOCUMENT_ALREADY_CREATED) {
                      load(key, callback);
                    } else {
                      metrics.failed_invention.run();
                      callback.failure(exNew);
                    }
                  }
                }));
              } catch (ErrorCodeException exNew) {
                callback.failure(exNew);
              }
            }

            @Override
            public void failure(ErrorCodeException ex) {
              callback.failure(ex);
            }
          }));
          return;
        }
        callback.failure(exOriginal);
      }
    });
  }

  public void restore(CoreRequestContext context, Key key, DocumentRestore snapshot, Callback<Void> callback) {
    QueuedRestoreRequest request = new QueuedRestoreRequest(context, key, snapshot, callback);
    load(key, new Callback<DurableLivingDocument>() {
      @Override
      public void success(DurableLivingDocument document) {
        document.restoreWhileInExecutor(request);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  /** internal: do the connect with retry when connect executes create */
  public void connect(CoreRequestContext context, Key key, String viewerState, ConnectionMode mode, Streamback stream) {
    if (!shield.canConnectExisting.get()) {
      stream.failure(new ErrorCodeException(ErrorCodes.SHIELD_REJECT_CONNECT_DOCUMENT));
      return;
    }
    loadOrCreate(context, key, new Callback<>() {
      @Override
      public void success(DurableLivingDocument document) {
        connectDirectMustBeInDocumentBase(context, document, stream, mode, new JsonStreamReader(viewerState));
      }

      @Override
      public void failure(ErrorCodeException exOriginal) {
        stream.failure(exOriginal);
      }
    });
  }

  /** internal: send connection to the document if not joined, then join */
  private void connectDirectMustBeInDocumentBase(CoreRequestContext context, DurableLivingDocument document, Streamback stream, ConnectionMode mode, JsonStreamReader viewerState) {
    PredictiveInventory inventory = document.base.getOrCreateInventory(document.key.space);
    Callback<Integer> onConnected = new Callback<>() {
      @Override
      public void success(Integer dontCare) {
        Perspective perspective = new Perspective() {
          @Override
          public void data(String data) {
            stream.next(data);
            inventory.bandwidth(data.length());
            inventory.message();
          }

          @Override
          public void disconnect() {
            stream.status(Streamback.StreamStatus.Disconnected);
          }
        };
        if (mode.read) {
          document.createPrivateView(context.who, perspective, viewerState, metrics.create_private_view.wrap(new Callback<>() {
            @Override
            public void success(PrivateView view) {
              stream.onSetupComplete(new CoreStream(context, metrics, inventory, document, mode, new StreamHandle(view)));
              stream.status(Streamback.StreamStatus.Connected);
              String viewStateFilter = document.document().__getViewStateFilter();
              if (!"[]".equals(viewStateFilter)) {
                view.deliver("{\"view-state-filter\":" + viewStateFilter + "}");
              }
            }

            @Override
            public void failure(ErrorCodeException ex) {
              stream.failure(ex);
            }
          }));
        } else {
          TrivialPrivateView view = document.document().__createTrivialPrivateView(context.who, perspective);
          stream.onSetupComplete(new CoreStream(context, metrics, inventory, document, mode, new StreamHandle(view)));
          stream.status(Streamback.StreamStatus.Connected);
        }
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
      document.connect(context, onConnected);
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
    long started = System.currentTimeMillis();
    for (int kThread = 0; kThread < bases.length; kThread++) {
      final int kThreadLocal = kThread;
      bases[kThread].executor.execute(new NamedRunnable("deploy-" + kThreadLocal) {
        @Override
        public void execute() throws Exception {
          for (Map.Entry<Key, DurableLivingDocument> entry : bases[kThreadLocal].map.entrySet()) {
            deploy(started, bases[kThreadLocal], entry.getKey(), entry.getValue(), monitor);
          }
        }
      });
    }
  }

  /** internal: deploy a specific document */
  private void deploy(long startedAt, DocumentThreadBase base, Key key, DurableLivingDocument document, DeploymentMonitor monitor) {
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
                document.deployWhileInExecutor(newFactory, metrics.deploy.wrap(new Callback<Integer>() {
                  @Override
                  public void success(Integer value) {
                    monitor.finished((int) (System.currentTimeMillis() - startedAt));
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

  public void authorize(String origin, String ip, Key key, String username, String password, String newPassword, Callback<String> callback) {
    CoreRequestContext context = new CoreRequestContext(NtPrincipal.NO_ONE, origin, ip, key.key);
    load(key, new Callback<>() {
      @Override
      public void success(DurableLivingDocument document) {
        document.registerActivity();
        String agent = document.document().__authorize(context, username, password);
        if (agent != null) {
          if (newPassword != null) {
            CoreRequestContext newContext = new CoreRequestContext(new NtPrincipal(agent, "doc/" + key.space + "/" + key.key), origin, ip, key.key);
            document.setPassword(newContext, newPassword, new Callback<Integer>() {
              @Override
              public void success(Integer value) {
                callback.success(agent);
              }

              @Override
              public void failure(ErrorCodeException ex) {
                callback.failure(ex);
              }
            });
          } else {
            callback.success(agent);
          }
        } else {
          callback.failure(new ErrorCodeException(ErrorCodes.DOCUMENT_AUTHORIIZE_FAILURE));
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  public void authorization(String origin, String ip, Key key, String payload, Callback<AuthResponse> callback) {
    CoreRequestContext context = new CoreRequestContext(NtPrincipal.NO_ONE, origin, ip, key.key);
    load(key, new Callback<>() {
      @Override
      public void success(DurableLivingDocument document) {
        document.registerActivity();
        AuthResponse response = document.document().__authorization(context, payload);
        if (response != null) {
          callback.success(response);
        } else {
          callback.failure(new ErrorCodeException(ErrorCodes.DOCUMENT_AUTHORIIZE_FAILURE));
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  /** execute a web get against the document */
  public void webGet(Key key, WebGet get, Callback<WebResponse> callback) {
    loadOrCreate(get.context.toCoreRequestContext(key), key, new Callback<>() {
      @Override
      public void success(DurableLivingDocument document) {
        PredictiveInventory inventory = document.base.getOrCreateInventory(document.key.space);
        document.registerActivity();
        document.document().__web_get(get, new Callback<WebResponse>() {
          @Override
          public void success(WebResponse response) {
            if (response != null) {
              response.account(inventory);
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

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  /** execute a web options against the document */
  public void webOptions(Key key, WebGet options, Callback<WebResponse> callback) {
    loadOrCreate(options.context.toCoreRequestContext(key), key, new Callback<DurableLivingDocument>() {
      @Override
      public void success(DurableLivingDocument document) {
        PredictiveInventory inventory = document.base.getOrCreateInventory(document.key.space);
        document.registerActivity();
        WebResponse response = document.document().__options(options.context.toCoreRequestContext(key), options);
        if (response != null) {
          response.account(inventory);
          callback.success(response);
        } else {
          callback.failure(new ErrorCodeException(ErrorCodes.DOCUMENT_WEB_OPTIONS_NOT_FOUND));
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  /** execute a web put against the document */
  public void webPut(Key key, WebPut put, Callback<WebResponse> callback) {
    loadOrCreate(put.context.toCoreRequestContext(key), key, new Callback<>() {
      @Override
      public void success(DurableLivingDocument document) {
        PredictiveInventory inventory = document.base.getOrCreateInventory(document.key.space);
        document.webPut(put, new Callback<>() {
          @Override
          public void success(WebResponse response) {
            document.base.executor.execute(new NamedRunnable("web-put-accounting") {
              @Override
              public void execute() throws Exception {
                response.account(inventory);
              }
            });
            callback.success(response);
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
  }

  /** execute a web put against the document */
  public void webDelete(Key key, WebDelete delete, Callback<WebResponse> callback) {
    loadOrCreate(delete.context.toCoreRequestContext(key), key, new Callback<>() {
      @Override
      public void success(DurableLivingDocument document) {
        PredictiveInventory inventory = document.base.getOrCreateInventory(document.key.space);
        document.webDelete(delete, new Callback<>() {
          @Override
          public void success(WebResponse response) {
            document.base.executor.execute(new NamedRunnable("web-delete-accounting") {
              @Override
              public void execute() throws Exception {
                response.account(inventory);
              }
            });
            callback.success(response);
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
  }

  public void startupLoad(Key key) {
    load(key, metrics.document_load_startup.wrap(DONT_CARE_DOCUMENT));
  }

  public void invalidateAll() {
    for (int k = 0; k < bases.length; k++) {
      bases[k].invalidateAll();
    }
  }

  public void directSend(CoreRequestContext context, Key key, String marker, String channel, String message, Callback<Integer> result) {
    loadOrCreate(context, key, new Callback<DurableLivingDocument>() {
      @Override
      public void success(DurableLivingDocument document) {
        document.send(context, null, marker, channel, message, result);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        result.failure(ex);
      }
    });
  }
}
