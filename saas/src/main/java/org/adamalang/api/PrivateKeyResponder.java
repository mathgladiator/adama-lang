package org.adamalang.api;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.adamalang.web.io.*;

public class PrivateKeyResponder {
  public final JsonResponder responder;

  public PrivateKeyResponder(JsonResponder responder) {
    this.responder = responder;
  }

  public void complete(String privateKey) {
    ObjectNode _obj = new JsonMapper().createObjectNode();
    _obj.put("private-key", privateKey);
    responder.finish(_obj.toString());
  }

  public void error(ErrorCodeException ex) {
    responder.error(ex);
  }
}
