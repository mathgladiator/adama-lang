/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.sys;

import org.adamalang.ErrorCodes;
import org.adamalang.common.*;
import org.adamalang.runtime.contracts.DocumentMonitor;
import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.runtime.contracts.Queryable;
import org.adamalang.runtime.data.*;
import org.adamalang.runtime.delta.secure.AssetIdEncoder;
import org.adamalang.runtime.exceptions.PerformDocumentDeleteException;
import org.adamalang.runtime.exceptions.PerformDocumentRewindException;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateView;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.remote.RemoteResult;
import org.adamalang.runtime.remote.ServiceRegistry;
import org.adamalang.runtime.sys.web.WebContext;
import org.adamalang.runtime.sys.web.WebDelete;
import org.adamalang.runtime.sys.web.WebPut;
import org.adamalang.runtime.sys.web.WebResponse;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/** A LivingDocument tied to a document id and DataService */
public class DurableLivingDocument implements Queryable {
  public static final int MAGIC_MAXIMUM_DOCUMENT_QUEUE = 256;
  private static final Logger LOG = LoggerFactory.getLogger(DurableLivingDocument.class);
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(LOG);
  private static final Callback<LivingDocumentChange> DONT_CARE_CHANGE = new Callback<>() {
    @Override
    public void success(LivingDocumentChange value) {
    }

    @Override
    public void failure(ErrorCodeException ex) {
    }
  };
  public final DocumentThreadBase base;
  public final Key key;
  private final ArrayDeque<IngestRequest> pending;
  private final AtomicInteger size;
  private LivingDocumentFactory currentFactory;
  private LivingDocument document;
  private Integer requiresInvalidateMilliseconds;
  private boolean inflightPatch;
  private boolean catastrophicFailureOccurred;
  private boolean loadShedOccurred;
  private long lastExpire;
  private int outstandingExecutionsWhichRequireDrain;
  private boolean inflightCompact;
  private int trackingSeq;
  private long lastActivityMS;

  private DurableLivingDocument(final Key key, final LivingDocument document, final LivingDocumentFactory currentFactory, final DocumentThreadBase base) {
    this.key = key;
    this.document = document;
    this.currentFactory = currentFactory;
    this.base = base;
    this.requiresInvalidateMilliseconds = document.__computeRequiresInvalidateMilliseconds();
    this.pending = new ArrayDeque<>(8);
    this.inflightPatch = false;
    this.inflightCompact = false;
    this.catastrophicFailureOccurred = false;
    this.lastExpire = 0;
    this.outstandingExecutionsWhichRequireDrain = 0;
    this.size = new AtomicInteger(0);
    this.trackingSeq = document.__seq.get();
    this.lastActivityMS = base.time.nowMilliseconds();
  }

