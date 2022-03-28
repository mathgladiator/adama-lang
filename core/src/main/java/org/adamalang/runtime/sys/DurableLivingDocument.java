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
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.NamedRunnable;
import org.adamalang.runtime.contracts.DocumentMonitor;
import org.adamalang.runtime.contracts.Perspective;
import org.adamalang.runtime.data.ComputeMethod;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.LocalDocumentChange;
import org.adamalang.runtime.data.RemoteDocumentUpdate;
import org.adamalang.runtime.delta.secure.AssetIdEncoder;
import org.adamalang.runtime.exceptions.PerformDocumentDeleteException;
import org.adamalang.runtime.exceptions.PerformDocumentRewindException;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateView;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/** A LivingDocument tied to a document id and DataService */
public class DurableLivingDocument {
  public static final int MAGIC_MAXIMUM_DOCUMENT_QUEUE = 256;
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(DurableLivingDocument.class);
  public final DocumentThreadBase base;
  public final Key key;
  private final ArrayDeque<IngestRequest> pending;
  private LivingDocumentFactory currentFactory;
  private LivingDocument document;
  private Integer requiresInvalidateMilliseconds;
  private boolean inflightPatch;
  private boolean catastrophicFailureOccurred;
  private long lastExpire;
  private int outstandingExecutionsWhichRequireDrain;
  private boolean inflightCompact;
  private final AtomicInteger size;
  private int trackingSeq;

  private DurableLivingDocument(final Key key, final LivingDocument document, final LivingDocumentFactory currentFactory, final DocumentThreadBase base) {
    this.key = key;
    this.document = document;
    this.currentFactory = currentFactory;
    this.base = base;
    this.requiresInvalidateMilliseconds = document.__computeRequiresInvalidateMilliseconds();
    this.pending = new ArrayDeque<>(8);
    this.inflightPatch = false;
    this.catastrophicFailureOccurred = false;
    this.lastExpire = 0;
    this.outstandingExecutionsWhichRequireDrain = 0;
    this.size = new AtomicInteger(0);
    this.trackingSeq = document.__seq.get();
  }

