/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.logger;

import org.adamalang.runtime.LivingDocument;
import org.adamalang.runtime.contracts.*;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateView;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.translator.jvm.LivingDocumentFactory;

/** this an exceptionally low-level class and provides the primitives to build
 * an API. */
@Deprecated
public class Transactor {
  public LivingDocument document;
  public final LivingDocumentFactory factory;
  private final TransactionLogger logger;
  public final DocumentMonitor monitor;
  public final TimeSource time;
  private final DumbDataServiceBridge bridge;
  private final long documentId;

  public Transactor(final LivingDocumentFactory factory, final DocumentMonitor monitor, final TimeSource time, final TransactionLogger logger) {
    this.factory = factory;
    this.monitor = monitor;
    this.time = time;
    this.logger = logger;
    document = null;
    this.bridge = new DumbDataServiceBridge(logger);
    this.documentId = 0L;
  }

  private TransactionResult ingest(DataService.RemoteDocumentUpdate update) {
    bridge.patch(documentId, update, new DataCallback<Void>() {
      @Override
      public void success(Void value) {

      }

      @Override
      public void progress(int stage) {

      }

      @Override
      public void failure(int stage, Exception ex) {

      }
    });
    return new TransactionResult(update.requiresFutureInvalidation, update.whenToInvalidateMilliseconds, update.seq);
  }

  /** log a bill, reset goodwill and cost */
  public TransactionResult bill() throws ErrorCodeException {
    final var request = forge("bill", null);
    request.endObject();
    final var update = document.__transact(request.toString());
    return ingest(update);
  }

  public void close() throws Exception {
    logger.close();
  }

  /** connect a client */
  public TransactionResult connect(final NtClient who) throws ErrorCodeException {
    final var request = forge("connect", who);
    request.endObject();
    final var update = document.__transact(request.toString());
    return ingest(update);
  }

  /** construct the document */
  public TransactionResult construct(final NtClient who, final String arg, final String entropy) throws ErrorCodeException {
    if (document != null) { throw new ErrorCodeException(ErrorCodeException.TRANSACTOR_CANT_CREATE_BECAUSE_ALREADY_CREATED); }
    document = factory.create(monitor);
    final var writer = forge("construct", who);
    writer.writeObjectFieldIntro("arg");
    writer.injectJson(arg);
    if (entropy != null) {
      writer.writeObjectFieldIntro("entropy");
      writer.writeFastString(entropy);
    }
    writer.endObject();
    final var update = document.__transact(writer.toString());
    return ingest(update);
  }

  /** this suggests that the document should not do anything on setup */
  public void create() throws ErrorCodeException {
    document = factory.create(monitor);
  }

  /** construct a private view */
  public PrivateView createView(final NtClient who, final Perspective perspective) {
    return document.__createView(who, perspective);
  }

  /** disconnect the client */
  public void disconnect(final NtClient who) {
    try {
      final var request = forge("disconnect", who);
      request.endObject();
      final var update = document.__transact(request.toString());
      ingest(update);
    } catch (final ErrorCodeException ece) {
      // ignored because the failure mode is primarily they were not connected, and
      // this is not useful to consumers
    }
  }

  /** drive changes */
  public TransactionResult drive() throws ErrorCodeException {
    var initial = invalidate();
    while (initial.needsInvalidation && initial.whenToInvalidMilliseconds == 0) {
      initial = invalidate();
    }
    return initial;
  }

  /** forge a new request */
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

  /** garbage collect views for the given person */
  public int gcViewsFor(final NtClient who) {
    return document.__garbageCollectViews(who);
  }

  /** get how much the document has cost */
  public int getCodeCost() {
    return document.__getCodeCost();
  }

  public void insert(final String json) {
    document.__insert(new JsonStreamReader(json));
  }

  /** drive a single change */
  public TransactionResult invalidate() throws ErrorCodeException {
    final var request = forge("invalidate", null);
    request.endObject();
    final var update = document.__transact(request.toString());
    return ingest(update);
  }

  /** is the given user connected */
  public boolean isConnected(final NtClient who) {
    return document.__isConnected(who);
  }

  /** dump a json */
  public String json() {
    final var writer = new JsonStreamWriter();
    document.__dump(writer);
    return writer.toString();
  }

  /** send a message from a person to a channel */
  public TransactionResult send(final NtClient who, final String channel, final String message) throws ErrorCodeException {
    final var writer = forge("send", who);
    writer.writeObjectFieldIntro("channel");
    writer.writeFastString(channel);
    writer.writeObjectFieldIntro("message");
    writer.injectJson(message);
    writer.endObject();
    final var update = document.__transact(writer.toString());
    return ingest(update);
  }
}
