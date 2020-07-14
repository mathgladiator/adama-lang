/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.logger;

import org.adamalang.runtime.LivingDocument;
import org.adamalang.runtime.contracts.DocumentMonitor;
import org.adamalang.runtime.contracts.TransactionLogger;
import org.adamalang.runtime.contracts.TimeSource;
import org.adamalang.runtime.exceptions.DocumentRequestRejectedException;
import org.adamalang.runtime.exceptions.DocumentRequestRejectedReason;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.stdlib.Utility;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Transactor {
  public LivingDocument document;
  public final LivingDocumentFactory factory;
  public final TransactionLogger logger;
  public final DocumentMonitor monitor;
  public final TimeSource time;

  public Transactor(final LivingDocumentFactory factory, final DocumentMonitor monitor, final TimeSource time, final TransactionLogger logger) {
    this.factory = factory;
    this.monitor = monitor;
    this.time = time;
    this.logger = logger;
    document = null;
  }

  public synchronized TransactionResult bill() throws Exception {
    final var request = forge("bill", null);
    final var transaction = document.__transact(request);
    logger.ingest(transaction);
    return transaction.transactionResult;
  }

  public synchronized void close() throws Exception {
    logger.close();
  }

  public synchronized TransactionResult connect(final NtClient who) throws Exception {
    final var request = forge("connect", who);
    final var transaction = document.__transact(request);
    logger.ingest(transaction);
    return transaction.transactionResult;
  }

  public synchronized TransactionResult construct(final NtClient who, final ObjectNode arg, final String entropy) throws Exception {
    if (document != null) { throw new DocumentRequestRejectedException(DocumentRequestRejectedReason.AlreadyConstructed); }
    final var root = Utility.createObjectNode();
    if (entropy != null) {
      root.put("__entropy", entropy);
    }
    document = factory.create(root, monitor);
    final var request = forge("construct", who);
    request.set("arg", arg);
    final var transaction = document.__transact(request);
    if (entropy != null) {
      transaction.delta.put("__entropy", entropy);
    }
    logger.ingest(transaction);
    return transaction.transactionResult;
  }

  public synchronized TransactionResult disconnect(final NtClient who) throws Exception {
    final var request = forge("disconnect", who);
    final var transaction = document.__transact(request);
    logger.ingest(transaction);
    return transaction.transactionResult;
  }

  public ObjectNode forge(final String command, final NtClient who) {
    final var request = Utility.createObjectNode();
    request.put("command", command);
    request.put("timestamp", Long.toString(time.nowMilliseconds()));
    if (who != null) {
      who.dump(request.putObject("who"));
    }
    return request;
  }

  public synchronized ObjectNode getView(final NtClient who) {
    return document.__getView(who);
  }

  public synchronized TransactionResult invalidate() throws Exception {
    final var request = forge("invalidate", null);
    final var transaction = document.__transact(request);
    logger.ingest(transaction);
    return transaction.transactionResult;
  }

  /** this suggests that the document should not do anything on setup */
  public synchronized void seed(final ObjectNode node) throws Exception {
    document = factory.create(node, monitor);
  }

  public synchronized TransactionResult send(final NtClient who, final String channel, final ObjectNode message) throws Exception {
    final var request = forge("send", who);
    request.put("channel", channel);
    request.set("message", message);
    final var transaction = document.__transact(request);
    logger.ingest(transaction);
    return transaction.transactionResult;
  }
}
