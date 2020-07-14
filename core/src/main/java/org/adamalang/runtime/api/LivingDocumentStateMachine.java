/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.api;

import java.util.LinkedHashMap;
import java.util.Map;
import org.adamalang.runtime.contracts.ApiResponder;
import org.adamalang.runtime.logger.TransactionResult;
import org.adamalang.runtime.logger.Transactor;
import org.adamalang.runtime.natives.NtClient;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** this is a proxy for the Transactor. The value-add of this layer is the ability to update clients directly. */
// TODO: this is not thread safe, need to revisit the threading model (or, well... define it)
public class LivingDocumentStateMachine {
  private final LinkedHashMap<NtClient, ApiResponder> responders;
  private final Transactor transactor;

  public LivingDocumentStateMachine(final Transactor transactor) {
    this.transactor = transactor;
    responders = new LinkedHashMap<>();
  }

  /** helper: broadcast client updates to all */
  private void broadcast() {
    for (final Map.Entry<NtClient, ApiResponder> entry : responders.entrySet()) {
      entry.getValue().respond(transactor.getView(entry.getKey()), false);
    }
  }

  /** close the transactor */
  public void close() throws Exception {
    transactor.close();
  }

  /** drive the state machine */
  private void drive(final TransactionResult initial) throws Exception {
    var next = initial;
    while (next.needsInvalidation && next.whenToInvalidMilliseconds == 0) {
      next = transactor.invalidate();
    }
  }

  /** api: get an instance of the document */
  public void get(final NtClient who, final ApiResponder responder) throws Exception {
    drive(transactor.connect(who));
    final var result = transactor.getView(who);
    drive(transactor.disconnect(who));
    responder.respond(result, true);
  }

  /** api: get a stream of the document */
  public void getAndSubscribe(final NtClient who, final ApiResponder responder) throws Exception {
    drive(transactor.connect(who));
    responders.put(who, responder);
    broadcast();
  }

  /** get the number of responders */
  public int getResponderCount() {
    return responders.size();
  }

  /** a client leaves */
  public void leave(final NtClient who) throws Exception {
    responders.remove(who);
    drive(transactor.disconnect(who));
    broadcast();
  }

  /** a client sends a message */
  public int send(final NtClient who, final String channel, final ObjectNode message) throws Exception {
    TransactionResult initial = transactor.send(who, channel, message);
    drive(initial);
    broadcast();
    return initial.seq;
  }
}
