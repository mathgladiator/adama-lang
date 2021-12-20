package org.adamalang.api;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.web.io.*;

public class DataResponder {
  public final JsonResponder responder;

  public DataResponder(JsonResponder responder) {
    this.responder = responder;
  }

  public void next(ObjectNode delta) {
    ObjectNode _obj = new JsonMapper().createObjectNode();
    if (delta != null) {
      _obj.set("delta", delta);
    }
    responder.stream(_obj.toString());
  }

  public void finish(ObjectNode delta) {
    ObjectNode _obj = new JsonMapper().createObjectNode();
    if (delta != null) {
      _obj.set("delta", delta);
    }
    responder.finish(_obj.toString());
  }

  public void finish() {
    responder.finish("{}");
  }

  public void error(ErrorCodeException ex) {
    responder.error(ex);
  }
}
