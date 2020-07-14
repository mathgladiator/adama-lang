/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.logger;

import com.fasterxml.jackson.databind.node.ObjectNode;

/** a single transaction of a request producing a delta */
public class Transaction {
  public final ObjectNode delta;
  public final TransactionResult transactionResult;
  public final ObjectNode request;
  public final int seq;

  public Transaction(final int seq, final ObjectNode request, final ObjectNode delta, final TransactionResult transactionResult) {
    this.seq = seq;
    this.request = request;
    this.delta = delta;
    this.transactionResult = transactionResult;
  }
}
