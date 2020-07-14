/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.api;

import java.util.HashMap;
import java.util.UUID;
import org.adamalang.runtime.contracts.ApiHandler;
import org.adamalang.runtime.contracts.ApiResponder;
import org.adamalang.runtime.exceptions.ApiErrorReason;
import org.adamalang.runtime.exceptions.DocumentRequestRejectedException;
import org.adamalang.runtime.stdlib.Utility;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** The central handler for converting any protocol into the Adama API */
public class CoreHandler implements ApiHandler {
  /** debate: the entropy on document construction is a controllable */
  private String extractOptionalEntropy(final HashMap<String, QueryVariant> query) {
    final var result = query.get("entropy");
    if (result != null) { return result.string_value; }
    return null;
  }

  @Override
  public void handle(final AdamaSession session, final ApiMethod method, final GameSpace gamespace, final String id, final HashMap<String, QueryVariant> query, final ObjectNode data, final ApiResponder responder) {
    try {
      if (method == ApiMethod.Get) {
        handleGet(session, gamespace, id, query, data, responder);
      } else if (method == ApiMethod.Post) {
        handlePost(session, gamespace, id, query, data, responder);
      }
    } catch (final DocumentRequestRejectedException drre) {
      responder.error(drre.reason.publicReason);
    } catch (final Exception e) {
      responder.error(ApiErrorReason.InternalIssue);
    }
  }

  /** get, getAndSubscribe */
  private void handleGet(final AdamaSession session, final GameSpace gamespace, final String id, final HashMap<String, QueryVariant> query, final ObjectNode data, final ApiResponder responder) throws Exception {
    if (mustHaveIdCallMustExitIfTrue(id, responder)) { return; }
    final var stateMachine = gamespace.get(id);
    if (stateMachine == null) {
      responder.error(ApiErrorReason.GameNotFound);
      return;
    }
    if (hasBooleanQueryFlagRaised(query, "subscribe")) {
      stateMachine.getAndSubscribe(session.who, responder);
    } else {
      stateMachine.get(session.who, responder);
    }
  }

  /** create, send */
  private void handlePost(final AdamaSession session, final GameSpace gamespace, final String id, final HashMap<String, QueryVariant> query, final ObjectNode data, final ApiResponder responder) throws Exception {
    if (id == null) {
      final var newId = UUID.randomUUID().toString();
      gamespace.create(newId, session.who, data, extractOptionalEntropy(query));
      final var response = Utility.createObjectNode();
      response.put("id", newId);
      responder.respond(response, true);
      return;
    } else {
      final var channel = query.get("channel");
      if (channel == null) {
        responder.error(ApiErrorReason.NoChannelSpecified);
        return;
      }
      final var sm = gamespace.get(id);
      if (sm == null) {
        responder.error(ApiErrorReason.GameNotFound);
        return;
      }
      int seq = sm.send(session.who, channel.string_value, data);
      ObjectNode response = Utility.createObjectNode();
      response.put("seq", seq);
      responder.respond(response, true);
    }
  }

  /** helper: does the query have the given boolean flag */
  private boolean hasBooleanQueryFlagRaised(final HashMap<String, QueryVariant> query, final String name) {
    final var result = query.get(name);
    if (result != null) { return result.bool_value; }
    return false;
  }

  /** helper: validate the id; caller must exit if this returns true */
  private boolean mustHaveIdCallMustExitIfTrue(final String id, final ApiResponder responder) {
    if (id == null) {
      responder.error(ApiErrorReason.NoGameIdSpecified);
      return true;
    }
    return false;
  }
}