  public static void fresh(final Key key, final LivingDocumentFactory factory, final CoreRequestContext context, final String arg, final String entropy, final DocumentMonitor monitor, final DocumentThreadBase base, final Callback<DurableLivingDocument> callback) {
    try {
      LivingDocument livingDocument = factory.create(monitor);
      livingDocument.__lateBind(key.space, key.key, Deliverer.FAILURE, ServiceRegistry.NOT_READY);
      DurableLivingDocument document = new DurableLivingDocument(key, livingDocument, factory, base);
      document.construct(context, arg, entropy, Callback.transform(callback, ErrorCodes.DURABLE_LIVING_DOCUMENT_STAGE_FRESH_PERSIST, (seq) -> document));
    } catch (Throwable ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.DURABLE_LIVING_DOCUMENT_STAGE_FRESH_DRIVE, ex, EXLOGGER));
    }
  }

  private void construct(final CoreRequestContext context, final String arg, final String entropy, Callback<Integer> callback) {
    try {
      final var writer = forge("construct", context.who);
      writer.writeObjectFieldIntro("arg");
      writer.injectJson(arg);
      if (entropy != null) {
        writer.writeObjectFieldIntro("entropy");
        writer.writeFastString(entropy);
      }
      writer.writeObjectFieldIntro("key");
      writer.writeString(context.key);
      writer.writeObjectFieldIntro("origin");
      writer.writeString(context.origin);
      writer.writeObjectFieldIntro("ip");
      writer.writeString(context.ip);
      writer.endObject();
      final var init = document.__transact(writer.toString(), currentFactory);
      final var invalidate = document.__transact(forgeInvalidate(), currentFactory);
      final var setup = RemoteDocumentUpdate.compact(new RemoteDocumentUpdate[]{init.update, invalidate.update})[0];
      size.set(1);
      base.service.initialize(key, setup, Callback.handoff(callback, ErrorCodes.DURABLE_LIVING_DOCUMENT_STAGE_CONSTRUCT_PERSIST, () -> {
        init.complete();
        invalidate.complete();
        callback.success(setup.seqEnd);
      }));
    } catch (Throwable ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.DURABLE_LIVING_DOCUMENT_STAGE_CONSTRUCT_DRIVE, ex, EXLOGGER));
    }
  }

  public JsonStreamWriter forge(final String command, final NtPrincipal who) {
    return forge(command, who, true);
  }

  private String forgeInvalidate() {
    final var request = forge("invalidate", null);
    request.endObject();
    return request.toString();
  }

  public JsonStreamWriter forge(final String command, final NtPrincipal who, boolean activity) {
    if (activity) {
      this.lastActivityMS = base.time.nowMilliseconds();
    }
    final var writer = new JsonStreamWriter();
    writer.beginObject();
    writer.writeObjectFieldIntro("command");
    writer.writeFastString(command);
    writer.writeObjectFieldIntro("timestamp");
    writer.writeLong(base.time.nowMilliseconds());
    if (who != null) {
      writer.writeObjectFieldIntro("who");
      writer.writeNtPrincipal(who);
    }
    return writer;
  }

  public static void load(final Key key, final LivingDocumentFactory factory, final DocumentMonitor monitor, final DocumentThreadBase base, final Callback<DurableLivingDocument> callback) {
    try {
      if (!base.shield.canConnectNew.get()) {
        callback.failure(new ErrorCodeException(ErrorCodes.SHIELD_REJECT_NEW_DOCUMENT));
        return;
      }

      LivingDocument doc = factory.create(monitor);
      doc.__lateBind(key.space, key.key, factory.deliverer, factory.registry);
      base.service.get(key, new Callback<>() {
        @Override
        public void success(LocalDocumentChange documentValue) {
          JsonStreamReader reader = new JsonStreamReader(documentValue.patch);
          reader.ingestDedupe(doc.__get_intern_strings());
          doc.__insert(reader);
          JsonStreamWriter writer = new JsonStreamWriter();
          doc.__dump(writer);
          DurableLivingDocument document = new DurableLivingDocument(key, doc, factory, base);
          document.size.set(documentValue.reads);
          document.load(new Callback<>() {
            @Override
            public void success(LivingDocumentChange change) {
              callback.success(document);
              document.queueCompact();
            }

            @Override
            public void failure(ErrorCodeException ex) {
              if (ex.code == ErrorCodes.LIVING_DOCUMENT_TRANSACTION_NO_CHANGE) {
                callback.success(document);
              } else {
                callback.failure(ex);
              }
            }
          });
        }

        @Override
        public void failure(ErrorCodeException ex) {
          callback.failure(ex);
        }
      });
    } catch (Throwable ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.DURABLE_LIVING_DOCUMENT_STAGE_LOAD_DRIVE, ex, EXLOGGER));
    }
  }

  private static Callback<LivingDocumentChange> JUST_SEQ(Callback<Integer> callback) {
    return new Callback<LivingDocumentChange>() {
      @Override
      public void success(LivingDocumentChange value) {
        callback.success(value.update.seqEnd);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    };
  }

  public JsonStreamWriter forgeWithContext(final String command, final CoreRequestContext context) {
    this.lastActivityMS = base.time.nowMilliseconds();
    final var writer = new JsonStreamWriter();
    writer.beginObject();
    writer.writeObjectFieldIntro("command");
    writer.writeFastString(command);
    writer.writeObjectFieldIntro("timestamp");
    writer.writeLong(base.time.nowMilliseconds());
    if (context != null) {
      if (context.who != null) {
        writer.writeObjectFieldIntro("who");
        writer.writeNtPrincipal(context.who);
      }
      writer.writeObjectFieldIntro("key");
      writer.writeString(context.key);
      writer.writeObjectFieldIntro("origin");
      writer.writeString(context.origin);
      writer.writeObjectFieldIntro("ip");
      writer.writeString(context.ip);
    }
    return writer;
  }

  @Override
  public void query(TreeMap<String, String> query, Callback<String> callback) {
    JsonStreamWriter writer = new JsonStreamWriter();
    writer.beginObject();
    writer.writeObjectFieldIntro("space");
    writer.writeString(key.space);
    writer.writeObjectFieldIntro("key");
    writer.writeString(key.key);
    writer.writeObjectFieldIntro("size");
    writer.writeInteger(size.get());
    writer.endObject();
    callback.success(writer.toString());
  }

  private void testQueueSizeAndThenMaybeCompact() {
    if (size.get() * 2 > currentFactory.maximum_history * 3) {
      queueCompact();
    }
  }

  private void queueCompact() {
    base.executor.execute(new NamedRunnable("document-compacting") {
      @Override
      public void execute() throws Exception {
        if (inflightCompact) {
          base.metrics.document_compacting_skipped.run();
          return;
        }
        inflightCompact = true;
        base.metrics.document_compacting.run();
        JsonStreamWriter writer = new JsonStreamWriter();
        writer.enableAssetTracking();
        document.__dump(writer);
        int toCompactNow = Math.max(0, size.get() - currentFactory.maximum_history);
        base.service.snapshot(key, new DocumentSnapshot(document.__seq.get(), writer.toString(), currentFactory.maximum_history, writer.getAssetBytes()), new Callback<>() {
          @Override
          public void success(Integer value) {
            base.executor.execute(new NamedRunnable("compact-complete") {
              @Override
              public void execute() throws Exception {
                inflightCompact = false;
                size.getAndAdd(-toCompactNow);
                testQueueSizeAndThenMaybeCompact();
              }
            });
          }

          @Override
          public void failure(ErrorCodeException ex) {
            base.executor.execute(new NamedRunnable("compact-failed") {
              @Override
              public void execute() throws Exception {
                inflightCompact = false;
              }
            });
          }
        });
      }
    });
  }

  public LivingDocument document() {
    return document;
  }

  public Integer getAndCleanRequiresInvalidateMilliseconds() {
    Integer result = requiresInvalidateMilliseconds;
    requiresInvalidateMilliseconds = null;
    return result;
  }

  public void deploy(LivingDocumentFactory factory, Callback<Integer> callback) throws ErrorCodeException {
    LivingDocument newDocument = factory.create(document.__monitor);
    newDocument.__lateBind(key.space, key.key, factory.deliverer, factory.registry);
    JsonStreamWriter writer = new JsonStreamWriter();
    document.__dump(writer);
    newDocument.__insert(new JsonStreamReader(writer.toString()));
    document.__usurp(newDocument);
    document = newDocument;
    currentFactory = factory;
    invalidate(callback);
  }

  public void triggerExpire() {
    long timeSinceLastExpire = base.time.nowMilliseconds() - lastExpire;
    if (timeSinceLastExpire > 60000) {
      lastExpire = base.time.nowMilliseconds();
      expire(10 * 60 * 1000);
    }
  }

  private void finishSuccessDataServicePatchWhileInExecutor(boolean checkInvalidate) {
    this.inflightPatch = false;
    if (pending.size() == 0) {
      outstandingExecutionsWhichRequireDrain = 0;
      if (requiresInvalidateMilliseconds != null && checkInvalidate) {
        base.executor.schedule(new NamedRunnable("finish-success-patch") {
          @Override
          public void execute() throws Exception {
            invalidate(Callback.DONT_CARE_INTEGER);
          }
        }, requiresInvalidateMilliseconds);
      }
    } else {
      IngestRequest[] remaining = new IngestRequest[pending.size()];
      for (int at = 0; at < remaining.length; at++) {
        remaining[at] = pending.removeFirst();
      }
      executeNow(remaining);
    }
  }

  public void shedWhileInExecutor() {
    base.metrics.document_load_shed.run();
    loadShedOccurred = true;
    issueCloseWhileInExecutor(ErrorCodes.DOCUMENT_SHEDDING_LOAD);
  }

  private void issueCloseWhileInExecutor(int errorCode) {
    document.__nukeViews();
    while (pending.size() > 0) {
      pending.removeFirst().callback.failure(new ErrorCodeException(errorCode));
    }
    cleanupWhileInExecutor();
  }

  public void cleanupWhileInExecutor() {
    if (base.map.remove(key) != null) {
      base.metrics.inflight_documents.down();
      if (getCurrentFactory().delete_on_close) {
        base.service.delete(key, Callback.DONT_CARE_VOID);
      } else {
        base.service.close(key, Callback.DONT_CARE_VOID);
      }
    }
  }

  public LivingDocumentFactory getCurrentFactory() {
    return currentFactory;
  }

  private void catastrophicFailureWhileInExecutor(int code) {
    inflightPatch = false;
    base.metrics.document_catastrophic_failure.run();
    catastrophicFailureOccurred = true;
    issueCloseWhileInExecutor(ErrorCodes.CATASTROPHIC_DOCUMENT_FAILURE_EXCEPTION);
  }

  private IngestRequest isolate(IngestRequest requestToFocus, IngestRequest[] all) {
    for (IngestRequest otherRequest : all) {
      if (otherRequest != requestToFocus) {
        otherRequest.callback.failure(new ErrorCodeException(ErrorCodes.DOCUMENT_QUEUE_CONFLICT_OPERATIONS));
      }
    }
    return requestToFocus;
  }

  private void executeNow(IngestRequest[] requests) {
    inflightPatch = true;
    IngestRequest lastRequest = null;
    ArrayList<LivingDocumentChange> changes = new ArrayList<>();
    Runnable revert = () -> {
      for (int k = changes.size() - 1; k >= 0; k--) {
        document.__insert(new JsonStreamReader(changes.get(k).update.undo));
      }
    };
    try {
      LivingDocumentChange last = null;

      Consumer<ErrorCodeException> sad = (ErrorCodeException ex) -> {
        for (final IngestRequest request : requests) {
          if (request.change != null) {
            request.callback.failure(ex);
          }
        }
      };

      boolean requestsCleanUp = false;
      for (final IngestRequest request : requests) {
        try {
          if (request.cleanupTest) {
            requestsCleanUp = true;
          }
          lastRequest = request;
          request.change = document.__transact(request.request, currentFactory);
          if (request.change != null) {
            changes.add(request.change);
            last = request.change;
          } else {
            request.callback.failure(new ErrorCodeException(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_NO_CHANGE));
          }
        } catch (PerformDocumentDeleteException | PerformDocumentRewindException valid) {
          throw valid;
        } catch (ErrorCodeException ex) {
          request.callback.failure(ex);
        } catch (Exception badThingHappened) {
          catastrophicFailureWhileInExecutor(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_UNKNOWN_EXCEPTION);
          request.callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.LIVING_DOCUMENT_TRANSACTION_UNKNOWN_EXCEPTION, badThingHappened, EXLOGGER));
          LOG.error("catastrophic-message:" + request.request);
        }
      }
      if (last == null) {
        finishSuccessDataServicePatchWhileInExecutor(false);
        return;
      }
      final boolean shouldCleanUp = requestsCleanUp;
      Consumer<ErrorCodeException> triggerFailure = (ex2) -> {
        base.executor.execute(new NamedRunnable("catastrophic-failure") {
          @Override
          public void execute() throws Exception {
            sad.accept(ex2);
            catastrophicFailureWhileInExecutor(ex2.code);
          }
        });
      };
      while (last.update.requiresFutureInvalidation && last.update.whenToInvalidateMilliseconds == 0) {
        // inject an invalidation
        try {
          last = document.__transact(forgeInvalidate(), currentFactory);
          changes.add(last);
        } catch (ErrorCodeException ex) {
          triggerFailure.accept(ex);
          return;
        }
      }
      requiresInvalidateMilliseconds = last.update.requiresFutureInvalidation ? last.update.whenToInvalidateMilliseconds : null;
      RemoteDocumentUpdate[] patches = new RemoteDocumentUpdate[changes.size()];
      int at = 0;
      for (LivingDocumentChange change : changes) {
        patches[at] = change.update;
        at++;
        if (trackingSeq + 1 != change.update.seqBegin) {
          base.metrics.internal_seq_drift.run();
        }
        trackingSeq = change.update.seqEnd;
      }
      size.addAndGet(patches.length);
      RemoteDocumentUpdate[] compactPatches = RemoteDocumentUpdate.compact(patches);
      base.service.patch(key, compactPatches, base.metrics.document_execute_patch.wrap(new Callback<>() {
        @Override
        public void success(Void value) {
          base.executor.execute(new NamedRunnable("execute-now-patch-callback") {
            @Override
            public void execute() throws Exception {
              try {
                for (final IngestRequest request : requests) {
                  if (request.change != null) {
                    request.callback.success(request.change);
                  }
                }
                for (final LivingDocumentChange change : changes) {
                  change.complete();
                }
                if (shouldCleanUp && document.__canRemoveFromMemory()) {
                  scheduleCleanup();
                }
                testQueueSizeAndThenMaybeCompact();
              } finally {
                finishSuccessDataServicePatchWhileInExecutor(true);
              }
            }
          });
        }

        @Override
        public void failure(ErrorCodeException ex) {
          base.executor.execute(new NamedRunnable("failed-patch") {
            @Override
            public void execute() throws Exception {
              if (ex.code == ErrorCodes.UNIVERSAL_PATCH_FAILURE_HEAD_SEQ_OFF) {
                for (IngestRequest request : requests) {
                  if (!request.tryAgain()) {
                    triggerFailure.accept(ex);
                    return;
                  }
                }
                revert.run();
                base.service.compute(key, ComputeMethod.HeadPatch, document.__seq.get(), base.metrics.catch_up_patch.wrap(new Callback<>() {
                  @Override
                  public void success(LocalDocumentChange value) {
                    base.executor.execute(new NamedRunnable("catch-up-computed") {
                      @Override
                      public void execute() throws Exception {
                        document.__insert(new JsonStreamReader(value.patch));
                        IngestRequest[] requestsAfterCatchUp = new IngestRequest[requests.length + 1];
                        requestsAfterCatchUp[0] = new IngestRequest(NtPrincipal.NO_ONE, forgeInvalidate(), DONT_CARE_CHANGE, false);
                        System.arraycopy(requests, 0, requestsAfterCatchUp, 1, requests.length);
                        executeNow(requestsAfterCatchUp);
                      }
                    });
                  }

                  @Override
                  public void failure(ErrorCodeException ex) {
                    triggerFailure.accept(ex);
                  }
                }));
              } else {
                triggerFailure.accept(ex);
              }
            }
          });
        }
      }));
    } catch (PerformDocumentRewindException rewind) {
      IngestRequest requestToActOn = isolate(lastRequest, requests);
      base.service.compute(key, ComputeMethod.Rewind, rewind.seq, new Callback<LocalDocumentChange>() {
        @Override
        public void success(LocalDocumentChange value) {
          base.executor.execute(new NamedRunnable("document-rewind-success") {
            @Override
            public void execute() throws Exception {
              revert.run();
              final var writer = forge("apply", requestToActOn.who);
              writer.writeObjectFieldIntro("patch");
              writer.injectJson(value.patch);
              writer.endObject();
              executeNow(new IngestRequest[]{new IngestRequest(requestToActOn.who, writer.toString(), requestToActOn.callback, false)});
            }
          });
        }

        @Override
        public void failure(ErrorCodeException ex) {
          base.executor.execute(new NamedRunnable("document-rewind-failure") {
            @Override
            public void execute() throws Exception {
              for (IngestRequest request : requests) {
                request.callback.failure(ex);
              }
              catastrophicFailureWhileInExecutor(ex.code);
            }
          });
        }
      });
    } catch (PerformDocumentDeleteException destroy) {
      base.service.delete(key, new Callback<>() {
        @Override
        public void success(Void value) {
          base.executor.execute(new NamedRunnable("document-destroy-success") {
            @Override
            public void execute() throws Exception {
              for (IngestRequest request : requests) {
                request.callback.failure(new ErrorCodeException(ErrorCodes.DOCUMENT_SELF_DESTRUCT_SUCCESSFUL));
              }
              catastrophicFailureWhileInExecutor(ErrorCodes.DOCUMENT_SELF_DESTRUCT_SUCCESSFUL);
            }
          });
        }

        @Override
        public void failure(ErrorCodeException ex) {
          base.executor.execute(new NamedRunnable("document-destroy-failure") {
            @Override
            public void execute() throws Exception {
              for (IngestRequest request : requests) {
                request.callback.failure(ex);
              }
              catastrophicFailureWhileInExecutor(ex.code);
            }
          });
        }
      });
    }
  }

  private void ingest(NtPrincipal who, String requestJson, Callback<LivingDocumentChange> callback, boolean cleanupTest, boolean forceIntoQueue) {
    IngestRequest request = new IngestRequest(who, requestJson, callback, cleanupTest);
    if (catastrophicFailureOccurred) {
      request.callback.failure(new ErrorCodeException(ErrorCodes.CATASTROPHIC_DOCUMENT_FAILURE_EXCEPTION));
      return;
    }
    if (loadShedOccurred) {
      request.callback.failure(new ErrorCodeException(ErrorCodes.DOCUMENT_SHEDDING_LOAD));
      return;
    }
    if (inflightPatch) {
      if (outstandingExecutionsWhichRequireDrain >= MAGIC_MAXIMUM_DOCUMENT_QUEUE && !forceIntoQueue) {
        base.metrics.document_queue_running_behind.run();
        callback.failure(new ErrorCodeException(ErrorCodes.DOCUMENT_QUEUE_BUSY_WAY_BEHIND));
        return;
      }
      outstandingExecutionsWhichRequireDrain++;
      if (pending.size() >= 128 && !forceIntoQueue) {
        base.metrics.document_queue_full.run();
        callback.failure(new ErrorCodeException(ErrorCodes.DOCUMENT_QUEUE_BUSY_TOO_MANY_PENDING_ITEMS));
      } else {
        pending.add(request);
      }
    } else {
      executeNow(new IngestRequest[]{request});
    }
  }

  public void invalidate(Callback<Integer> callback) {
    ingest(NtPrincipal.NO_ONE, forgeInvalidate(), JUST_SEQ(base.metrics.document_invalidate.wrap(callback)), false, true);
  }

  public int getCodeCost() {
    return document.__getCodeCost();
  }

  public void zeroOutCodeCost() {
    document.__zeroOutCodeCost();
  }

  public int getConnectionsCount() {
    return document.__getConnectionsCount();
  }

  public long getMemoryBytes() {
    return document.__memory();
  }

  public void expire(long limit) {
    final var request = forge("expire", null, false);
    request.writeObjectFieldIntro("limit");
    request.writeLong(limit);
    request.endObject();
    ingest(NtPrincipal.NO_ONE, request.toString(), DONT_CARE_CHANGE, true, false);
  }

  public void load(Callback<LivingDocumentChange> callback) {
    final var request = forge("load", null, false);
    request.endObject();
    ingest(NtPrincipal.NO_ONE, request.toString(), callback, true, false);
  }

  public void registerActivity() {
    this.lastActivityMS = base.time.nowMilliseconds();
  }

  public void connect(final CoreRequestContext context, Callback<Integer> callback) {
    final var request = forgeWithContext("connect", context);
    request.endObject();
    ingest(context.who, request.toString(), JUST_SEQ(base.metrics.document_connect.wrap(callback)), false, false);
  }

  public boolean isConnected(final NtPrincipal who) {
    return document.__isConnected(who);
  }

  public void createPrivateView(final NtPrincipal who, final Perspective perspective, JsonStreamReader viewerState, AssetIdEncoder encoder, Callback<PrivateView> callback) {
    PrivateView result = document.__createView(who, perspective, encoder);
    result.ingest(viewerState);
    invalidate(Callback.transform(callback, ErrorCodes.DURABLE_LIVING_DOCUMENT_STAGE_ATTACH_PRIVATE_VIEW, (seq) -> result));
  }

  public int garbageCollectPrivateViewsFor(final NtPrincipal who) {
    return document.__garbageCollectViews(who);
  }

  public void scheduleCleanup() {
    base.executor.schedule(new NamedRunnable("document-cleanup") {
      @Override
      public void execute() throws Exception {
        if (document.__canRemoveFromMemory()) {
          cleanupWhileInExecutor();
        }
      }
    }, base.getMillisecondsForCleanupCheck());
  }

  public boolean testInactive() {
    long timeSinceLastActivity = base.time.nowMilliseconds() - lastActivityMS;
    return timeSinceLastActivity > base.getMillisecondsInactivityBeforeCleanup() && document.__canRemoveFromMemory();
  }

  public void disconnect(final CoreRequestContext context, Callback<Integer> callback) {
    final var request = forgeWithContext("disconnect", context);
    request.endObject();
    ingest(context.who, request.toString(), JUST_SEQ(base.metrics.document_disconnect.wrap(callback)), true, false);
  }

  public void send(final CoreRequestContext context, final String marker, final String channel, final String message, Callback<Integer> callback) {
    final var writer = forgeWithContext("send", context);
    writer.writeObjectFieldIntro("channel");
    writer.writeFastString(channel);
    if (marker != null) {
      writer.writeObjectFieldIntro("marker");
      writer.writeString(marker);
    }
    writer.writeObjectFieldIntro("message");
    writer.injectJson(message);
    writer.endObject();
    ingest(context.who, writer.toString(), JUST_SEQ(base.metrics.document_send.wrap(callback)), false, false);
  }

  public void delete(CoreRequestContext context, Callback<Void> callbackRaw) {
    Callback<Void> callback = base.metrics.document_send.wrap(callbackRaw);
    final var writer = forgeWithContext("delete", context);
    writer.endObject();
    ingest(context.who, writer.toString(), new Callback<>() {
      @Override
      public void success(LivingDocumentChange value) {
        callback.failure(new ErrorCodeException(ErrorCodes.IMPOSSIBLE));
      }

      @Override
      public void failure(ErrorCodeException ex) {
        if (ex.code == 134195) {
          callback.success(null);
        } else {
          callback.failure(ex);
        }
      }
    }, false, false);
  }

  public void apply(NtPrincipal who, String patch, Callback<Integer> callback) {
    final var writer = forge("apply", who);
    writer.writeObjectFieldIntro("patch");
    writer.injectJson(patch);
    writer.endObject();
    ingest(who, writer.toString(), JUST_SEQ(base.metrics.document_apply.wrap(callback)), false, false);
  }

  public boolean canAttach(CoreRequestContext context) {
    // TODO: check policy first
    return document.__onCanAssetAttached(context);
  }

  public void attach(CoreRequestContext context, NtAsset asset, Callback<Integer> callback) {
    final var writer = forgeWithContext("attach", context);
    writer.writeObjectFieldIntro("asset");
    writer.writeNtAsset(asset);
    writer.endObject();
    ingest(context.who, writer.toString(), JUST_SEQ(base.metrics.document_attach.wrap(callback)), false, false);
  }

  public void deliver(NtPrincipal who, int deliveryId, RemoteResult result, Callback<Integer> callback) {
    var writer = forge("deliver", who);
    writer.writeObjectFieldIntro("delivery_id");
    writer.writeInteger(deliveryId);
    writer.writeObjectFieldIntro("result");
    result.write(writer);
    writer.endObject();
    ingest(who, writer.toString(), JUST_SEQ(base.metrics.document_attach.wrap(callback)), false, false);
  }

  private JsonStreamWriter forge_web(String command, WebContext context) {
    final var writer = forge(command, context.who);
    writer.writeObjectFieldIntro("origin");
    writer.writeString(context.origin);
    writer.writeObjectFieldIntro("ip");
    writer.writeString(context.ip);
    return writer;
  }

  public void webPut(WebPut put, Callback<WebResponse> callback) {
    final var writer = forge_web("web_put", put.context);
    put.write(writer);
    writer.endObject();
    ingest(put.context.who, writer.toString(), base.metrics.document_web_put.wrap(new Callback<LivingDocumentChange>() {
      @Override
      public void success(LivingDocumentChange value) {
        if (value.response != null) {
          callback.success((WebResponse) value.response);
        } else {
          callback.failure(new ErrorCodeException(ErrorCodes.DOCUMENT_WEB_PUT_NOT_FOUND));
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    }), false, false);
  }

  public void webDelete(WebDelete delete, Callback<WebResponse> callback) {
    final var writer = forge_web("web_delete", delete.context);
    delete.write(writer);
    writer.endObject();
    ingest(delete.context.who, writer.toString(), base.metrics.document_web_delete.wrap(new Callback<LivingDocumentChange>() {
      @Override
      public void success(LivingDocumentChange value) {
        if (value.response != null) {
          callback.success((WebResponse) value.response);
        } else {
          callback.failure(new ErrorCodeException(ErrorCodes.DOCUMENT_WEB_DELETE_NOT_FOUND));
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    }), false, false);
  }

  public String json() {
    final var writer = new JsonStreamWriter();
    document.__dump(writer);
    return writer.toString();
  }

  public void afterLoad() {
    for (NtPrincipal client : document.__reconcileClientsToForceDisconnect()) {
      disconnect(new CoreRequestContext(client, "adama", "127.0.0.1", key.key), Callback.DONT_CARE_INTEGER);
    }
    if (document.__state.has() && !document.__blocked.get()) {
      invalidate(Callback.DONT_CARE_INTEGER);
    }
    testQueueSizeAndThenMaybeCompact();
  }

  private static class IngestRequest {
    public final boolean cleanupTest;
    private final NtPrincipal who;
    private final String request;
    private final Callback<LivingDocumentChange> callback;
    private int attempts;
    private LivingDocumentChange change;

    private IngestRequest(NtPrincipal who, String request, Callback<LivingDocumentChange> callback, boolean cleanup) {
      this.who = who;
      this.request = request;
      this.callback = callback;
      this.attempts = 0;
      this.cleanupTest = cleanup;
      this.change = null;
    }

    public boolean tryAgain() {
      return attempts++ < 5;
    }
  }
}
