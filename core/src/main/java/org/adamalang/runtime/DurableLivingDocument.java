/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime;

import org.adamalang.runtime.contracts.*;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateView;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.translator.jvm.LivingDocumentFactory;

/** A LivingDocument tied to a document id and DataService */
public class DurableLivingDocument {
  public final long documentId;
  public final LivingDocument document;
  public final TimeSource time;
  private final DataService service;
  private Integer requiresInvalidateMilliseconds;

  private DurableLivingDocument(final long documentId, final LivingDocument document, final TimeSource time, final DataService service) {
    this.documentId = documentId;
    this.document = document;
    this.time = time;
    this.service = service;
    this.requiresInvalidateMilliseconds = null;
  }

  public Integer getAndCleanRequiresInvalidateMilliseconds() {
    Integer result = requiresInvalidateMilliseconds;
    requiresInvalidateMilliseconds = null;
    return result;
  }

  public static void fresh(
          final long documentId,
          final LivingDocumentFactory factory,
          final NtClient who,
          final String arg,
          final String entropy,
          final DocumentMonitor monitor,
          final TimeSource time,
          final DataService service,
          final DataCallback<DurableLivingDocument> callback) {
    try {
      DurableLivingDocument document = new DurableLivingDocument(documentId, factory.create(monitor), time, service);
      document.construct(who, arg, entropy, DataCallback.transform(callback, ErrorCodes.E1_DURABLE_LIVING_DOCUMENT_STAGE_FRESH_TRANSFORM, (seq) -> document));
    } catch (Throwable ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.E1_DURABLE_LIVING_DOCUMENT_STAGE_FRESH_DRIVE, ex));
    }
  }

  public static void load(
          long documentId,
          final LivingDocumentFactory factory,
          final DocumentMonitor monitor,
          final TimeSource time,
          final DataService service,
          final DataCallback<DurableLivingDocument> callback) {
    try {
      LivingDocument doc = factory.create(monitor);
      service.get(documentId, DataCallback.transform(callback, ErrorCodes.E1_DURABLE_LIVING_DOCUMENT_STAGE_PARSE, (data) -> {
          doc.__insert(new JsonStreamReader(data.patch));
          JsonStreamWriter writer = new JsonStreamWriter();
          doc.__dump(writer);
          return new DurableLivingDocument(documentId, doc, time, service);
      }));
    } catch (Throwable ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.E1_DURABLE_LIVING_DOCUMENT_STAGE_LOAD, ex));
    }
  }

  public JsonStreamWriter forge(final String command, final NtClient who) {
    final var writer = new JsonStreamWriter();
    writer.beginObject();
    writer.writeObjectFieldIntro("command");
    writer.writeFastString(command);
    writer.writeObjectFieldIntro("timestamp");
    writer.writeLong(time.nowMilliseconds());
    if (who != null) {
      writer.writeObjectFieldIntro("who");
      writer.writeNtClient(who);
    }
    return writer;
  }

  private void ingest(String request, DataCallback<Integer> callback) {
    try {
      final var update = document.__transact(request);
      if (update.requiresFutureInvalidation && update.whenToInvalidateMilliseconds == 0) {
        service.patch(documentId, update, DataCallback.handoff(callback, ErrorCodes.E1_DURABLE_LIVING_DOCUMENT_STAGE_INGEST_PARTIAL, () -> {
          invalidate(callback);
        }));
      } else {
        if (update.requiresFutureInvalidation) {
          this.requiresInvalidateMilliseconds = update.whenToInvalidateMilliseconds;
        }
        service.patch(documentId, update, DataCallback.transform(callback, ErrorCodes.E1_DURABLE_LIVING_DOCUMENT_STAGE_INGEST_DONE, (v) -> update.seq));
      }
    } catch (Throwable ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.E1_DURABLE_LIVING_DOCUMENT_STAGE_INGEST_DRIVE, ex));
    }
  }

  private void construct(final NtClient who, final String arg, final String entropy, DataCallback<Integer> callback) {
    try {
      final var writer = forge("construct", who);
      writer.writeObjectFieldIntro("arg");
      writer.injectJson(arg);
      if (entropy != null) {
        writer.writeObjectFieldIntro("entropy");
        writer.writeFastString(entropy);
      }
      writer.endObject();
      final var update = document.__transact(writer.toString());
      service.initialize(documentId, update, DataCallback.handoff(callback, ErrorCodes.E1_DURABLE_LIVING_DOCUMENT_STAGE_CONSTRUCT_DONE, () -> {
        invalidate(callback);
      }));
    } catch (Throwable ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.E1_DURABLE_LIVING_DOCUMENT_STAGE_CONSTRUCT_DRIVE, ex));
    }
  }

  public void invalidate(DataCallback<Integer> callback) {
    final var request = forge("invalidate", null);
    request.endObject();
    ingest(request.toString(), callback);
  }

  public int getCodeCost() {
    return document.__getCodeCost();
  }

  public void bill(DataCallback<Integer> callback) {
    final var request = forge("bill", null);
    request.endObject();
    ingest(request.toString(), callback);
  }

  public void connect(final NtClient who, DataCallback<Integer> callback) {
    final var request = forge("connect", who);
    request.endObject();
    ingest(request.toString(), callback);
  }

  public boolean isConnected(final NtClient who) {
    return document.__isConnected(who);
  }

  public void createPrivateView(final NtClient who, final Perspective perspective, DataCallback<PrivateView> callback) {
    PrivateView result = document.__createView(who, perspective);
    invalidate(DataCallback.transform(callback, ErrorCodes.E1_DURABLE_LIVING_DOCUMENT_STAGE_ATTACH_PRIVATE_VIEW, (seq) -> result));
  }

  public int garbageCollectPrivateViewsFor(final NtClient who) {
    return document.__garbageCollectViews(who);
  }

  public void disconnect(final NtClient who, DataCallback<Integer> callback) {
    final var request = forge("disconnect", who);
    request.endObject();
    ingest(request.toString(), callback);
  }

  public void send(final NtClient who, final String channel, final String message, DataCallback<Integer> callback) {
    final var writer = forge("send", who);
    writer.writeObjectFieldIntro("channel");
    writer.writeFastString(channel);
    writer.writeObjectFieldIntro("message");
    writer.injectJson(message);
    writer.endObject();
    ingest(writer.toString(), callback);
  }

  public String json() {
    final var writer = new JsonStreamWriter();
    document.__dump(writer);
    return writer.toString();
  }
}
