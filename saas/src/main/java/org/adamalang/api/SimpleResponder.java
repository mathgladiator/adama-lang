package org.adamalang.api;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.io.*;

public class SimpleResponder {
  public final JsonResponder responder;

  public SimpleResponder(JsonResponder responder) {
    this.responder = responder;
  }

  public void complete() {
    ObjectNode _obj = new JsonMapper().createObjectNode();
    responder.finish(_obj.toString());
  }

  public void error(ErrorCodeException ex) {
    responder.error(ex);
  }
}
