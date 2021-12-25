package org.adamalang.api;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.web.io.*;

public class ReflectionResponder {
  public final JsonResponder responder;

  public ReflectionResponder(JsonResponder responder) {
    this.responder = responder;
  }

  public void complete(ObjectNode reflection) {
    ObjectNode _obj = new JsonMapper().createObjectNode();
    _obj.set("reflection", reflection);
    responder.finish(_obj.toString());
  }

  public void error(ErrorCodeException ex) {
    responder.error(ex);
  }
}
