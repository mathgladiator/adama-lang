package org.adamalang.api;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.io.*;

public class InitiationResponder {
  public final JsonResponder responder;

  public InitiationResponder(JsonResponder responder) {
    this.responder = responder;
  }

  public void complete(String identity) {
    ObjectNode _obj = new JsonMapper().createObjectNode();
    _obj.put("identity", identity);
    responder.finish(_obj.toString());
  }

  public void error(ErrorCodeException ex) {
    responder.error(ex);
  }
}
