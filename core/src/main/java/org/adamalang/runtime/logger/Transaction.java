/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.logger;

/** a single transaction of a request producing a delta */
@Deprecated
public class Transaction {
  public final String forwardDelta;
  public final String reverseDelta;
  public final String request;
  public final int seq;
  public final TransactionResult transactionResult;

  public Transaction(final int seq, final String request, final String forwardDelta, final String reverseDelta,  final TransactionResult transactionResult) {
    this.seq = seq;
    this.request = request;
    this.forwardDelta = forwardDelta;
    this.reverseDelta = reverseDelta;
    this.transactionResult = transactionResult;
  }
}
