/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import org.adamalang.runtime.stdlib.Utility;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** used by SynchronousJsonDeltaDiskLogger to de-serialize a Transaction. This
 * will pull the record off a buffered reader */
@Deprecated
class NewlineJsonTransactionDiskRecord {
  public static void writeTo(final Transaction transaction, final PrintWriter writer) {
    writer.println(transaction.request);
    writer.println(transaction.forwardDelta);
    writer.println(transaction.reverseDelta);
    final var next = Utility.createObjectNode();
    transaction.transactionResult.dumpInto(next);
    writer.println(next.toString());
  }

  public final String forwardDelta;
  public final String reverseDelta;
  public final String metadata;
  public final ObjectNode metaObject;
  public final String request;

  public NewlineJsonTransactionDiskRecord(final BufferedReader buffered) throws IOException {
    request = buffered.readLine();
    if (request == null) {
      forwardDelta = null;
      reverseDelta = null;
      metadata = null;
      metaObject = null;
    } else {
      forwardDelta = buffered.readLine();
      if (forwardDelta == null) { throw new IOException("incomplete record, missing forward delta"); }
      reverseDelta = buffered.readLine();
      if (reverseDelta == null) { throw new IOException("incomplete record, missing reverse delta"); }
      metadata = buffered.readLine();
      if (metadata == null) { throw new IOException("incomplete record, missing metadata"); }
      try {
        metaObject = Utility.parseJsonObjectThrows(metadata);
      } catch (final Exception re) {
        throw new IOException(re);
      }
    }
  }

  public Transaction toTransaction() {
    return new Transaction(-1, request, forwardDelta, reverseDelta, TransactionResult.from(metaObject));
  }

  public boolean valid() {
    return request != null;
  }
}
