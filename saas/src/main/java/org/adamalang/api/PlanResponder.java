package org.adamalang.api;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.web.io.*;

public class PlanResponder {
  public final JsonResponder responder;

  public PlanResponder(JsonResponder responder) {
    this.responder = responder;
  }

  public void complete(ObjectNode plan) {
    ObjectNode _obj = new JsonMapper().createObjectNode();
    _obj.set("plan", plan);
    responder.finish(_obj.toString());
  }

  public void error(ErrorCodeException ex) {
    responder.error(ex);
  }
}
