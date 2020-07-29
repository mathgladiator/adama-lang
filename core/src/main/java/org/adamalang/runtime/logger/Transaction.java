/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.logger;

/** a single transaction of a request producing a delta */
public class Transaction {
  public final String delta;
  public final String request;
  public final int seq;
  public final TransactionResult transactionResult;

  public Transaction(final int seq, final String request, final String delta, final TransactionResult transactionResult) {
    this.seq = seq;
    this.request = request;
    this.delta = delta;
    this.transactionResult = transactionResult;
  }
}