  public static void fresh(final Key key, final LivingDocumentFactory factory, final NtClient who, final String arg, final String entropy, final DocumentMonitor monitor, final DocumentThreadBase base, final Callback<DurableLivingDocument> callback) {
    try {
      LivingDocument livingDocument = factory.create(monitor);
      livingDocument.__setKey(key.key);
      DurableLivingDocument document = new DurableLivingDocument(key, livingDocument, factory, base);
      document.construct(who, arg, entropy, Callback.transform(callback, ErrorCodes.DURABLE_LIVING_DOCUMENT_STAGE_FRESH_PERSIST, (seq) -> document));
    } catch (Throwable ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.DURABLE_LIVING_DOCUMENT_STAGE_FRESH_DRIVE, ex, EXLOGGER));
    }
  }

  private void construct(final NtClient who, final String arg, final String entropy, Callback<Integer> callback) {
    try {
      final var writer = forge("construct", who);
      writer.writeObjectFieldIntro("arg");
      writer.injectJson(arg);
      if (entropy != null) {
        writer.writeObjectFieldIntro("entropy");
        writer.writeFastString(entropy);
      }
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

  public JsonStreamWriter forge(final String command, final NtClient who) {
    final var writer = new JsonStreamWriter();
    writer.beginObject();
    writer.writeObjectFieldIntro("command");
    writer.writeFastString(command);
    writer.writeObjectFieldIntro("timestamp");
    writer.writeLong(base.time.nowMilliseconds());
    if (who != null) {
      writer.writeObjectFieldIntro("who");
      writer.writeNtClient(who);
    }
    return writer;
  }

  public JsonStreamWriter forgeWithContext(final String command, final CoreRequestContext context) {
    final var writer = new JsonStreamWriter();
    writer.beginObject();
    writer.writeObjectFieldIntro("command");
    writer.writeFastString(command);
    writer.writeObjectFieldIntro("timestamp");
    writer.writeLong(base.time.nowMilliseconds());
    if (context != null) {
      if (context.who != null) {
        writer.writeObjectFieldIntro("who");
        writer.writeNtClient(context.who);
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

  private String forgeInvalidate() {
    final var request = forge("invalidate", null);
    request.endObject();
    return request.toString();
  }

  public static void load(final Key key, final LivingDocumentFactory factory, final DocumentMonitor monitor, final DocumentThreadBase base, final Callback<DurableLivingDocument> callback) {
    try {
      LivingDocument doc = factory.create(monitor);
      doc.__setKey(key.key);
      base.service.get(key, Callback.transform(callback, ErrorCodes.DURABLE_LIVING_DOCUMENT_STAGE_LOAD_READ, (data) -> {
        JsonStreamReader reader = new JsonStreamReader(data.patch);
        reader.ingestDedupe(doc.__get_intern_strings());
        doc.__insert(reader);
        JsonStreamWriter writer = new JsonStreamWriter();
        doc.__dump(writer);
        DurableLivingDocument document = new DurableLivingDocument(key, doc, factory, base);
        document.size.set(data.reads);
        return document;
      }));
    } catch (Throwable ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.DURABLE_LIVING_DOCUMENT_STAGE_LOAD_DRIVE, ex, EXLOGGER));
    }
  }

  private void queueCompact() {
    base.executor.execute(new NamedRunnable("document-compacting") {
      @Override
      public void execute() throws Exception {
        if (inflightCompact) {
          return;
        }
        inflightCompact = true;
        base.metrics.document_compacting.run();
        JsonStreamWriter writer = new JsonStreamWriter();
        document.__dump(writer);
        int toCompactNow = size.get();
        base.service.compactAndSnapshot(key, document.__seq.get(), writer.toString(), currentFactory.maximum_history, new Callback<>() {
          @Override
          public void success(Integer value) {
            base.executor.execute(new NamedRunnable("compact-complete") {
              @Override
              public void execute() throws Exception {
                inflightCompact = false;
                size.getAndAdd(-toCompactNow);
                if (size.get() >= currentFactory.maximum_history) {
                  queueCompact();
                }
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

  public LivingDocumentFactory getCurrentFactory() {
    return currentFactory;
  }

  public void deploy(LivingDocumentFactory factory, Callback<Integer> callback) throws ErrorCodeException {
    LivingDocument newDocument = factory.create(document.__monitor);
    newDocument.__setKey(key.key);
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
      expire(10 * 60 * 1000, Callback.DONT_CARE_INTEGER);
    }
  }

  private void finishSuccessDataServicePatchWhileInExecutor() {
    this.inflightPatch = false;
    if (pending.size() == 0) {
      outstandingExecutionsWhichRequireDrain = 0;
      if (requiresInvalidateMilliseconds != null) {
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

  private void catastrophicFailureWhileInExecutor() {
    base.metrics.document_catastrophic_failure.run();
    document.__nukeViews();
    base.map.remove(key);
    base.metrics.inflight_documents.down();
    catastrophicFailureOccurred = true;
    while (pending.size() > 0) {
      pending.removeFirst().callback.failure(new ErrorCodeException(ErrorCodes.CATASTROPHIC_DOCUMENT_FAILURE_EXCEPTION));
    }
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
      ArrayList<Callback<Integer>> callbacks = new ArrayList<>();
      boolean requestsCleanUp = false;
      for (IngestRequest request : requests) {
        try {
          if (request.cleanupTest) {
            requestsCleanUp = true;
          }
          lastRequest = request;
          last = document.__transact(request.request, currentFactory);
          changes.add(last);
          callbacks.add(request.callback);
        } catch (ErrorCodeException ex) {
          request.callback.failure(ex);
        }
      }
      if (last == null) {
        inflightPatch = false;
        return;
      }
      final boolean shouldCleanUp = requestsCleanUp;
      Consumer<ErrorCodeException> triggerFailure = (ex2) -> {
        base.executor.execute(new NamedRunnable("catastrophic-failure") {
          @Override
          public void execute() throws Exception {
            inflightPatch = false;
            for (Callback<Integer> callback : callbacks) {
              callback.failure(ex2);
            }
            catastrophicFailureWhileInExecutor();
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
      int seqToUse = last.update.seqEnd;
      size.addAndGet(patches.length);
      RemoteDocumentUpdate[] compactPatches = RemoteDocumentUpdate.compact(patches);
      base.service.patch(key, compactPatches, base.metrics.document_execute_patch.wrap(new Callback<>() {
        @Override
        public void success(Void value) {
          base.executor.execute(new NamedRunnable("execute-now-patch-callback") {
            @Override
            public void execute() throws Exception {
              for (Callback<Integer> callback : callbacks) {
                callback.success(seqToUse);
              }
              for (LivingDocumentChange change : changes) {
                change.complete();
              }
              if (shouldCleanUp && document.__canRemoveFromMemory()) {
                scheduleCleanup();
              }
              if (size.get() > currentFactory.maximum_history) {
                queueCompact();
              }
              finishSuccessDataServicePatchWhileInExecutor();
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
                        requestsAfterCatchUp[0] = new IngestRequest(NtClient.NO_ONE, forgeInvalidate(), Callback.DONT_CARE_INTEGER, false);
                        for (int j = 0; j < requests.length; j++) {
                          requestsAfterCatchUp[j + 1] = requests[j];
                        }
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
              catastrophicFailureWhileInExecutor();
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
              catastrophicFailureWhileInExecutor();
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
              catastrophicFailureWhileInExecutor();
            }
          });
        }
      });
    }
  }

  private void ingest(NtClient who, String requestJson, Callback<Integer> callback, boolean cleanupTest, boolean forceIntoQueue) {
    IngestRequest request = new IngestRequest(who, requestJson, callback, cleanupTest);
    if (catastrophicFailureOccurred) {
      request.callback.failure(new ErrorCodeException(ErrorCodes.CATASTROPHIC_DOCUMENT_FAILURE_EXCEPTION));
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
    ingest(NtClient.NO_ONE, forgeInvalidate(), base.metrics.document_invalidate.wrap(callback), false, true);
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

  public void expire(long limit, Callback<Integer> callback) {
    final var request = forge("expire", null);
    request.writeObjectFieldIntro("limit");
    request.writeLong(limit);
    request.endObject();
    ingest(NtClient.NO_ONE, request.toString(), base.metrics.document_expire.wrap(callback), true, false);
  }

  public void connect(final NtClient who, Callback<Integer> callback) {
    final var request = forge("connect", who);
    request.endObject();
    ingest(who, request.toString(), base.metrics.document_connect.wrap(callback), false, false);
  }

  public boolean isConnected(final NtClient who) {
    return document.__isConnected(who);
  }

  public void createPrivateView(final NtClient who, final Perspective perspective, JsonStreamReader viewerState, AssetIdEncoder encoder, Callback<PrivateView> callback) {
    PrivateView result = document.__createView(who, perspective, encoder);
    result.ingest(viewerState);
    invalidate(Callback.transform(callback, ErrorCodes.DURABLE_LIVING_DOCUMENT_STAGE_ATTACH_PRIVATE_VIEW, (seq) -> result));
  }

  public int garbageCollectPrivateViewsFor(final NtClient who) {
    return document.__garbageCollectViews(who);
  }

  public void scheduleCleanup() {
    base.executor.schedule(new NamedRunnable("document-cleanup") {
      @Override
      public void execute() throws Exception {
        if (document.__canRemoveFromMemory()) {
          base.map.remove(key);
          base.metrics.inflight_documents.down();
        }
      }
    }, base.getMillisecondsForCleanupCheck());
  }

  public void disconnect(final NtClient who, Callback<Integer> callback) {
    final var request = forge("disconnect", who);
    request.endObject();
    ingest(who, request.toString(), base.metrics.document_disconnect.wrap(callback), true, false);
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
    ingest(context.who, writer.toString(), base.metrics.document_send.wrap(callback), false, false);
  }

  public void apply(NtClient who, String patch, Callback<Integer> callback) {
    final var writer = forge("apply", who);
    writer.writeObjectFieldIntro("patch");
    writer.injectJson(patch);
    writer.endObject();
    ingest(who, writer.toString(), base.metrics.document_apply.wrap(callback), false, false);
  }

  public boolean canAttach(NtClient who) {
    // TODO: check policy first
    return document.__onCanAssetAttached(who);
  }

  public void attach(NtClient who, NtAsset asset, Callback<Integer> callback) {
    final var writer = forge("attach", who);
    writer.writeObjectFieldIntro("asset");
    writer.writeNtAsset(asset);
    writer.endObject();
    ingest(who, writer.toString(), base.metrics.document_attach.wrap(callback), false, false);
  }

  public String json() {
    final var writer = new JsonStreamWriter();
    document.__dump(writer);
    return writer.toString();
  }

  public void reconcileClients() {
    for (NtClient client : document.__reconcileClientsToForceDisconnect()) {
      disconnect(client, Callback.DONT_CARE_INTEGER);
    }
  }

  private static class IngestRequest {
    public final boolean cleanupTest;
    private final NtClient who;
    private final String request;
    private final Callback<Integer> callback;
    private int attempts;

    private IngestRequest(NtClient who, String request, Callback<Integer> callback, boolean cleanup) {
      this.who = who;
      this.request = request;
      this.callback = callback;
      this.attempts = 0;
      this.cleanupTest = cleanup;
    }

    public boolean tryAgain() {
      return attempts++ < 5;
    }
  }
}
