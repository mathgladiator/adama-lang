/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.server;

import java.util.HashMap;
import org.adamalang.netty.api.AdamaSession;
import org.adamalang.netty.client.AdamaCookieCodec;
import org.adamalang.netty.contracts.JsonHandler;
import org.adamalang.netty.contracts.JsonResponder;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.runtime.stdlib.Utility;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class MockJsonHandler implements JsonHandler {
  @Override
  public void handle(final AdamaSession session, final ObjectNode request, final JsonResponder responder) throws ErrorCodeException {
    if (request.has("crash")) { throw new RuntimeException("w00t"); }
    if (request.has("error")) { throw new ErrorCodeException(13); }
    if (session == null && request.has("auth")) {
      final var httpHeaders = new HashMap<String, String>();
      httpHeaders.put("Set-Cookie", AdamaCookieCodec.server(new CliServerOptions(), "x", "y"));
      responder.respond("{\"ok\":\"auth\"}", true, httpHeaders);
      return;
    }
    if (session != null && request.has("auth")) {
      responder.respond("{\"ok\":\"authgood\"}", true, null);
      return;
    }
    if (request.has("success")) {
      responder.respond("{\"ok\":\"go\"}", true, null);
    } else {
      if (session == null) {
        responder.failure(new ErrorCodeException(12, new RuntimeException("")));
      } else {
        responder.failure(new ErrorCodeException(400, new RuntimeException("")));
      }
    }
  }

  @Override
  public void shutdown() {
  }
}
