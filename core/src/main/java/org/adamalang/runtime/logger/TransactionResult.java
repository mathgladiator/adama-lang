/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.logger;

import com.fasterxml.jackson.databind.node.ObjectNode;

/** the result of a transaction */
public class TransactionResult {
  public static TransactionResult from(final ObjectNode node) {
    final var needsInvalidationNode = node.get("needsInvalidation");
    final var whenToInvalidMillisecondsNode = node.get("whenToInvalidMilliseconds");
    final var seqNode = node.get("seq");
    final var needsInvalidation = needsInvalidationNode != null && needsInvalidationNode.isBoolean() ? needsInvalidationNode.asBoolean() : false;
    final var whenToInvalidMilliseconds = whenToInvalidMillisecondsNode != null && whenToInvalidMillisecondsNode.isIntegralNumber() ? whenToInvalidMillisecondsNode.asInt() : 0;
    final var seq = seqNode != null && seqNode.isIntegralNumber() ? seqNode.asInt() : 0;
    return new TransactionResult(needsInvalidation, whenToInvalidMilliseconds, seq);
  }

  public final boolean needsInvalidation;
  public final int seq;
  public final int whenToInvalidMilliseconds;

  public TransactionResult(final boolean needsInvalidation, final int whenToInvalidMilliseconds, final int seq) {
    this.needsInvalidation = needsInvalidation;
    this.whenToInvalidMilliseconds = whenToInvalidMilliseconds;
    this.seq = seq;
  }

  public void dumpInto(final ObjectNode node) {
    node.put("needsInvalidation", needsInvalidation);
    node.put("whenToInvalidMilliseconds", whenToInvalidMilliseconds);
    node.put("seq", seq);
  }
}
