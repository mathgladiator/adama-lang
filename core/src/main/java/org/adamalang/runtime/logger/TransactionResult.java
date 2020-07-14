/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.logger;

import org.adamalang.runtime.bridges.NativeBridge;
import com.fasterxml.jackson.databind.node.ObjectNode;


/** the result of a transaction */
public class TransactionResult {
  public static TransactionResult from(final ObjectNode node) {
    final boolean needsInvalidation = NativeBridge.BOOLEAN_NATIVE_SUPPORT.readFromMessageObject(node, "needsInvalidation");
    final int whenToInvalidMilliseconds = NativeBridge.INTEGER_NATIVE_SUPPORT.readFromMessageObject(node, "whenToInvalidMilliseconds");
    final int seq = NativeBridge.INTEGER_NATIVE_SUPPORT.readFromMessageObject(node, "seq");
    return new TransactionResult(needsInvalidation, whenToInvalidMilliseconds, seq);
  }

  public final boolean needsInvalidation;
  public final int whenToInvalidMilliseconds;
  public final int seq;

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
